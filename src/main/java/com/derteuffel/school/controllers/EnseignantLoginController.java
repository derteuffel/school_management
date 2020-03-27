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
@RequestMapping("/enseignant")
public class EnseignantLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String director(){
        return "enseignant/login";
    }

}
