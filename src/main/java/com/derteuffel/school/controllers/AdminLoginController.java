package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String director(){
        return "admin/login";
    }

    @GetMapping("/registration")
    public String registrationForm(Model model){
        return "admin/registration";
    }


    @PostMapping("/registration")
    public String registrationDirectionSave(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                            BindingResult result, RedirectAttributes redirectAttributes, Model model, String ecole){

        Compte existAccount = compteService.findByUsername(compteDto.getUsername());
        if (existAccount != null){
            result.rejectValue("username", null, "Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
            model.addAttribute("error","Il existe deja un compte avec ce nom d'utilisateur vueillez choisir un autre");
        }

        if (result.hasErrors()) {
            return "admin/registration";
        }

        Ecole ecole1 = ecoleRepository.getOne(Long.parseLong(ecole));

        compteService.save(compteDto,"/images/icon/avatar-01.jpg",ecole1.getId());
        redirectAttributes.addFlashAttribute("success", "Votre enregistrement a ete effectuer avec succes");
        return "redirect:/admin/login";
    }
}
