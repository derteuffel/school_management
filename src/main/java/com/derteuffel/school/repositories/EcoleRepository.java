package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Ecole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by user on 23/03/2020.
 */
@Repository
public interface EcoleRepository extends JpaRepository<Ecole, Long> {
    List<Ecole> findAllByStatus(Boolean status, Sort sort);
}
