package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by user on 29/03/2020.
 */
@Repository
public interface ExamenRepository extends JpaRepository<Examen,Long> {

    Collection<Examen> findAllBySalle(String name);
}
