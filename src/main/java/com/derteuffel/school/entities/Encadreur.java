package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by user on 02/04/2020.
 */

@Data
@Entity
@Table(name = "encadreur")
@PrimaryKeyJoinColumn(name = "id")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Encadreur extends Enseignant {

    private Double salaire;
    private String cv;
    private String heureDebut;
    private String heureFin;
    private  int nbreJourParSemaine;
    private  int nbreMois;
    private String motivation;
    private String localisation;
}
