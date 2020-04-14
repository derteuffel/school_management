package com.derteuffel.school.entities;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by user on 10/04/2020.
 */
@Data
@Entity
@Table(name = "presence")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Presence implements Serializable {

    @Id
    @GeneratedValue
    private Long id;


    private String date;
    private Boolean status = false;
    @ManyToOne(fetch = FetchType.LAZY)
    private Hebdo hebdo;
    @ManyToOne(fetch = FetchType.LAZY)
    private Eleve eleve;
}
