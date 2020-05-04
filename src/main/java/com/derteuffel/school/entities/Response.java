package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "response")
public class Response implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private String fichier;
    private String salle;
    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cours cours;
}
