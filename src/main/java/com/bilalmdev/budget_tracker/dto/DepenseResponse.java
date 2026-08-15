package com.bilalmdev.budget_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepenseResponse {
    private Long id;
    private BigDecimal montant;
    private String description;
    private LocalDate date;
    private String categorieNom;

    public DepenseResponse(Long id, BigDecimal montant, String description, LocalDate date, String categorieNom) {
        this.id = id;
        this.montant = montant;
        this.description = description;
        this.date = date;
        this.categorieNom = categorieNom;
    }

    public Long getId() { return id; }
    public BigDecimal getMontant() { return montant; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public String getCategorieNom() { return categorieNom; }
}