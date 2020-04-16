package com.derteuffel.school.entities;

import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created by user on 22/03/2020.
 */
@Data
@Entity
@Table(name = "compte")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Compte implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;
    private String avatar;
    private String resetToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "comptes_roles",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ecole ecole;

    @OneToOne(fetch = FetchType.LAZY)
    private Enseignant enseignant;

    @OneToOne(fetch = FetchType.LAZY)
    private Parent parent;

   /* @OneToMany(mappedBy = "compte")
    private Collection<Cours> cours;*/

   @OneToOne(fetch = FetchType.LAZY)
   private Enfant enfant;


}
