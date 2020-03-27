package com.derteuffel.school.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by user on 22/03/2020.
 */
@Data
@Entity
@Table(name = "role")
public class Role implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
