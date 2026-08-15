package com.bilalmdev.budget_tracker.repository;

import com.bilalmdev.budget_tracker.model.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DepenseRepository extends JpaRepository<Depense, Long> {

    List<Depense> findByUtilisateurId(Long utilisateurId);

    @Query("""
        SELECT d.categorie.nom, SUM(d.montant)
        FROM Depense d
        WHERE d.utilisateur.id = :utilisateurId
        AND d.date >= :debutMois
        AND d.date < :finMois
        GROUP BY d.categorie.nom
        """)
    List<Object[]> sommeParCategorie(@Param("utilisateurId") Long utilisateurId,
                                      @Param("debutMois") LocalDate debutMois,
                                      @Param("finMois") LocalDate finMois);
}