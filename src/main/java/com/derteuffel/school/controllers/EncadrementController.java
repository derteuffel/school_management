package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ECours;
import com.derteuffel.school.enums.ERole;
import com.derteuffel.school.enums.EVisibilite;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.helpers.EleveEncadreurHelper;
import com.derteuffel.school.helpers.EncadrementRegistrationDto;
import com.derteuffel.school.repositories.*;
import com.derteuffel.school.services.CompteService;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user on 02/04/2020.
 */

@Controller
@RequestMapping("/encadrements")
public class EncadrementController {

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private EncadreurRepository encadreurRepository;

    @Autowired
    private EnfantRepository enfantRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String login(){
        return "encadrements/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        System.out.println("je suis deconnectee");
        return "redirect:/encadrements/login";
    }
    @ModelAttribute("compte")
    public EncadrementRegistrationDto encadrementRegistrationDto(){
        return new EncadrementRegistrationDto();
    }


    @GetMapping("/registration2")
    public String registrationForm(Model model){
        ArrayList<Integer> amounts = new ArrayList<>();
        for (int i = 10;i<500;i+=10){
            amounts.add(i);
        }
        model.addAttribute("amounts",amounts);
        return "encadrements/registration2";
    }

    @GetMapping("/registration1")
    public String registration1(Model model){
        return "encadrements/registration1";
    }
    @GetMapping("/registration")
    public String registration(Model model){
        return "encadrements/registration";
    }

