package com.derteuffel.school.repositories;

import com.derteuffel.school.entities.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ResponseRepository extends JpaRepository<Response,Long> {
    Collection<Response> findAllByCompte_IdAndSalle(Long id, String name);
    Collection<Response> findAllByCours_Id(Long id);
    Collection<Response> findAllByCompte_Id(Long id);
    Collection<Response> findAllBySalle(String name);
}
