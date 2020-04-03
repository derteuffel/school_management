package com.derteuffel.school.entities;

import com.sun.source.doctree.SerialDataTree;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 02/04/2020.
 */
@Data
@Entity
@Table(name = "enfant")
public class Enfant implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String niveau;
    private ArrayList<String> matieres = new ArrayList<>();

    @ManyToOne
    private Parent parent;
}
