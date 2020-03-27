package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.repositories.EleveRepository;
import com.derteuffel.school.repositories.ParentRepository;
import com.derteuffel.school.repositories.SalleRepository;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

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
        Salle salle = salleRepository.findByPrincipal(compte.getEnseignant().getName()+""+compte.getEnseignant().getPrenom());
        request.getSession().setAttribute("classe",salle);
        request.getSession().setAttribute("ecole", ecole);
        request.getSession().setAttribute("compte",compte);
        return "redirect:/enseignant/classe/detail/"+salle.getId();
    }

    @GetMapping("/classe/detail/{id}")
    public String detail(Model model, @PathVariable Long id){
        Salle salle = salleRepository.getOne(id);
        model.addAttribute("salle",salle);
        return "enseignant/home";
    }

    @GetMapping("/eleves/lists/{id}")
    public String allEleves(@PathVariable Long id, Model model){

        Collection<Eleve> eleves = eleveRepository.findAllBySalle_Id(id);
        model.addAttribute("classe",salleRepository.getOne(id));
        model.addAttribute("student",new Eleve());
        model.addAttribute("lists",eleves);
        return "enseignant/eleves";
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
            compteRegistrationDto.setEmail(parent.getEmail());
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
}
