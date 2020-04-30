package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Livre;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre,Long> {

    List<Livre> findAllBySalle(String name, Sort sort);
}
