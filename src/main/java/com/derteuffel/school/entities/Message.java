package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 01/04/2020.
 */
@Data
@Entity
@Table(name = "message")
public class Message implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private  String sender;
    private String fichier;
    private String content;
    private ArrayList<String> receivers = new ArrayList<>();
    private String salle;
    private String visibilite;
    private String date;
    @ManyToOne
    private Compte compte;


}
