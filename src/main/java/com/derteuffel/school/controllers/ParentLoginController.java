package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ECours;
import com.derteuffel.school.repositories.*;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/parent")
public class ParentLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EleveRepository eleveRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String director(){
        return "parent/login";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        Parent parent = compte.getParent();
        Collection<Eleve> eleves =eleveRepository.findAllByParent_Id(parent.getId());
        Collection<Ecole> ecoles = new ArrayList<>();
        for (Eleve eleve : eleves){
            if (!(ecoles.contains(eleve.getSalle().getEcole()))) {
                ecoles.add(eleve.getSalle().getEcole());
            }
        }
        request.getSession().setAttribute("ecoles", ecoles);
        request.getSession().setAttribute("compte",compte);
        return "parent/home";
    }

    @GetMapping("/ecole/detail/{id}")
    public String ecoleDetail(@PathVariable Long id, Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        Parent parent = compte.getParent();
        Collection<Eleve> eleves =eleveRepository.findAllByParent_Id(parent.getId());
        Collection<Salle> salles = new ArrayList<>();
        Collection<Salle> lists = new ArrayList<>();
        for (Eleve eleve : eleves){
            salles.add(eleve.getSalle());
        }

        Ecole ecole = ecoleRepository.getOne(id);
        for (Salle salle : salles){
            if (salleRepository.findAllByEcole_Id(ecole.getId()).contains(salle)){
                lists.add(salle);
            }
        }

        model.addAttribute("lists", lists);
        model.addAttribute("ecole",ecole);
        request.getSession().setAttribute("ecole",ecole);

        return "parent/ecole/home";
    }

    @GetMapping("/classe/detail/{id}")
    public String parentClasse(@PathVariable Long id, Model model){
        Salle salle = salleRepository.getOne(id);
        Ecole ecole = salle.getEcole();
        model.addAttribute("ecole",ecole);
        model.addAttribute("classe",salle);

        return "parent/ecole/classe";
    }

    @GetMapping("/cours/lists/{id}/{ecoleId}")
    public String cours(@PathVariable Long id, @PathVariable Long ecoleId, Model model){
        Ecole ecole = ecoleRepository.getOne(ecoleId);
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> cours = new ArrayList<>();
        if (salles.contains(salle)) {
            cours = coursRepository.findAllBySalleAndType(salle.getNiveau(), ECours.COURS.toString());
        }else {
            model.addAttribute("error","Vous n'avez aucune classe avec ce nom dans cet etablissement");
        }
        model.addAttribute("ecole",ecole);
        model.addAttribute("lists",cours);
        model.addAttribute("classe",salle);
        return "parent/courses";
    }

    @GetMapping("/devoirs/lists/{id}/{ecoleId}")
    public String devoirs(@PathVariable Long id, @PathVariable Long ecoleId, Model model, HttpServletRequest request){
        Ecole ecole = ecoleRepository.getOne(ecoleId);
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> devoirs = new ArrayList<>();
        if (salles.contains(salle)) {
            devoirs = coursRepository.findAllBySalleAndType(salle.getNiveau(), ECours.DEVOIRS.toString());
        }else {
            model.addAttribute("error","Vous n'avez aucune classe avec ce nom dans cet etablissement");
        }
        model.addAttribute("ecole",ecole);
        model.addAttribute("lists",devoirs);
        model.addAttribute("classe",salle);
        request.getSession().setAttribute("ecoleId", ecole.getId());
        request.getSession().setAttribute("salleId", salle.getId());
        return "parent/devoirs";
    }

    @GetMapping("/reponses/lists/{id}/{username}/{ecoleId}")
    public String reponse(@PathVariable Long id, @PathVariable Long ecoleId,@PathVariable String username, Model model, HttpServletRequest request){
        Ecole ecole = ecoleRepository.getOne(ecoleId);
        Compte compte = compteService.findByUsername(username);
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Cours> reponses = new ArrayList<>();
        if (salles.contains(salle)) {
            reponses = coursRepository.findAllByCompte_IdAndSalleAndType(compte.getId(),salle.getNiveau(), ECours.REPONSES.toString());
        }else {
            model.addAttribute("error","Vous n'avez aucune classe avec ce nom dans cet etablissement");
        }
        model.addAttribute("ecole",ecole);
        model.addAttribute("lists",reponses);
        model.addAttribute("classe",salle);
        return "parent/reponses";
    }

    @GetMapping("/reponses/add/{id}")
    public String reponsesForm(@PathVariable Long id, Model model){
        Cours devoir = coursRepository.getOne(id);
        Cours reponse = new Cours();
        model.addAttribute("devoir",devoir);
        model.addAttribute("reponse",reponse);
        return "parent/reponse";
    }

    @PostMapping("/reponse/save/{id}")
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

        return "redirect:/parent/reponses/lists/"+(Long)request.getSession().getAttribute("salleId")+"/"+compte.getUsername()+"/"+(Long)request.getSession().getAttribute("ecoleId");
    }

    @GetMapping("/examens/lists/{id}/{ecoleId}")
    public String examens(@PathVariable Long id, @PathVariable Long ecoleId, Model model){
        Ecole ecole = ecoleRepository.getOne(ecoleId);
        Collection<Salle> salles = salleRepository.findAllByEcole_Id(ecole.getId());
        Salle salle = salleRepository.getOne(id);
        Collection<Examen> examens = new ArrayList<>();
        if (salles.contains(salle)) {
            examens = examenRepository.findAllBySalle(salle.getNiveau());
        }else {
            model.addAttribute("error","Vous n'avez aucune classe avec ce nom dans cet etablissement");
        }
        model.addAttribute("ecole",ecole);
        model.addAttribute("lists",examens);
        model.addAttribute("classe",salle);
        return "parent/examens";
    }

}
