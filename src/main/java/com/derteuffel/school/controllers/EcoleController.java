package com.derteuffel.school.controllers;

import com.derteuffel.school.entities.Ecole;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/ecole")
public class EcoleController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/connexion")
    public String home(){
        return "index";
    }

    @PostMapping("/save")
    public String save(Ecole ecole, Model model, HttpServletRequest request, @RequestParam("file") MultipartFile file){
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
        ecole.setCode(UUID.randomUUID().toString());
        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            ecole.setLogo("/downloadFile/"+file.getOriginalFilename());
        }
        ecoleRepository.save(ecole);
        MailService mailService = new MailService();
        mailService.sendSimpleMessage(
                "solutionsarl02@gmail.com",
                "Ecole Yesbanana: Notification Creation d'une ecole",
                "Nom : "+ecole.getName()+", Province : "+ecole.getProvince()+", Commune : "+ecole.getCommune()+", Cycle : "+ecole.getCycle()+", Matricule : "+ecole.getMatricule()+", et le code generer que vous allez communiquer au directeur pour valider son compte ---> "+ecole.getCode()+
                        " sur la plateforme ecoles.yesbanana.org. Veuillez vous connectez pour l'envoyer son code de confirmation.");

        request.getSession().setAttribute("item",ecole);
        model.addAttribute("ecole",ecole);
        model.addAttribute("success","Votre ecole a ete ajouter avec success");
        return "index";
    }


}
