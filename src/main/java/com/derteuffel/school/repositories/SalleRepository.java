package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 24/03/2020.
 */
@Repository
public interface SalleRepository extends JpaRepository<Salle,Long> {

}