    @PostMapping("/registration2")
    public String registrationDirectionSave(@ModelAttribute("compte") @Valid EncadrementRegistrationDto encadrementRegistrationDto,
                                            BindingResult result, RedirectAttributes redirectAttributes,  @RequestParam("file") MultipartFile file ){

        Compte existAccount = compteService.findByUsername(encadrementRegistrationDto.getUsername());
        if (existAccount != null){
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            redirectAttributes.addFlashAttribute("error","Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "redirect:/encadrements/registration2";
        }

            Encadreur encadreur = new Encadreur();
            encadreur.setAvatar("/images/icon/avatar-01.jpg");
            encadreur.setTelephone(encadrementRegistrationDto.getTelephone());
            encadreur.setName(encadrementRegistrationDto.getName());
            encadreur.setEmail(encadrementRegistrationDto.getEmail());
            encadreur.setCour_enseigner(encadrementRegistrationDto.getCours_reference());
            encadreur.setHeureDebut(encadrementRegistrationDto.getHeureDebut());
            encadreur.setHeureFin(encadrementRegistrationDto.getHeureFin());
            encadreur.setMotivation(encadrementRegistrationDto.getMotivation());
            encadreur.setNbreJourParSemaine(encadrementRegistrationDto.getNbreJourParSemanie());
            encadreur.setNbreMois(encadrementRegistrationDto.getNbreMois());
            encadreur.setSalaire(encadrementRegistrationDto.getSalaire()+" $");
            encadreur.setLocalisation(encadrementRegistrationDto.getLocalisation());
            encadreur.setPays(encadrementRegistrationDto.getPays());
            if (!(file.isEmpty())) {
                try {
                    // Get the file and save it somewhere
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                encadreur.setCv("/downloadFile/" + file.getOriginalFilename());
            }

            encadreurRepository.save(encadreur);
            compteService.saveEncadreur(encadrementRegistrationDto,"/images/profile.jpeg",encadreur);

        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/encadrements/login";
    }

    @GetMapping("/encadreurs")
    public String encadreurs(Model model){
        Collection<Encadreur> encadreurs = encadreurRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",encadreurs);
        return "encadrements/enseignants";
    }

    @GetMapping("/encadreur/delete/")
    public String deleteEncadreur(@PathVariable Long id){
        encadreurRepository.deleteById(id);
        return "redirect:/encadrements/encadreurs";
    }

    @GetMapping("/eleves")
    public String eleves(Model model){
        Collection<Enfant> enfants = enfantRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",enfants);
        return "encadrements/enfants";
    }

    @GetMapping("/enseignant/eleves/{id}")
    public String encadreurEleves(Model model, @PathVariable Long id){
        Encadreur encadreur = encadreurRepository.getOne(id);
        Collection<Enfant> enfantCollection = enfantRepository.findAll();
        Collection<Enfant> eleves = new ArrayList<>();
        for (Enfant enfant : enfantCollection){
            if (enfant.getMatieres().contains(encadreur.getCour_enseigner())){
                eleves.add(enfant);
            }
        }
        Collection<Enfant> enfants = encadreur.getEnfants();
        System.out.println(enfants.size());
        model.addAttribute("eleves",eleves);
        model.addAttribute("helper",new EleveEncadreurHelper());
        model.addAttribute("encadreur",encadreur);
        model.addAttribute("lists",enfants);
        return "encadrements/enseignant/enfants";
    }

    @GetMapping("/eleves/enseignant/{id}")
    public String elevesEncadreur(Model model, @PathVariable Long id){
        Enfant enfant =enfantRepository.getOne(id);
        Collection<Encadreur> encadreurs = encadreurRepository.findAllByEnfants_Id(enfant.getId(),Sort.by(Sort.Direction.DESC,"id"));
        System.out.println(encadreurs.size());
        model.addAttribute("enfant",enfant);
        model.addAttribute("lists",encadreurs);
        return "encadrements/enfant/enseignants";
    }


    @PostMapping("/enseignant/eleves/save/{id}")
    public String EncadreurAddEleve(EleveEncadreurHelper eleveEncadreurHelper, @PathVariable Long id){

        System.out.println(eleveEncadreurHelper.getStudents());
        Encadreur encadreur = encadreurRepository.getOne(id);
        for (Long student : eleveEncadreurHelper.getStudents()){
            Enfant enfant = enfantRepository.getOne(student);
            if (!(encadreur.getEnfants().contains(enfant))) {
                encadreur.getEnfants().add(enfant);
            }
            System.out.println(encadreur.getEnfants().size());
            encadreurRepository.save(encadreur);
        }

        return "redirect:/encadrements/enseignant/eleves/"+encadreur.getId();
    }

    @GetMapping("/eleves/delete/")
    public String deleteEnfants(@PathVariable Long id){
        encadreurRepository.deleteById(id);
        return "redirect:/encadrements/eleves";
    }

    @PostMapping("/registration1")
    public String registrationSave(@ModelAttribute("compte") @Valid EncadrementRegistrationDto encadrementRegistrationDto,
                                            BindingResult result, RedirectAttributes redirectAttributes ){

        Compte existAccount = compteService.findByUsername(encadrementRegistrationDto.getUsername());
        if (existAccount != null){
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            redirectAttributes.addFlashAttribute("error","Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "redirect:/encadrements/registration1";
        }



            Enfant enfant = new Enfant();
            enfant.setMatieres(new ArrayList<>(Arrays.asList(encadrementRegistrationDto.getCours_reference())));
            enfant.setName(encadrementRegistrationDto.getName());
            enfant.setEmail(encadrementRegistrationDto.getEmail());
            enfant.setModePaiement(encadrementRegistrationDto.getModePaiement());
            enfant.setMotivation(encadrementRegistrationDto.getMotivation());
            enfant.setNiveau(encadrementRegistrationDto.getNiveau());
            enfant.setLocalisation(encadrementRegistrationDto.getLocalisation());
            enfant.setAge(encadrementRegistrationDto.getAge());
            enfant.setHeureDebut(encadrementRegistrationDto.getHeureDebut());
            enfant.setHeureFin(encadrementRegistrationDto.getHeureFin());
            enfant.setNbreJourParSemaine(encadrementRegistrationDto.getNbreJourParSemanie());
            enfant.setNbreMois(encadrementRegistrationDto.getNbreMois());
            enfant.setPays(encadrementRegistrationDto.getPays());
            enfantRepository.save(enfant);
            compteService.saveEnfant(encadrementRegistrationDto,"/images/profile.jpeg",enfant);
        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/encadrements/login";
    }

    @GetMapping("/cours/lists")
    public String cours( Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Collection<Compte> alls = compteRepository.findAll();
        Collection<Cours> allsCours = new ArrayList<>();
        for (Compte compte1 : alls){
            if (compte1.getRoles().contains(ERole.ROLE_ENCADREUR.toString())){
                allsCours.addAll(coursRepository.findAllByCompte_IdAndType(compte1.getId(),ECours.COURS.toString()));
            }
        }
        Role role = roleRepository.findByName(ERole.ROLE_ROOT.toString());
        Collection<Cours> cours = coursRepository.findAllByCompte_IdAndType(compte.getId(),ECours.COURS.toString());
        if (compte.getRoles().contains(role)){
            model.addAttribute("lists",allsCours);
        }else {
            model.addAttribute("lists", cours);
        }
        System.out.println(cours);
        System.out.println(allsCours);
        request.getSession().setAttribute("compte",compte);
        model.addAttribute("course",new Cours());
        return "encadrements/courses";
    }

    @GetMapping("/bibliotheque/lists")
    public String bibliotheques( Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        return "encadrements/bibliotheques";
    }

    @GetMapping("/cours/update/{id}")
    public String updateCours(@PathVariable Long id, Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Cours cours = coursRepository.getOne(id);
        model.addAttribute("cours",cours);
        return "encadrements/updateCourse";
    }

    @PostMapping("/cours/save")
    public String saveCourse(Cours cours, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        cours.setCompte(compte);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            cours.setFichier("/downloadFile/"+file.getOriginalFilename());
        }

        cours.setDate(dateFormat.format(date));
        cours.setType(ECours.COURS.toString());
        coursRepository.save(cours);
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau cours avec success");
        return "redirect:/encadrements/cours/lists";
    }

    @GetMapping("/devoirs/lists")
    public String devoirs( Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Collection<Compte> alls = compteRepository.findAll();
        Collection<Cours> allsCours = new ArrayList<>();
        for (Compte compte1 : alls){
            if (compte1.getRoles().contains(ERole.ROLE_ENCADREUR.toString())){
                allsCours.addAll(coursRepository.findAllByCompte_IdAndType(compte1.getId(),ECours.DEVOIRS.toString()));
            }
        }
        Role role = roleRepository.findByName(ERole.ROLE_ROOT.toString());
        Collection<Cours> devoirs = coursRepository.findAllByCompte_IdAndType(compte.getId(),ECours.DEVOIRS.toString());
        if (compte.getRoles().contains(role)){
            model.addAttribute("lists",allsCours);
        }else {
            model.addAttribute("lists", devoirs);
        }
        System.out.println(allsCours);
        model.addAttribute("lists",devoirs);
        model.addAttribute("devoir",new Cours());
        return "encadrements/devoirs";
    }

    @GetMapping("/devoir/update/{id}")
    public String updateDevoir(@PathVariable Long id, Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Cours devoir = coursRepository.getOne(id);
        model.addAttribute("devoir",devoir);
        return "encadrements/updateDevoir";
    }


    @PostMapping("/devoirs/save")
    public String saveDevoir(Cours devoir, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        devoir.setCompte(compte);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            devoir.setFichier("/downloadFile/"+file.getOriginalFilename());
        }

        devoir.setDate(dateFormat.format(date));
        devoir.setType(ECours.DEVOIRS.toString());
        coursRepository.save(devoir);
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau devoir avec success");
        return "redirect:/encadrements/devoirs/lists";
    }

    @GetMapping("/reponses/add/{id}")
    public String reponsesForm(@PathVariable Long id, Model model){
        Cours devoir = coursRepository.getOne(id);
        Cours reponse = new Cours();
        model.addAttribute("devoir",devoir);
        model.addAttribute("reponse",reponse);
        return "encadrements/reponse";
    }

    @PostMapping("/reponses/save/{id}")
    public String reponseSave(Cours cours, HttpServletRequest request, @PathVariable Long id, @RequestParam("file") MultipartFile file){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        cours.setType(ECours.REPONSES.toString());
        cours.setCompte(compte);
        cours.setCours(coursRepository.getOne(id));
        cours.setSalle(coursRepository.getOne(id).getSalle());
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        cours.setDate(dateFormat.format(date));
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            cours.setFichier("/downloadFile/"+file.getOriginalFilename());
        }

        cours.setStatus(false);
        coursRepository.save(cours);

        return "redirect:/encadrements/reponses/lists";
    }


