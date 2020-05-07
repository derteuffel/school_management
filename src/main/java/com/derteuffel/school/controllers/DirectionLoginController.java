package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ENiveau;
import com.derteuffel.school.enums.EVisibilite;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.*;
import com.derteuffel.school.services.CompteService;
import com.derteuffel.school.services.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/direction")
public class DirectionLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private CompteService compteService;
    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EleveRepository eleveRepository;
    @Value("${file.upload-dir}")
    private String fileStorage;

    @GetMapping("/login")
    public String director() {
        return "direction/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        System.out.println("je suis deconnectee");
        return "redirect:/direction/login";
    }
    @ModelAttribute("compte")
    public CompteRegistrationDto compteRegistrationDto() {
        return new CompteRegistrationDto();
    }

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        List<Ecole> ecoles = ecoleRepository.findAllByStatus(false, Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("lists", ecoles);
        return "direction/registration";
    }

    @GetMapping("/registration/root")
    public String registrationRoot(Model model) {
        return "direction/rootRegistration";
    }


    @PostMapping("/registration")
    public String registrationDirectionSave(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                            BindingResult result, RedirectAttributes redirectAttributes, Model model, String ecole) {

        Compte existAccount = compteService.findByUsername(compteDto.getUsername());
        Ecole ecole1 = ecoleRepository.findByCode(ecole);
        if (existAccount != null) {
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            model.addAttribute("error", "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "direction/registration";
        }

        if (ecole1 != null) {
            if (compteRepository.findAllByEcole_Id(ecole1.getId()).size() > 0) {
                model.addAttribute("error", "Cet Etablissement a deja un dirigeant veuillez choisir celui que vous avez creer");
                return "direction/registration";
            }
            compteService.save(compteDto, "/images/profile.jpeg", ecole1.getId());
            Mail sender = new Mail();
            sender.sender(
                    "confirmation@yesbanana.org",
                    "Enregistrement d'un directeur ou responsable",
                    "Viens de s'enregistrer comme directeur de l'ecole :" + ecole1.getName() + " de " + ecole1.getProvince());

        } else {
            model.addAttribute("error", "Aucune ecole n'est enregistrer avec ce code, veillez contacter l'administrateur sur info@yesbanana.org");
            return "direction/registration";
        }

        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/direction/login";
    }

    @PostMapping("/registration/root")
    public String registrationRoot(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                            BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        Compte existAccount = compteService.findByUsername(compteDto.getUsername());
        if (existAccount != null) {
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            redirectAttributes.addFlashAttribute("error", "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "redirect:/direction/registration/root";
        }


            compteService.saveRoot(compteDto, "/images/profile.jpeg");

        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        request.getSession().setAttribute("compte", compte);
        return "redirect:/direction/ecole/detail/" + ecole.getId();
    }

    @GetMapping("/ecole/detail/{id}")
    public String detail(Model model, @PathVariable Long id, HttpServletRequest request) {
        Ecole ecole = ecoleRepository.getOne(id);
        request.getSession().setAttribute("teacher", new Enseignant());
        request.getSession().setAttribute("ecole", ecole);
        model.addAttribute("teacher", new Enseignant());
        model.addAttribute("message",new Message());
        model.addAttribute("ecole", ecole);
        return "direction/home";
    }

    @GetMapping("/enseignant/lists/{id}")
    public String enseignants(@PathVariable Long id, Model model){
        Ecole ecole = ecoleRepository.getOne(id);
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Collection<Enseignant> enseignants = new ArrayList<>();

        for (Salle salle : salles){
            for (Enseignant enseignant : enseignantRepository.findAllBySalles_Id(salle.getId())){
                if (!(enseignants.contains(enseignant))){
                    enseignants.add(enseignant);
                }else {
                    System.out.println("this enseignant is already present");
                }
            }

        }
        System.out.println(enseignants.size());

        model.addAttribute("lists",enseignants);
        model.addAttribute("ecole",ecole);

        return "direction/enseignants";


    }


    //--- Enseignant management start ----///
    @PostMapping("/enseignant/save")
    public String teacherSave(Enseignant enseignant, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        CompteRegistrationDto compte1 = new CompteRegistrationDto();
        Enseignant enseignant1 = enseignantRepository.findByEmail(enseignant.getEmail());
        if (enseignant1 != null) {
            model.addAttribute("error", "il existe un enseignant deja enregistrer avec cet adresse email");
            return "direction/home";
        }
        compte1.setUsername(enseignant.getName() + "" + compteRepository.findAllByEcole_Id(compte.getEcole().getId()).size());
        compte1.setEmail(enseignant.getEmail());
        compte1.setPassword(enseignant.getName() + "" + compteRepository.findAllByEcole_Id(compte.getEcole().getId()).size());
        compte1.setConfirmPassword(enseignant.getName() + "" + compteRepository.findAllByEcole_Id(compte.getEcole().getId()).size());
        enseignant.setAvatar("/images/profile.jpeg");
        enseignantRepository.save(enseignant);

        compteService.saveEnseignant(compte1, "/images/profile.jpeg", compte.getEcole().getId(), enseignant);
        Mail sender = new Mail();
        sender.sender(
                enseignant.getEmail(),
                "Enregistrement d'un enseignant",
                "vos identifiants : username:" + compte1.getUsername() + " et password : " + compte1.getPassword());

        sender.sender(
                "confirmation@yesbanana.org",
                "Enregistrement d'un enseignant",
                "L'utilisateur " + compte1.getUsername() + " avec l'email :" +
                        compte1.getEmail() + "  Vient d'etre ajouter " +
                        "sur la plateforme de gestion ecoles en ligne. Veuillez vous connectez pour manager son status.");

        redirectAttributes.addFlashAttribute("success", "Vous avez enregistrer avec success ce nouvel enseignant : " + enseignant.getPrenom() + " " + enseignant.getName() + " " + enseignant.getPostnom());
        return "redirect:/direction/enseignant/lists";
    }


    @GetMapping("/enseignant/lists")
    public String teacherLists(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());

        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(compte.getEcole().getId());
        List<Enseignant> enseignants = new ArrayList<>();

        for (Compte compte1 : comptes) {
            if (compte1.getEnseignant() != null) {
                enseignants.add(compte1.getEnseignant());
            }
        }

        model.addAttribute("teacher", new Enseignant());
        model.addAttribute("lists", enseignants);

        return "direction/enseignants/lists";
    }

    @GetMapping("/bibliotheque/lists")
    public String lists(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        List<Livre> alls = new ArrayList<>();
        List<Livre> livres = livreRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        List<Livre> generals = livreRepository.findAllBySalle(ENiveau.GENERALE_PRIMAIRE.toString(),Sort.by(Sort.Direction.DESC,"id"));
        List<Livre> generals1 = livreRepository.findAllBySalle(ENiveau.GENERALE_SECONDAIRE.toString(),Sort.by(Sort.Direction.DESC,"id"));
        livres.addAll(generals);
        livres.addAll(generals1);
        for (int i=0;i<livres.size();i++){
            if (!(i>12)){
                alls.add(livres.get(i));
            }
        }
        model.addAttribute("lists",alls);

        return "direction/bibliotheques";
    }


    @GetMapping("/parent/lists")
    public String parentLists(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());

        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(compte.getEcole().getId());
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(compte.getEcole().getId());
        Collection<Eleve> eleves = new ArrayList<>();
        Collection<Parent> parents = new ArrayList<>();
        for (Salle salle : salles) {
            Collection<Eleve> eleves1= eleveRepository.findAllBySalle_Id(salle.getId());
            eleves.addAll(eleves1);
        }

        for (Eleve eleve : eleves){
            if (!(parents.contains(eleve.getParent()))){
                parents.add(eleve.getParent());
            }
        }

        System.out.println(parents.size());

        model.addAttribute("ecole",compte.getEcole());
        model.addAttribute("lists", parents);

        return "direction/parent/lists";
    }

    @GetMapping("/eleve/lists")
    public String elevesLists(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());

        Collection<Salle> salles = salleRepository.findAllByEcole_Id(compte.getEcole().getId());
        Collection<Eleve> eleves = new ArrayList<>();
        for (Salle salle : salles) {
            Collection<Eleve> eleves1= eleveRepository.findAllBySalle_Id(salle.getId());
            eleves.addAll(eleves1);
        }

        model.addAttribute("ecole",compte.getEcole());
        model.addAttribute("lists", eleves);

        return "direction/eleve/lists";
    }


    @GetMapping("/enseignant/edit/{id}")
    public String enseignantEdit(@PathVariable Long id, Model model) {
        Enseignant enseignant = enseignantRepository.getOne(id);
        model.addAttribute("teacher", enseignant);
        return "direction/enseignants/edit";
    }


    @PostMapping("/enseignant/update")
    public String enseignantUpdate(Enseignant enseignant, @RequestParam("file") MultipartFile file) {

        if (!(file.isEmpty())) {
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            enseignant.setAvatar("/downloadFile/" + file.getOriginalFilename());
        }

        enseignantRepository.save(enseignant);

        return "redirect:/direction/enseignant/lists";
    }

    @GetMapping("/enseignant/delete/{id}")
    public String deleteEnseignant(@PathVariable Long id){
        Collection<Compte> comptes = compteRepository.findAllByEmail(enseignantRepository.getOne(id).getEmail());
        for (Compte compte : comptes){
            compteRepository.delete(compte);
        }
        enseignantRepository.deleteById(id);
        return "redirect:/direction/enseignant/lists";
    }

    // ------ Enseignant management end -----///
    // ------ Classe management start -----///

    @GetMapping("/classe/lists")
    public String classe(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(ecole.getId());
        List<Enseignant> enseignants = new ArrayList<>();
        for (Compte compte1 : comptes) {
            enseignants.add(compte1.getEnseignant());
        }
        model.addAttribute("lists", salleRepository.findAllByEcole_Id(ecole.getId()));
        model.addAttribute("enseignants", enseignants);
        model.addAttribute("salle", new Salle());
        return "direction/classes/lists";
    }


    @PostMapping("/classe/save")
    public String classeSave(Salle salle, Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Enseignant enseignant = enseignantRepository.getOne(id);
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        salle.setEcole(ecole);
        salle.setEnseignants(Arrays.asList(enseignant));
        salle.setPrincipal(enseignant.getName() + "  " + enseignant.getPrenom());

        salleRepository.save(salle);
        enseignant.getSallesIds().add(salle.getId());
        enseignantRepository.save(enseignant);

        redirectAttributes.addFlashAttribute("success", "Vous avez ajoute avec succes une nouvelle classe");
        return "redirect:/direction/classe/lists";
    }

    @GetMapping("/salle/detail/{id}")
    public String classeDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Salle salle = salleRepository.getOne(id);
        Collection<Message> messages = messageRepository.findAllByVisibiliteAndSalleAndEcole(EVisibilite.DIRECTION.toString(), (salle.getNiveau()+""+salle.getId()),ecole.getName(), Sort.by(Sort.Direction.DESC, "id"));
        messages.addAll(messageRepository.findAllByVisibiliteAndSalleAndEcole(EVisibilite.PUBLIC.toString(), (salle.getNiveau()+""+salle.getId()),ecole.getName(), Sort.by(Sort.Direction.DESC, "id")));
        messages.addAll(messageRepository.findAllByVisibiliteAndSalleAndEcole(EVisibilite.ENSEIGNANT.toString(), (salle.getNiveau()+""+salle.getId()),ecole.getName(), Sort.by(Sort.Direction.DESC, "id")));
        messages.addAll(messageRepository.findAllByVisibiliteAndSalleAndEcole(EVisibilite.PARENT.toString(), (salle.getNiveau()+""+salle.getId()),ecole.getName(), Sort.by(Sort.Direction.DESC, "id")));
        Collection<Message> messages1 = messageRepository.findAllByCompte_Id(compte.getId());
        for (Message message : messages1) {
            if (!(messages.contains(message))) {
                messages.add(message);
            }
        }
        model.addAttribute("lists", messages);
        model.addAttribute("message", new Message());
        model.addAttribute("ecole", ecole);
        model.addAttribute("classe", salle);
        return "direction/classes/detail";
    }

    @GetMapping("/classe/add/enseignant/{classeId}")
    public String addTeacherClasse(@PathVariable Long classeId, Long enseignantId, RedirectAttributes redirectAttributes) {

        System.out.println(classeId);
        Salle salle = salleRepository.getOne(classeId);
        Enseignant enseignant = enseignantRepository.getOne(enseignantId);
        enseignant.getSallesIds().add(salle.getId());
        enseignantRepository.save(enseignant);
        salle.getEnseignants().add(enseignant);
        salleRepository.save(salle);
        redirectAttributes.addFlashAttribute("success", "Vous avez ajouter avec succes un nouvel enseignant a cette classe");
        return "redirect:/direction/enseignant/classe/" + salle.getId();

    }


    @GetMapping("/enseignant/classe/{id}")
    public String classeTeachers(Model model, @PathVariable Long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(ecole.getId());
        List<Enseignant> teachers = new ArrayList<>();

        for (Compte compte1 : comptes) {
            if (compte1.getEnseignant() != null && !(comptes.contains(compte1.getEnseignant()))) {
                teachers.add(compte1.getEnseignant());
            }
        }
        Salle salle = salleRepository.getOne(id);

        Collection<Enseignant> enseignants = salle.getEnseignants();

        model.addAttribute("classe", salle);
        model.addAttribute("teachers", teachers);
        model.addAttribute("lists", enseignants);

        return "direction/classes/enseignants";
    }


    //---- Classe management end ----//
    //---- Eleve management start ----//

    @GetMapping("/classe/eleves/{id}")
    public String allEleves(@PathVariable Long id, Model model) {

        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(id);
        model.addAttribute("classe", salleRepository.getOne(id));
        model.addAttribute("student", new Eleve());
        model.addAttribute("lists", eleves);
        return "direction/classes/eleves";
    }

    @PostMapping("/eleves/save/{id}")
    public String save(Eleve eleve, @PathVariable Long id, RedirectAttributes redirectAttributes){

        Parent existParent = parentRepository.findByNomComplet(eleve.getNomCompletTuteur().toUpperCase());
        Salle salle = salleRepository.getOne(id);
        eleve.setSalle(salle);

        if (existParent != null){
            eleve.setParent(existParent);
        }else {
            Parent parent= new Parent();
            CompteRegistrationDto compteRegistrationDto = new CompteRegistrationDto();
            parent.setNomComplet(eleve.getNomCompletTuteur().toUpperCase());
            parent.setEmail(eleve.getEmailTuteur());
            parent.setTelephone(eleve.getTelephoneTuteur().toUpperCase());
            parent.setWhatsapp(eleve.getWhatsappTuteur().toUpperCase());
            compteRegistrationDto.setEmail(parent.getEmail().toLowerCase());
            compteRegistrationDto.setUsername(parent.getNomComplet().toLowerCase());
            compteRegistrationDto.setPassword(compteRegistrationDto.getUsername());
            compteRegistrationDto.setConfirmPassword(compteRegistrationDto.getPassword());
            parentRepository.save(parent);
            compteService.saveParent(compteRegistrationDto,"/images/profile.jpeg",parent);
            eleve.setParent(parent);
            eleveRepository.save(eleve);
            Mail sender = new Mail();
            sender.sender(
                    compteRegistrationDto.getEmail(),
                    "Enregistrement d'un parent",
                    "L'utilisateur " + compteRegistrationDto.getUsername() + " avec mot de passe :" +
                            compteRegistrationDto.getPassword() + "  Vient d'etre ajouter " +
                            "sur la plateforme de gestion ecoles en ligne. Veuillez vous connectez pour manager son status.");

            sender.sender(
                    "confirmation@yesbanana.org",
                    "Enregistrement d'un parent",
                    "L'utilisateur " + compteRegistrationDto.getUsername() + " avec email :" +
                            compteRegistrationDto.getEmail() + "  Vient d'etre ajouter " +
                            "sur la plateforme de gestion ecoles en ligne. Veuillez vous connectez pour manager son status.");



        }



        redirectAttributes.addFlashAttribute("success","Vous avez ajouter avec success un nouvel eleve dans cette classe");
        return "redirect:/direction/classe/eleves/"+salle.getId();
    }


    //---- Eleve management end -----//
    //---- Parent management start -----//

    @GetMapping("/classe/parents/{id}")
    public String allParents(@PathVariable Long id, Model model) {
        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(id);
        Collection<Parent> parents = new ArrayList<>();

        for (Eleve eleve : eleves) {
            parents.add(eleve.getParent());
        }

        model.addAttribute("classe", salleRepository.getOne(id));

        model.addAttribute("lists", parents);
        return "direction/classes/parents";
    }


    @GetMapping("/access-denied")
    public String access_denied() {
        return "direction/access-denied";
    }

    @PostMapping("/message/save/{id}")
    public String saveMessage(Message message, @RequestParam("file") MultipartFile file, @PathVariable Long id, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Salle salle = salleRepository.getOne(id);
        message.setCompte(compte);
        message.setSender(compte.getUsername());
        message.setSalle(salle.getNiveau() + "" + salle.getId());
        message.setEcole(compte.getEcole().getName());
        message.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date()));
        message.setVisibilite(message.getVisibilite().toString());
        if (!(file.isEmpty())) {
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message.setFichier("/downloadFile/" + file.getOriginalFilename());
        }
        messageRepository.save(message);
        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(compte.getEcole().getId());

        Mail sender = new Mail();
        sender.sender(
                compte.getEmail(),
                "Envoi d'un message",
                "Message de la direction ---> "+message.getContent()+", envoye le "+message.getDate()+", fichier associe(s) "+message.getFichier()+"avec un visibilite ----> "+message.getVisibilite());

        for (Compte compte1: comptes){

            if (compte1.getEcole() == salle.getEcole()) {
                sender.sender(
                        compte1.getEmail(),
                        "Message de la direction",
                        message.getContent() + ", envoye le " + message.getDate() + ", fichier associe(s) " + message.getFichier()+"Vous pouvez consulter ce message dans votre espace membre dans l'ecole en ligne sur ----> www.ecoles.yesbanana.org");

            }
        }
        return "redirect:/direction/salle/detail/" + salle.getId();

    }

    @GetMapping("/message/delete/{id}")
    public String deleteMessage(@PathVariable Long id){
        messageRepository.deleteById(id);
        return "redirect:/direction/home";
    }

    @PostMapping("/message/save")
    public String saveMessageEcole(Message message, @RequestParam("file") MultipartFile file, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        message.setCompte(compte);
        message.setSender(compte.getUsername());
        message.setEcole(compte.getEcole().getName());
        message.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date()));
        message.setVisibilite(message.getVisibilite().toString());
        if (!(file.isEmpty())) {
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message.setFichier("/downloadFile/" + file.getOriginalFilename());
        }
        Collection<Compte> comptes = compteRepository.findAllByEcole_Id(compte.getEcole().getId());

        Mail sender = new Mail();
        sender.sender(
                compte.getEmail(),
                "Envoi d'un message",
                "Message de la direction ---> "+message.getContent()+", envoye le "+message.getDate()+", fichier associe(s) "+message.getFichier()+"avec un visibilite ----> "+message.getVisibilite());

        for (Compte compte1: comptes){

            sender.sender(
                    compte1.getEmail(),
                    "Message de la direction",
                    message.getContent() + ", envoye le " + message.getDate() + ", fichier associe(s) " + message.getFichier()+"Vous pouvez consulter ce message dans votre espace membre dans l'ecole en ligne sur ----> www.ecoles.yesbanana.org");

        }

        messageRepository.save(message);
        return "redirect:/direction/home";
    }


    @Autowired
    private HebdoRepository hebdoRepository;

    @Autowired
    private PlanningRepository planningRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @GetMapping("/classe/hebdos/{id}")
    public String presences(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Salle salle = salleRepository.getOne(id);
        Ecole ecole = salle.getEcole();

                Collection<Hebdo> hebdos = hebdoRepository.findAllBySalle_Id(salle.getId(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("ecole",ecole);
        model.addAttribute("classe",salle);
        model.addAttribute("lists",hebdos);
        return "direction/classes/hebdos";
    }

    public List<String> removeDuplicates(List<String> list)
    {
        if (list == null){
            return new ArrayList<>();
        }

        // Create a new ArrayList
        List<String> newList = new ArrayList<String>();
        // Traverse through the first list
        for (String element : list) {

            // If this element is not present in newList
            // then add it

            if (element !=null && !newList.contains(element) && !element.isEmpty()) {

                newList.add(element);
            }
        }
        // return the new list
        return newList;
    }

    @GetMapping("/hebdo/detail/{id}")
    public String detailHebdo(Model model, @PathVariable Long id){

        Hebdo hebdo = hebdoRepository.getOne(id);
        Collection<Planning> plannings = planningRepository.findAllByHebdo_Id(hebdo.getId());
        Collection<Presence> presences = presenceRepository.findAllByHebdo_Id(hebdo.getId());
        ArrayList<String> dates = new ArrayList<>();
        for (Presence presenceString : presences){
            dates.add(presenceString.getDate());
        }

        Salle salle = hebdo.getSalle();
        Ecole ecole = salle.getEcole();


        model.addAttribute("plannings",plannings);
        model.addAttribute("dates",removeDuplicates(dates));
        model.addAttribute("hebdo",hebdo);
        model.addAttribute("ecole",ecole);
        model.addAttribute("classe",salle);
        return "direction/classes/hebdo";
    }

    @GetMapping("/presence/detail/{id}")
    public String presenceNew(Model model, @PathVariable Long id, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hebdo hebdo = hebdoRepository.getOne(id);
        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(hebdo.getSalle().getId());


        model.addAttribute("lists",eleves);
        model.addAttribute("hebdo",hebdo);
        model.addAttribute("classe",hebdo.getSalle());
        model.addAttribute("ecole",hebdo.getSalle().getEcole());
        return "direction/classes/presence";

    }

    @GetMapping("/presence/eleve/detail/{eleveId}/{id}")
    public String presenceDetail(Model model, @PathVariable Long id,@PathVariable Long eleveId, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Hebdo hebdo = hebdoRepository.getOne(id);
        Eleve eleve = eleveRepository.getOne(eleveId);
        Collection<Presence> presences = presenceRepository.findAllByEleve_IdAndHebdo_Id(eleve.getId(),hebdo.getId());

        model.addAttribute("lists",presences);
        model.addAttribute("eleve",eleve);
        model.addAttribute("hebdo",hebdo);
        model.addAttribute("classe",hebdo.getSalle());
        model.addAttribute("ecole",hebdo.getSalle().getEcole());
        return "direction/classes/presenceDetail";

    }


}
