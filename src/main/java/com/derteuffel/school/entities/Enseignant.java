package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 24/03/2020.
 */
@Data
@Entity
@Table(name = "enseignant")
public class Enseignant implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String prenom;
    private String postnom;
    private String telephone;
    private String email;
    private String whatsapp;
    private String cour_enseigner;
    private String avatar;

    private ArrayList<Long> sallesIds = new ArrayList<>();

}
