package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 10/04/2020.
 */
@Data
@Entity
@Table(name = "planning")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Planning implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateJour = new Date();
    private String titre;
    private String heure;
    private ArrayList<Boolean> validations ;

    @ManyToOne
    private Hebdo hebdo;
}
