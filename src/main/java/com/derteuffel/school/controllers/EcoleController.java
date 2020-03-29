package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.repositories.EcoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/ecole")
public class EcoleController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @GetMapping("/connexion")
    public String home(){
        return "index";
    }

    @PostMapping("/save")
    public String save(Ecole ecole, Model model, HttpServletRequest request){
        ecole.setAvenue(ecole.getAvenue().toUpperCase());
        ecole.setCommune(ecole.getCommune().toUpperCase());
        ecole.setCountry(ecole.getCountry().toUpperCase());
        ecole.setCycle(ecole.getCycle().toUpperCase());
        ecole.setMatricule(ecole.getMatricule().toUpperCase());
        ecole.setName(ecole.getName().toUpperCase());
        ecole.setNumParcelle(ecole.getNumParcelle().toUpperCase());
        ecole.setProvince(ecole.getProvince().toUpperCase());
        ecole.setQuartier(ecole.getQuartier().toUpperCase());
        ecole.setStatus(false);
        ecoleRepository.save(ecole);
        request.getSession().setAttribute("item",ecole);
        model.addAttribute("ecole",ecole);
        model.addAttribute("success","Votre ecole a ete ajouter avec success");
        return "index";
    }


}
