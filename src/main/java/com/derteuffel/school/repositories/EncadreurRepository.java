package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Encadreur;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by user on 02/04/2020.
 */
@Repository
public interface EncadreurRepository extends JpaRepository<Encadreur, Long> {
    Collection<Encadreur> findAllByEnfants_Id(Long id, Sort sort);
}
