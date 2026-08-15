package com.bilalmdev.budget_tracker.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DepenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String obtenirToken(String email) throws Exception {
        String reponse = mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"email\": \"" + email + "\", \"motDePasse\": \"motdepasse123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(reponse, "$.token");
    }

    private String depenseJson(double montant, String description, String date, int categorieId) {
        return "{\"montant\": " + montant + ", \"description\": \"" + description
                + "\", \"date\": \"" + date + "\", \"categorieId\": " + categorieId + "}";
    }

    @Test
    void creerUneDepenseReussit() throws Exception {
        String token = obtenirToken("depense1@test.com");

        mockMvc.perform(post("/depenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(depenseJson(45.50, "Courses", "2026-08-05", 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montant").value(45.50))
                .andExpect(jsonPath("$.categorieNom").value("Nourriture"));
    }

    @Test
    void listerLesDepensesDeLUtilisateur() throws Exception {
        String token = obtenirToken("depense2@test.com");

        mockMvc.perform(post("/depenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(depenseJson(30.00, "Métro", "2026-08-10", 3)));

        mockMvc.perform(get("/depenses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Métro"));
    }

    @Test
    void modifierUneDepenseReussit() throws Exception {
        String token = obtenirToken("depense3@test.com");

        String reponseCreation = mockMvc.perform(post("/depenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(depenseJson(20.00, "Cinéma", "2026-08-12", 4)))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(reponseCreation, "$.id");

        mockMvc.perform(put("/depenses/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(depenseJson(25.00, "Cinéma (corrigé)", "2026-08-12", 4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montant").value(25.00))
                .andExpect(jsonPath("$.description").value("Cinéma (corrigé)"));
    }

    @Test
    void supprimerUneDepenseReussit() throws Exception {
        String token = obtenirToken("depense4@test.com");

        String reponseCreation = mockMvc.perform(post("/depenses")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(depenseJson(15.00, "Café", "2026-08-13", 2)))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(reponseCreation, "$.id");

        mockMvc.perform(delete("/depenses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/depenses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void unUtilisateurNePeutPasModifierLaDepenseDUnAutre() throws Exception {
        String tokenA = obtenirToken("utilisateurA@test.com");
        String tokenB = obtenirToken("utilisateurB@test.com");

        String reponseCreation = mockMvc.perform(post("/depenses")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType("application/json")
                        .content(depenseJson(50.00, "Dépense de A", "2026-08-13", 2)))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(reponseCreation, "$.id");

        // B essaie de modifier la dépense de A : doit échouer
        mockMvc.perform(put("/depenses/" + id)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType("application/json")
                        .content(depenseJson(999.00, "Piraté", "2026-08-13", 2)))
                .andExpect(status().isNotFound());

        // B essaie de supprimer la dépense de A : doit échouer aussi
        mockMvc.perform(delete("/depenses/" + id)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }
}