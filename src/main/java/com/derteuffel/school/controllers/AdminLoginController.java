package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.entities.Livre;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.repositories.LivreRepository;
import com.derteuffel.school.services.CompteService;
import com.derteuffel.school.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private CompteService compteService;

    @Autowired
    private StorageService storageService;
    /*@Value("${file.upload-dir}")
    private  String fileStorage ;*///=System.getProperty("user.dir")+"/src/main/resources/static/downloadFile/";

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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        System.out.println("je suis deconnectee");
        return "redirect:/admin/login";
    }

    @GetMapping("/bibliotheque/lists")
    public String lists(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        System.out.println(principal.getName());
        Compte compte = compteService.findByUsername(principal.getName());
        List<Livre> alls = new ArrayList<>();
        List<Livre> livres = livreRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        for (int i=0;i<livres.size();i++){
            if (!(i>12)){
                alls.add(livres.get(i));
            }
        }
        model.addAttribute("livre",new Livre());
        model.addAttribute("lists",alls);

        return "admin/bibliotheques";
    }

    @PostMapping("/livre/save")
    public String saveBook(@Valid Livre livre, @RequestParam("file") MultipartFile file, @RequestParam("cover") MultipartFile cover){
        storageService.store(file);
        livre.setFichier("/upload-dir/"+file.getOriginalFilename());
        storageService.store(cover);
        livre.setCouverture("/upload-dir/"+cover.getOriginalFilename());

        livreRepository.save(livre);
        return "redirect:/admin/bibliotheque/lists";
    }

    @GetMapping("/livre/edit/{id}")
    public String updateLivre(@PathVariable Long id, Model model){
        Livre livre = livreRepository.getOne(id);
        model.addAttribute("livre",livre);
        return "admin/updateBibliotheque";
    }

    @GetMapping("/livre/delete/{id}")
    public String deleteLivre(@PathVariable Long id){
        livreRepository.deleteById(id);
        return "redirect:/admin/bibliotheque/lists";
    }
}
