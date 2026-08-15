package com.bilalmdev.budget_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private final String email = "test@example.com";

    @Test
    void genereUnTokenValide() {
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extraitCorrectementLEmailDuToken() {
        String token = jwtService.generateToken(email);

        String emailExtrait = jwtService.extractEmail(token);

        assertEquals(email, emailExtrait);
    }

    @Test
    void valideUnTokenCorrectPourLeBonEmail() {
        String token = jwtService.generateToken(email);

        boolean estValide = jwtService.isTokenValid(token, email);

        assertTrue(estValide);
    }

    @Test
    void rejetteUnTokenPourUnAutreEmail() {
        String token = jwtService.generateToken(email);

        boolean estValide = jwtService.isTokenValid(token, "autre@example.com");

        assertFalse(estValide);
    }
}