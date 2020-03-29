package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by user on 23/03/2020.
 */
@Data
@Entity
@Table(name = "ecole")
public class Ecole implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String cycle;
    private String country;
    private String province;
    private String commune;
    private String avenue;
    private String quartier;
    private String numParcelle;
    private String matricule;
    private Boolean status;
    private Boolean terms;

}
