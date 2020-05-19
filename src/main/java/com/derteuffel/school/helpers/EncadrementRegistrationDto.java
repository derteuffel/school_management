package com.derteuffel.school.helpers;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * Created by user on 22/03/2020.
 */

@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
})
@Data
public class EncadrementRegistrationDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String confirmPassword;

    @Email
    @NotEmpty
    private String email;
    private String name;
    private String cours_reference;
    private String heureDebut;
    private String heureFin;
    private  int nbreJourParSemanie;
    private String telephone;
    private String niveau;
    private String localisation;
    private int age;
    private String pays;
    private  int nbreMois;
    private String salaire;
    private String whatsapp;
    private String motivation;
    private String description;
    private String modePaiement;
    private String type;
}
