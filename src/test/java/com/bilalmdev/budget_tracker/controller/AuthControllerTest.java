package com.bilalmdev.budget_tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String toJson(String email, String motDePasse) {
        return "{\"email\": \"" + email + "\", \"motDePasse\": \"" + motDePasse + "\"}";
    }

    @Test
    void inscriptionReussie() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(toJson("nouveau@test.com", "motdepasse123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void inscriptionRefuseeSiEmailDejaUtilise() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(toJson("existant@test.com", "motdepasse123")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(toJson("existant@test.com", "autremotdepasse")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void connexionReussie() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(toJson("connexion@test.com", "motdepasse123")));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(toJson("connexion@test.com", "motdepasse123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void connexionRefuseeSiMauvaisMotDePasse() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(toJson("mauvaispass@test.com", "motdepasse123")));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(toJson("mauvaispass@test.com", "mauvaispassword")))
                .andExpect(status().isUnauthorized());
    }
}