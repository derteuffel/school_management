package com.derteuffel.school.controllers;


import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.repositories.CompteRepository;
import com.derteuffel.school.services.CompteService;
import com.derteuffel.school.services.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by derteuffel on 30/11/2018.
 */
@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private CompteService compteService;
    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    //display forgot password page

    @GetMapping("/forgot")
    public String forgot(){
        return "resetSentEmail";
    }

    @PostMapping("/forgot")
    public String sent(Model model, @RequestParam("email") String email, HttpServletRequest request){

        // look up user in database by e-mail

        Collection<Compte> comptes = compteRepository.findAllByEmail(email);

        for (Compte compte : comptes) {
            if (compte == null) {
                model.addAttribute("error", "We didn't find an account for that e-mail address.");
            } else {
                // Generate random 36-character string token for reset password
                compte.setResetToken(UUID.randomUUID().toString());

                //save token to data base
                compteRepository.save(compte);


                String appUrl = request.getScheme() + "://" + request.getServerName();

                Mail sender = new Mail();
                sender.sender(
                        compte.getEmail(),
                        "Demande de réinitialisation de mot de passe",
                        "Pour réinitialiser votre mot de passe, cliquez sur le lien ci-dessous:\n" + appUrl +
                                "/password/reset?token=" + compte.getResetToken());

                // add message in view to confirmation
                model.addAttribute("success", "un lien pour réinitialiser votre mot de passe a été envoyé à cette adresse" + email);

            }
        }
        return "resetSentEmail";
    }

    // display reste password view
    @GetMapping("/reset")
    public  String resetview(Model model, @RequestParam("token") String token){

        Optional<Compte> compteOptional= compteRepository.findByResetToken(token);

        System.out.println(compteOptional.get().getPassword());

        if (compteOptional.isPresent()){
            // token found in DB
            model.addAttribute("resetToken", token);
        }else {
            // token not found in dataBase
            model.addAttribute("error", "Ooops this is an invalid password link.");
        }

        return "resetPassword";

    }


    // process reset password form
    @PostMapping("/reset")
    public  String resetform(Model model, @RequestParam Map<String, String> requestParams, RedirectAttributes redirectAttributes){
        // find the user associated with the reset token

        Optional<Compte> compte= compteRepository.findByResetToken(requestParams.get("token"));


        // this will be non-null but we check just in case
        if (compte.isPresent()){
            // set new password
            System.out.println(compte.get().getPassword());
            compte.get().setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));
            System.out.println(compte.get().getPassword());
            // set the reset token in null so it cannot be used again
            compte.get().setResetToken(null);

            // save user
            compteRepository.save(compte.get());

            System.out.println(compte.get().getPassword());
            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            redirectAttributes.addFlashAttribute("success", "Vous avez réinitialisé avec succès votre Mot de Passe, veuillez vous connecter");
            return "redirect:/ecole/connexion";
        }else {
            model.addAttribute("error", "Ooops vous n'avez pas le bon lien de réinitialisation");
            return "resetPassword";
        }
    }
}
