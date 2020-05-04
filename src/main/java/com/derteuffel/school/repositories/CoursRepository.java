package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by user on 28/03/2020.
 */
@Repository
public interface CoursRepository extends JpaRepository<Cours,Long> {

    Collection<Cours> findAllBySalleAndType(String name, String type);
    Collection<Cours> findAllByCompte_IdAndSalleAndType(Long id,String name, String type);
    Collection<Cours> findAllByCompte_IdAndType(Long id,String type);
}
