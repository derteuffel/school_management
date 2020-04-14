package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by user on 23/03/2020.
 */
@Data
@Entity
@Table(name = "ecole")
@OnDelete(action= OnDeleteAction.NO_ACTION)
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
    private String code;
    private String category;
    private String logo;

}
