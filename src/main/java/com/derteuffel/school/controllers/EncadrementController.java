package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ECours;
import com.derteuffel.school.enums.ERole;
import com.derteuffel.school.enums.EVisibilite;
import com.derteuffel.school.helpers.CompteRegistrationDto;
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
    public CompteRegistrationDto compteRegistrationDto(){
        return new CompteRegistrationDto();
    }

    @GetMapping("/registration")
    public String registrationForm(Model model){
        return "encadrements/registration";
    }

    @PostMapping("/registration")
    public String registrationDirectionSave(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                            BindingResult result, RedirectAttributes redirectAttributes, Model model, String type,String cours_reference){

        Compte existAccount = compteService.findByUsername(compteDto.getUsername());
        if (existAccount != null){
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            model.addAttribute("error","Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "encadrements/registration";
        }



        if (type.equals("ENCADREUR")){
            Encadreur encadreur = new Encadreur();
            encadreur.setAvatar("/images/icon/avatar-01.jpg");
            encadreur.setCour_enseigner(cours_reference);
            encadreur.setEmail(compteDto.getEmail());
            encadreur.setName(compteDto.getUsername());
            encadreurRepository.save(encadreur);
            compteService.saveEncadreur(compteDto,"/images/profile.jpeg",encadreur);
        }else {
            Enfant enfant = new Enfant();
            enfant.setEmail(compteDto.getEmail());
            enfant.setName(compteDto.getUsername());
            enfant.setMatieres(new ArrayList<>(Arrays.asList(cours_reference)));
            enfantRepository.save(enfant);
            compteService.saveEnfant(compteDto,"/images/profile.jpeg",enfant);
        }
        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/encadrements/login";
    }

    @GetMapping("/cours/lists")
    public String cours( Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Collection<Cours> cours = coursRepository.findAllByCompte_IdAndType(compte.getId(),ECours.COURS.toString());
        model.addAttribute("lists",cours);
        request.getSession().setAttribute("compte",compte);
        model.addAttribute("course",new Cours());
        return "encadrements/courses";
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
        Collection<Cours> devoirs = coursRepository.findAllByCompte_IdAndType(compte.getId(), ECours.DEVOIRS.toString());
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

        Collection<Cours> reponses = coursRepository.findAllByCompte_IdAndType(compte.getId(), ECours.REPONSES.toString());
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
        Collection<Examen> examens = examenRepository.findAllByCompte_Id(compte.getId());
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
