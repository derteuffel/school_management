package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 27/03/2020.
 */
@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Parent findByNomComplet(String name);
}
