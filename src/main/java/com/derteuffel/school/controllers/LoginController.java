package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by user on 22/03/2020.
 */
@Controller
public class LoginController {

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private EcoleRepository ecoleRepository;

    @ModelAttribute("compte")
    public CompteRegistrationDto compteRegistrationDto(){
        return new CompteRegistrationDto();
    }

    @GetMapping("/registration/direction")
    public String registrationDirection(Model model){

        List<Ecole> ecoles = ecoleRepository.findAllByStatus(false, Sort.by(Sort.Direction.ASC,"name"));
        model.addAttribute("lists",ecoles);
        return "registration/direction";
    }

    @GetMapping("/registration/enseignant")
    public String registrationEnseignant(Model model){
        return "registration/enseignant";
    }

    @GetMapping("/registration/parent")
    public String registrationParent(Model model){
        return "registration/parent";
    }









}
