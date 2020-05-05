package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.repositories.CompteRepository;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.derteuffel.school.services.MailService;

/**
 * Created by user on 22/03/2020.
 */

@Controller
public class HomeController {

    @Autowired
    private EcoleRepository ecoleRepository;
    @Autowired
    private CompteRepository compteRepository;
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
        Compte compte = compteRepository.findByEmail(sender);
        compte.setConferenceId(conferenceId);
        compteRepository.save(compte);
        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                sender,
                "YesBanana School: VideoCall live",
                "Go to your profile at https://ecoles.yesbanana.org, to join the call");
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
}
