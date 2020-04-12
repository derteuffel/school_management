package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.EVisibilite;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.*;
import com.derteuffel.school.services.CompteService;
import com.derteuffel.school.services.MailService;
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
    private SalleRepository salleRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EleveRepository eleveRepository;
    @Value("${file.upload-dir}")
    private String fileStorage;

    @GetMapping("/login")
    public String director() {
        return "direction/login";
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
            compteService.save(compteDto, "/images/icon/avatar-01.jpg", ecole1.getId());
            MailService mailService = new MailService();
            mailService.sendSimpleMessage(
                    "solutionsarl02@gmail.com",
                    "Viens de s'enregistrer comme directeur de l'ecole :" + ecole1.getName() + " de " + ecole1.getProvince(),
                    "...."

            );
        } else {
            model.addAttribute("error", "Aucune ecole n'est enregistrer avec ce code, veillez contacter l'administrateur sur info@yesbanana.org");
            return "direction/registration";
        }

        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/direction/login";
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
        enseignant.setAvatar("/images/icon/avatar-01.jpg");
        enseignantRepository.save(enseignant);

        compteService.saveEnseignant(compte1, "/images/icon/avatar-01.jpg", compte.getEcole().getId(), enseignant);
        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                enseignant.getEmail(),
                "Vous venez d'etre ajouter en tant que enseignant dans votre ecole virtuelle :",
                "vos identifiants : username:" + compte1.getUsername() + " et password : " + compte1.getPassword()

        );

        mailService.sendSimpleMessage(
                "solutionsarl02@gmail.com",
                "YesBanana: Notification Inscription d'un enseignant",
                "L'utilisateur " + compte1.getUsername() + " dont l'email est " +
                        compte1.getEmail() + "  Vient de s'inscrire " +
                        "sur la plateforme YesBanana. Veuillez vous connectez pour manager son status.");

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
        model.addAttribute("lists", eleves);
        return "direction/classes/eleves";
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

        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                compte.getEmail(),
                "Message de la direction ---> "+message.getContent()+", envoye le "+message.getDate()+", fichier associe(s) "+message.getFichier(),
                "avec un visibilite ----> "+message.getVisibilite()

        );
        for (Compte compte1: comptes){

            if (compte1.getEcole() == salle.getEcole()) {
                mailService.sendSimpleMessage(
                        compte1.getEmail(),
                        "Message de la direction ---> " + message.getContent() + ", envoye le " + message.getDate() + ", fichier associe(s) " + message.getFichier(),
                        "Vous pouvez consulter ce message dans votre espace membre dans l'ecole en ligne sur ----> www.ecoles.yesbanana.org"

                );
            }
        }
        return "redirect:/direction/salle/detail/" + salle.getId();

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

        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                compte.getEmail(),
                "Message de la direction ---> "+message.getContent()+", envoye le "+message.getDate()+", fichier associe(s) "+message.getFichier(),
                "avec un visibilite ----> "+message.getVisibilite()

        );
        for (Compte compte1: comptes){

            mailService.sendSimpleMessage(
                    compte1.getEmail(),
                    "Message de la direction ---> "+message.getContent()+", envoye le "+message.getDate()+", fichier associe(s) "+message.getFichier(),
                    "Vous pouvez consulter ce message dans votre espace membre dans l'ecole en ligne sur ----> www.ecoles.yesbanana.org"

            );
        }

        messageRepository.save(message);
        return "redirect:/direction/home";
    }

}
