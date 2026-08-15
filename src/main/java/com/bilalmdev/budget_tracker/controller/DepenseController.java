package com.bilalmdev.budget_tracker.controller;

import com.bilalmdev.budget_tracker.dto.CategorieTotal;
import com.bilalmdev.budget_tracker.dto.DepenseRequest;
import com.bilalmdev.budget_tracker.dto.DepenseResponse;
import com.bilalmdev.budget_tracker.dto.ResumeMensuelResponse;
import com.bilalmdev.budget_tracker.model.Categorie;
import com.bilalmdev.budget_tracker.model.Depense;
import com.bilalmdev.budget_tracker.model.Utilisateur;
import com.bilalmdev.budget_tracker.repository.CategorieRepository;
import com.bilalmdev.budget_tracker.repository.DepenseRepository;
import com.bilalmdev.budget_tracker.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/depenses")
public class DepenseController {

    private final DepenseRepository depenseRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    public DepenseController(DepenseRepository depenseRepository,
                              CategorieRepository categorieRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.depenseRepository = depenseRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Méthode utilitaire : récupère l'utilisateur connecté à partir du token
    private Utilisateur getUtilisateurConnecte(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private DepenseResponse toResponse(Depense d) {
        return new DepenseResponse(d.getId(), d.getMontant(), d.getDescription(), d.getDate(), d.getCategorie().getNom());
    }

    @PostMapping
    public ResponseEntity<?> creer(@RequestBody DepenseRequest request, Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);
        Categorie categorie = categorieRepository.findById(request.getCategorieId())
                .orElse(null);

        if (categorie == null) {
            return ResponseEntity.badRequest().body("Catégorie introuvable.");
        }

        Depense depense = new Depense();
        depense.setMontant(request.getMontant());
        depense.setDescription(request.getDescription());
        depense.setDate(request.getDate());
        depense.setUtilisateur(utilisateur);
        depense.setCategorie(categorie);

        depenseRepository.save(depense);
        return ResponseEntity.ok(toResponse(depense));
    }

    @GetMapping
    public List<DepenseResponse> lister(Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);
        return depenseRepository.findByUtilisateurId(utilisateur.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(@PathVariable Long id, @RequestBody DepenseRequest request, Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);
        Depense depense = depenseRepository.findById(id).orElse(null);

        if (depense == null || !depense.getUtilisateur().getId().equals(utilisateur.getId())) {
            return ResponseEntity.status(404).body("Dépense introuvable.");
        }

        Categorie categorie = categorieRepository.findById(request.getCategorieId()).orElse(null);
        if (categorie == null) {
            return ResponseEntity.badRequest().body("Catégorie introuvable.");
        }

        depense.setMontant(request.getMontant());
        depense.setDescription(request.getDescription());
        depense.setDate(request.getDate());
        depense.setCategorie(categorie);

        depenseRepository.save(depense);
        return ResponseEntity.ok(toResponse(depense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id, Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);
        Depense depense = depenseRepository.findById(id).orElse(null);

        if (depense == null || !depense.getUtilisateur().getId().equals(utilisateur.getId())) {
            return ResponseEntity.status(404).body("Dépense introuvable.");
        }

        depenseRepository.delete(depense);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resume")
    public ResponseEntity<?> resumeMensuel(@RequestParam String mois, Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);

        LocalDate debutMois = LocalDate.parse(mois + "-01");
        LocalDate finMois = debutMois.plusMonths(1);

        List<Object[]> resultats = depenseRepository.sommeParCategorie(utilisateur.getId(), debutMois, finMois);

        List<CategorieTotal> parCategorie = resultats.stream()
                .map(r -> new CategorieTotal((String) r[0], (BigDecimal) r[1]))
                .toList();

        BigDecimal totalMensuel = parCategorie.stream()
                .map(CategorieTotal::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(new ResumeMensuelResponse(mois, totalMensuel, parCategorie));
    }
}