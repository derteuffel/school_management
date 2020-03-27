package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 22/03/2020.
 */
@Repository
public interface CompteRepository extends JpaRepository<Compte,Long> {

    Compte findByUsername(String username);
}
