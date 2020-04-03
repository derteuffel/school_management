package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Encadreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 02/04/2020.
 */
@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Long> {
}
