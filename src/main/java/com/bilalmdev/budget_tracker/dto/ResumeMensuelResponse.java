package com.bilalmdev.budget_tracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class ResumeMensuelResponse {
    private String mois;
    private BigDecimal totalMensuel;
    private List<CategorieTotal> parCategorie;

    public ResumeMensuelResponse(String mois, BigDecimal totalMensuel, List<CategorieTotal> parCategorie) {
        this.mois = mois;
        this.totalMensuel = totalMensuel;
        this.parCategorie = parCategorie;
    }

    public String getMois() { return mois; }
    public BigDecimal getTotalMensuel() { return totalMensuel; }
    public List<CategorieTotal> getParCategorie() { return parCategorie; }
}