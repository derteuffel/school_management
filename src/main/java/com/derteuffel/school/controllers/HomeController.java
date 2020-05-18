package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.entities.Encadreur;
import com.derteuffel.school.enums.ECategory;
import com.derteuffel.school.repositories.CompteRepository;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.repositories.EncadreurRepository;
import com.derteuffel.school.services.MailService;
import com.derteuffel.school.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by user on 22/03/2020.
 */

@Controller
public class HomeController {

    @Autowired
    private EcoleRepository ecoleRepository;
    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private EncadreurRepository encadreurRepository;

    @Autowired
    private StorageService storageService;
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/")
    public String accueil(Model model){
        model.addAttribute("lists", ecoleRepository.findAllByStatus(true,Sort.by(Sort.Direction.ASC,"name")));
        model.addAttribute("ecole", new Ecole());
        return "index1";
    }


    @GetMapping("/login/admin")
    public String admin(){
        return "login/admin";
    }

    @GetMapping("/sendMail/{sender}/{conferenceId}")
    public String sendMail(@PathVariable String sender,@PathVariable String conferenceId){
        Compte compte = compteRepository.findByEnseignant_Id(Long.parseLong(sender));
        System.out.println(compte);
        if(compte==null)
        {
             compte = compteRepository.findByParent_Id(Long.parseLong(sender));
             if(compte==null){

                     compte = compteRepository.findByEnfant_Id(Long.parseLong(sender));
                 if(compte==null){

                 compte = compteRepository.getOne(Long.parseLong(sender));
                 }

             }
        }

        compte.setConferenceId(conferenceId);
        compteRepository.save(compte);
        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                compte.getEmail(),
                "YesBanana School: VideoCall live",
                "Go to your profile at https://ecoles.yesbanana.org, to join the call");
        return "index1";
    }
    @GetMapping("/planning/{sender}")
    public String planning(@PathVariable String sender, @RequestParam String date){
        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                sender,
                "YesBanana School: Reunion du " + date ,
                "Allez a votre profil, a l'adresse:  https://ecoles.yesbanana.org, pour participer a la " +
                        "reunion du " + date);
        return "index1";
    }


    @GetMapping("/login/parent")
    public String parent(){
        return "login/parent";
    }

    @GetMapping("/login/enseignant")
    public String enseignant(){
        return "login/enseignant";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @GetMapping("/experts/ecoles")
    public String getExpert1(Model model){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.expert_yesb_primaire.toString(),Sort.by(Sort.Direction.DESC,"id"));
        encadreurs.addAll(encadreurRepository.findAllByCategory(ECategory.expert_yesb_secondaire.toString(),Sort.by(Sort.Direction.DESC,"id")));
        model.addAttribute("lists",encadreurs);
        model.addAttribute("name","ecoles");
        return "expertsProfiles";
    }
    @GetMapping("/experts/stages")
    public String getExpert4(Model model){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.expert_yesb_en_stage_professionnelle.toString(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",encadreurs);
        model.addAttribute("name","stages");
        return "expertsProfiles";
    }

    @GetMapping("/experts/ecoles/{matiere}")
    public String getExpert1Matieres(Model model,@PathVariable String matiere){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.expert_yesb_primaire.toString(),Sort.by(Sort.Direction.DESC,"id"));
        encadreurs.addAll(encadreurRepository.findAllByCategory(ECategory.expert_yesb_secondaire.toString(),Sort.by(Sort.Direction.DESC,"id")));
        Collection<Encadreur> lists = new ArrayList<>();
        for (Encadreur encadreur : encadreurs){
            if (encadreur.getCour_enseigner().contains(matiere.toString())){
                lists.add(encadreur);
            }
        }
        model.addAttribute("name","ecoles");
        model.addAttribute("lists",lists);
        return "expertsProfiles";
    }

    @GetMapping("/experts/universites")
    public String getExpert2(Model model){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.appui_redaction_travail_de_fin_de_cycle.toString(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",encadreurs);
        model.addAttribute("name","universite");
        return "expertsProfiles";
    }

    @GetMapping("/experts/universites/{matiere}")
    public String getExpert2Matieres(Model model,@PathVariable String matiere){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.appui_redaction_travail_de_fin_de_cycle.toString(),Sort.by(Sort.Direction.DESC,"id"));
        Collection<Encadreur> lists = new ArrayList<>();
        for (Encadreur encadreur : encadreurs){
            if (encadreur.getCour_enseigner().contains(matiere.toString())){
                lists.add(encadreur);
            }
        }
        model.addAttribute("name","universite");
        model.addAttribute("lists",lists);
        return "expertsProfiles";
    }

    @GetMapping("/experts/professionnels")
    public String getExpert3(Model model){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.expert_yesb_en_formation_professionnelle.toString(),Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("lists",encadreurs);
        model.addAttribute("name","professionnelle");
        return "expertsProfiles";
    }

    @GetMapping("/experts/professionnels/{matiere}")
    public String getExpert3Matieres(Model model,@PathVariable String matiere){

        Collection<Encadreur> encadreurs = encadreurRepository.findAllByCategory(ECategory.expert_yesb_en_formation_professionnelle.toString(),Sort.by(Sort.Direction.DESC,"id"));
        Collection<Encadreur> lists = new ArrayList<>();
        for (Encadreur encadreur : encadreurs){
            if (encadreur.getCour_enseigner().contains(matiere.toString())){
                lists.add(encadreur);
            }
        }
        model.addAttribute("name","professionnelle");
        model.addAttribute("lists",encadreurs);
        return "expertsProfiles";
    }
}
