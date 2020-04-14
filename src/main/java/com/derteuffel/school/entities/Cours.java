package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by user on 27/03/2020.
 */
@Data
@Entity
@Table(name = "cours")
@OnDelete(action= OnDeleteAction.NO_ACTION)
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
    private String lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cours cours;
}
