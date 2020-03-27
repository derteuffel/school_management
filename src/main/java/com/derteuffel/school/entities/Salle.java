package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 24/03/2020.
 */
@Data
@Entity
@Table(name = "salle")
public class Salle implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String niveau;
    private String principal;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "salle_enseignant",
            joinColumns = @JoinColumn(
                    name = "salle_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "enseignant_id", referencedColumnName = "id"))
    private Collection<Enseignant> enseignants;

    @ManyToOne
    private Ecole ecole;
}