    @GetMapping("/reponses/lists")
    public String reponses( Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());

        Collection<Compte> alls = compteRepository.findAll();
        Collection<Cours> allsCours = new ArrayList<>();
        for (Compte compte1 : alls){
            if (compte1.getRoles().contains(ERole.ROLE_ENCADREUR.toString())){
                allsCours.addAll(coursRepository.findAllByCompte_IdAndType(compte1.getId(),ECours.REPONSES.toString()));
            }
        }
        Role role = roleRepository.findByName(ERole.ROLE_ROOT.toString());
        Collection<Cours> reponses = coursRepository.findAllByCompte_IdAndType(compte.getId(),ECours.REPONSES.toString());
        if (compte.getRoles().contains(role)){
            model.addAttribute("lists",allsCours);
        }else {
            model.addAttribute("lists", reponses);
        }
        System.out.println(reponses);
        System.out.println(allsCours);
        for (Cours cours : reponses){
            if (cours.getStatus().equals(false)){
                cours.setStatus(true);
                coursRepository.save(cours);
            }
        }
        model.addAttribute("lists",reponses);

        return "encadrements/reponses";
    }


    @Autowired
    private ExamenRepository examenRepository;

    @GetMapping("/examens/lists")
    public String examens( Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Collection<Compte> alls = compteRepository.findAll();
        Collection<Examen> allsCours = new ArrayList<>();
        for (Compte compte1 : alls){
            if (compte1.getRoles().contains(ERole.ROLE_ENCADREUR.toString())){
                allsCours.addAll(examenRepository.findAllByCompte_Id(compte1.getId()));
            }
        }
        Role role = roleRepository.findByName(ERole.ROLE_ROOT.toString());
        Collection<Examen> examens = examenRepository.findAllByCompte_Id(compte.getId());
        if (compte.getRoles().contains(role)){
            model.addAttribute("lists",allsCours);
        }else {
            model.addAttribute("lists", examens);
        }
        model.addAttribute("lists",examens);

        model.addAttribute("examen",new Examen());
        return "encadrements/examens";
    }

    @GetMapping("/examen/update/{id}")
    public String updateExamen(@PathVariable Long id, Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Cours examen = coursRepository.getOne(id);
        model.addAttribute("examen",examen);
        return "encadrements/updateExamen";
    }

    @PostMapping("/examens/save")
    public String saveExamen(Examen examen, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        examen.setCompte(compte);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            examen.setFichier("/downloadFile/"+file.getOriginalFilename());
        }

        examen.setDate(dateFormat.format(date));
        examenRepository.save(examen);
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau devoir avec success");
        return "redirect:/encadrements/examens/lists";
    }

    @GetMapping("/access-denied")
    public String access_denied(){
        return "encadrements/access-denied";
    }

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/message/save")
    public String saveMessage(Message message, @RequestParam("file") MultipartFile file, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        message.setCompte(compte);
        message.setSender(compte.getUsername());
        message.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date()));
        message.setVisibilite(message.getVisibilite().toString());
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            message.setFichier("/downloadFile/"+file.getOriginalFilename());
        }

        messageRepository.save(message);
        return "redirect:/encadrements/message";

    }

    @GetMapping("/message")
    public String messages( Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Role roleEncadreur = roleRepository.findByName(ERole.ROLE_ENCADREUR.toString());
        Role roleParent = roleRepository.findByName(ERole.ROLE_PARENT.toString());
        Collection<Message> messages =messageRepository.findAllByVisibilite(EVisibilite.ENCADREUR.toString(), Sort.by(Sort.Direction.DESC,"id"));
        Collection<Message> messages2 =messageRepository.findAllByVisibilite(EVisibilite.PARENT.toString(), Sort.by(Sort.Direction.DESC,"id"));
        Collection<Message> messages1 = messageRepository.findAllByCompte_Id(compte.getId());

        if (compte.getRoles().contains(roleEncadreur)){
            for (Message message : messages1){
                if(!(messages.contains(message))){
                    messages.add(message);
                }
            }
            model.addAttribute("lists",messages);
        }else {
            for (Message message : messages1){
                if(!(messages2.contains(message))){
                    messages2.add(message);
                }
            }
            model.addAttribute("lists",messages2);
        }


        model.addAttribute("message",new Message());
        return "encadrements/messages";
    }

}
