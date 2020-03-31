package com.derteuffel.school.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by user on 27/03/2020.
 */
@Data
@Entity
@Table(name = "parent")

public class Parent implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String nomComplet;
    private String telephone;
    private String email;
    private String whatsapp;



}
