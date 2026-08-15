package com.bilalmdev.budget_tracker.repository;

import com.bilalmdev.budget_tracker.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}