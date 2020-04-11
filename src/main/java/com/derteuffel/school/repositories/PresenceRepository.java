package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

/**
 * Created by user on 10/04/2020.
 */
@Repository
public interface PresenceRepository extends JpaRepository<Presence,Long> {

    Collection<Presence> findAllByHebdo_Id(Long id);

    Collection<Presence> findAllByEleve_Id(Long id);

    Collection<Presence> findAllByDate(String date);
}
