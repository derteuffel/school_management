package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by user on 29/03/2020.
 */
@Data
@Entity
@Table(name = "examen")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Examen implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String date;
    private String session;
    private String fichier;
    private String salle;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Compte compte;
}
