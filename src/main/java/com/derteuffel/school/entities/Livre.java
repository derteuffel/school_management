package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "livre")
public class Livre implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int page;
    private String author;
    private String description;
    private String salle;
    private String couverture;
    private String fichier;
}
