package com.derteuffel.school.entities;

import com.sun.source.doctree.SerialDataTree;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 02/04/2020.
 */
@Data
@Entity
@Table(name = "enfant")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Enfant implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String niveau;
    private ArrayList<String> matieres = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Parent parent;
}
