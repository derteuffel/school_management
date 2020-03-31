package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ECours;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.*;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/enseignant")
public class EnseignantLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private EleveRepository eleveRepository;
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private ExamenRepository examenRepository;
    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String director(){
        return "enseignant/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();

        request.getSession().setAttribute("ecole", ecole);
        request.getSession().setAttribute("compte",compte);
        return "redirect:/enseignant/ecole/detail/"+ecole.getId();
    }

    @GetMapping("/ecole/detail/{id}")
    public String detail(Model model, @PathVariable Long id,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        Enseignant enseignant = compte.getEnseignant();
        Collection<Salle> salles = new ArrayList<>();
        if (!(enseignant.getSallesIds().isEmpty())) {
            for (Long ids : enseignant.getSallesIds()) {
                salles.add(salleRepository.getOne(ids));
            }
            System.out.println(salles);
            model.addAttribute("salles",salles);
            request.getSession().setAttribute("salles",salles);
        }
        Ecole ecole = ecoleRepository.getOne(id);

        model.addAttribute("ecole",ecole);
        return "enseignant/home";
    }

    @GetMapping("/classe/detail/{id}")
    public String classeDetail(@PathVariable Long id,HttpServletRequest request,Model model){
        Salle salle = salleRepository.getOne(id);
        request.getSession().setAttribute("classe",salle);
        model.addAttribute("classe",salle);

        return "redirect:/enseignant/eleves/lists/"+salle.getId();

    }

    @GetMapping("/eleves/lists/{id}")
    public String allEleves(@PathVariable Long id, Model model){

        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(id);
        model.addAttribute("classe",salleRepository.getOne(id));
        model.addAttribute("student",new Eleve());
        model.addAttribute("lists",eleves);
        return "enseignant/eleves";
    }

    @GetMapping("/parents/lists/{id}")
    public String allParents(@PathVariable Long id, Model model){

        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(id);
        Collection<Parent> parents = new ArrayList<>();
        for (Eleve eleve:eleves){
            if (!(parents.contains(eleve.getParent()))) {
                parents.add(eleve.getParent());
            }
        }
        model.addAttribute("classe",salleRepository.getOne(id));
        model.addAttribute("lists",parents);
        return "enseignant/parents";
    }

    @GetMapping("/parent/detail/{id}/{classeId}")
    public String parentDetail(@PathVariable Long id, @PathVariable Long classeId, Model model){
        Parent parent = parentRepository.getOne(id);
        Collection<Eleve> eleves = eleveRepository.findAllByParent_Id(parent.getId());
        Salle salle = salleRepository.getOne(classeId);
        model.addAttribute("parent",parent);
        model.addAttribute("lists",eleves);
        model.addAttribute("classe",salle);
        return "enseignant/parent";
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
            parent.setEmail(eleve.getEmailTuteur().toUpperCase());
            parent.setTelephone(eleve.getTelephoneTuteur().toUpperCase());
            parent.setWhatsapp(eleve.getWhatsappTuteur().toUpperCase());
            compteRegistrationDto.setEmail(parent.getEmail().toLowerCase());
            compteRegistrationDto.setUsername(parent.getNomComplet().toLowerCase());
            compteRegistrationDto.setPassword(compteRegistrationDto.getUsername());
            compteRegistrationDto.setConfirmPassword(compteRegistrationDto.getPassword());
            parentRepository.save(parent);
            compteService.saveParent(compteRegistrationDto,"/images/icon/avatar-01.jpg",parent);
            eleve.setParent(parent);

        }

        eleveRepository.save(eleve);

        redirectAttributes.addFlashAttribute("success","Vous avez ajouter avec success un nouvel eleve dans cette classe");
        return "redirect:/enseignant/eleves/lists/"+salle.getId();
    }

    @GetMapping("/cours/lists/{id}")
    public String cours(@PathVariable Long id, Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> cours = coursRepository.findAllBySalleAndType(salle.getNiveau(), ECours.COURS.toString());
        model.addAttribute("lists",cours);
        model.addAttribute("salles",salles);
        model.addAttribute("classe",salle);
        model.addAttribute("course",new Cours());
        return "enseignant/courses";
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
        Salle salle = (Salle)request.getSession().getAttribute("classe");
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau cours avec success");
        return "redirect:/enseignant/cours/lists/"+ salle.getId();
    }

    @GetMapping("/devoirs/lists/{id}")
    public String devoirs(@PathVariable Long id, Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> devoirs = coursRepository.findAllBySalleAndType(salle.getNiveau(), ECours.DEVOIRS.toString());
        model.addAttribute("lists",devoirs);
        model.addAttribute("salles",salles);
        model.addAttribute("classe",salle);
        model.addAttribute("devoir",new Cours());
        return "enseignant/devoirs";
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
        Salle salle = (Salle)request.getSession().getAttribute("classe");
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau devoir avec success");
        return "redirect:/enseignant/devoirs/lists/"+ salle.getId();
    }

    @GetMapping("/reponses/lists/{id}")
    public String reponses(@PathVariable Long id, Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> reponses = coursRepository.findAllBySalleAndType(salle.getNiveau(), ECours.REPONSES.toString());
        for (Cours cours : reponses){
            if (cours.getStatus().equals(false)){
                cours.setStatus(true);
                coursRepository.save(cours);
            }
        }
        model.addAttribute("lists",reponses);
        model.addAttribute("classe",salle);
        model.addAttribute("salles",salles);
        return "enseignant/reponses";
    }


    @GetMapping("/examens/lists/{id}")
    public String examens(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Ecole ecole = compte.getEcole();
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Examen> examens = examenRepository.findAllBySalle(salle.getNiveau());
        model.addAttribute("lists",examens);
        model.addAttribute("salles",salles);
        model.addAttribute("classe",salle);
        model.addAttribute("examen",new Examen());
        return "enseignant/examens";
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
        Salle salle = (Salle)request.getSession().getAttribute("classe");
        redirectAttributes.addFlashAttribute("success", "vous avez ajouter un vouveau devoir avec success");
        return "redirect:/enseignant/examens/lists/"+ salle.getId();
    }

    @GetMapping("/access-denied")
    public String access_denied(){
        return "enseignant/access-denied";
    }
}
