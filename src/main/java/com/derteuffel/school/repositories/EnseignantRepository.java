package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by user on 24/03/2020.
 */
@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant,Long> {

    Enseignant findByEmail(String email);


}
