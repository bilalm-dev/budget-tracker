package com.bilalmdev.budget_tracker.controller;

import com.bilalmdev.budget_tracker.dto.AuthRequest;
import com.bilalmdev.budget_tracker.dto.AuthResponse;
import com.bilalmdev.budget_tracker.model.Utilisateur;
import com.bilalmdev.budget_tracker.repository.UtilisateurRepository;
import com.bilalmdev.budget_tracker.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Cet email est déjà utilisé.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(utilisateur.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (utilisateur == null || !passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect.");
        }

        String token = jwtService.generateToken(utilisateur.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}