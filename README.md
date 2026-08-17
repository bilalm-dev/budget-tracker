# Budget Tracker API

API REST de suivi de budget personnel, développée en Java/Spring Boot, avec authentification 
JWT, conteneurisation Docker et déploiement cloud.

🔗 **API en ligne** : https://budget-tracker-n7bj.onrender.com
(⚠️ hébergée sur un tier gratuit : le premier appel peut prendre 30-60 secondes après une période d'inactivité)

## Fonctionnalités

- Authentification sécurisée (inscription/connexion par JWT, mots de passe hachés avec BCrypt)
- CRUD complet des dépenses (créer, lister, modifier, supprimer), avec sécurité par utilisateur
- Catégories de dépenses prédéfinies
- Résumé mensuel avec agrégation des dépenses par catégorie (requête JPQL)

## Stack technique

- **Backend** : Java 21, Spring Boot, Spring Data JPA, Spring Security
- **Base de données** : PostgreSQL (H2 pour les tests)
- **Sécurité** : JWT (jjwt)
- **Tests** : JUnit 5, MockMvc (14 tests, dont un test dédié à la sécurité inter-utilisateurs)
- **Infrastructure** : Docker (build multi-stage), Docker Compose
- **CI/CD** : GitHub Actions (build + tests automatiques à chaque push)
- **Déploiement** : Render (service web + PostgreSQL managé)

## Endpoints principaux

| Méthode | Route | Description | Authentification |
|---|---|---|---|
| POST | `/auth/register` | Inscription | Non |
| POST | `/auth/login` | Connexion | Non |
| POST | `/depenses` | Créer une dépense | Oui |
| GET | `/depenses` | Lister ses dépenses | Oui |
| PUT | `/depenses/{id}` | Modifier une dépense | Oui |
| DELETE | `/depenses/{id}` | Supprimer une dépense | Oui |
| GET | `/depenses/resume?mois=AAAA-MM` | Résumé mensuel | Oui |

## Lancer le projet en local

### Avec Docker (recommandé)

```bash
docker-compose up
```

L'application sera accessible sur `http://localhost:8080`.

### Sans Docker

1. Avoir PostgreSQL installé et une base créée
2. Configurer les variables d'environnement (voir `application.yml`)
3. `mvn spring-boot:run`

## Lancer les tests

```bash
mvn test
```

## Pistes d'amélioration

- Ajouter la gestion de budgets prévisionnels par catégorie
- Export des données (CSV/PDF)
- Catégories personnalisables par utilisateur
