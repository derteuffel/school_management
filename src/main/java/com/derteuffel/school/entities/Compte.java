package com.derteuffel.school.entities;

import lombok.Data;
import lombok.Generated;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by user on 22/03/2020.
 */
@Data
@Entity
@Table(name = "compte")
public class Compte implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "comptes_roles",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToOne
    private Ecole ecole;

    @OneToOne
    private Enseignant enseignant;

    @OneToOne
    private Parent parent;


}
