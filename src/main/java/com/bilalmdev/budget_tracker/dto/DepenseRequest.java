package com.bilalmdev.budget_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepenseRequest {
    private BigDecimal montant;
    private String description;
    private LocalDate date;
    private Long categorieId;

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }
}