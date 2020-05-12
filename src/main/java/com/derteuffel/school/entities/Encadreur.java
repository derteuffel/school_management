package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by user on 02/04/2020.
 */

@Data
@Entity
@Table(name = "encadreur")
@PrimaryKeyJoinColumn(name = "id")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Encadreur extends Enseignant {

    private String salaire;
    private String cv;
    private String heureDebut;
    private String heureFin;
    private  int nbreJourParSemaine;
    private  int nbreMois;
    private String motivation;
    private String localisation;
    private String pays;
    private String code;
    private String description;
    private String category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "encadreurs_enfants",
            joinColumns = @JoinColumn(
                    name = "encadreur_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "enfant_id", referencedColumnName = "id"))
    private Collection<Enfant> enfants;
}
