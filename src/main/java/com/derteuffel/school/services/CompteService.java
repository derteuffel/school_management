package com.derteuffel.school.services;

import com.derteuffel.school.entities.Compte;
import com.derteuffel.school.entities.Encadreur;
import com.derteuffel.school.entities.Enseignant;
import com.derteuffel.school.entities.Parent;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by user on 22/03/2020.
 */
public interface CompteService extends UserDetailsService{

    Compte findByUsername(String username);
    Compte save(CompteRegistrationDto compteRegistrationDto, String s, Long id);
    Compte saveEnseignant(CompteRegistrationDto compteRegistrationDto, String s, Long id, Enseignant enseignant);
    Compte saveParent(CompteRegistrationDto compteRegistrationDto, String s, Parent parent);
    Compte saveEncadreur(CompteRegistrationDto compteRegistrationDto, String s, Encadreur encadreur);
}
