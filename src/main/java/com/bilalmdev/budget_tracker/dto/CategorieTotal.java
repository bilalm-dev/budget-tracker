package com.bilalmdev.budget_tracker.dto;

import java.math.BigDecimal;

public class CategorieTotal {
    private String categorie;
    private BigDecimal total;

    public CategorieTotal(String categorie, BigDecimal total) {
        this.categorie = categorie;
        this.total = total;
    }

    public String getCategorie() { return categorie; }
    public BigDecimal getTotal() { return total; }
}