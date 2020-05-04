package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by user on 02/04/2020.
 */
@Data
@Entity
@Table(name = "enfant")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Enfant implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String niveau;
    private String email;
    private String modePaiement;
    private String motivation;
    private String telephone;
    private String localisation;
    private int age;
    private String heureDebut;
    private String heureFin;
    private  int nbreJourParSemaine;
    private  int nbreMois;
    private String pays;
    private ArrayList<String> matieres = new ArrayList<>();

    @ManyToMany
    Collection<Encadreur> encadreurs;

}
