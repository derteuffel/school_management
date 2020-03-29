package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by user on 27/03/2020.
 */
@Data
@Entity
@Table(name = "cours")
public class Cours implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private String fichier;
    private String salle;
    private String date;
    private String type;
    private Boolean status;

    @ManyToOne
    private Compte compte;
}
