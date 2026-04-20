<p align="center">
  <h1> Projet REST API : Application de location de véhicules </h1>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens" />
</p>

<h1 align="center">🚗 AutoLoc — Système de Gestion de Location de Véhicules</h1>

---

## Objectif du Projet

Ce projet a pour objectif de concevoir et développer une **application web complète basée sur une architecture REST API**, dédiée à la gestion d'une entreprise de location de voitures et de camions.

L'application expose un ensemble d'**endpoints REST sécurisés par JWT**, consommés par un frontend Angular, et déployée dans des **conteneurs Docker**. Elle couvre l'intégralité du cycle métier de la location : de l'inscription du client jusqu'à la clôture du contrat, en passant par la validation des réservations, le traitement des paiements, la génération de factures PDF et le suivi de la maintenance du parc automobile.

---

## Description

**AutoLoc** est une plateforme de gestion interne et client pour une entreprise de location de véhicules. Elle repose sur une architecture **Spring Boot en couches** (Controller → Service → Repository) côté backend, et une interface **Angluar** côté frontend.

Le système distingue quatre types d'utilisateurs aux responsabilités bien définies :

- Un **client** peut parcourir le catalogue, effectuer des réservations et régler ses paiements en ligne.
- Un **administrateur** gère le parc de véhicules, valide ou refuse les réservations, enregistre les retours et supervise les paiements.
- Un **super administrateur** dispose des mêmes droits que l'admin, avec en plus la capacité de créer et gérer d'autres comptes administrateurs.
- Un **technicien** reçoit et traite les ordres de maintenance assignés par l'admin.

L'ensemble des données est stocké dans une base **MySQL** dont le schéma est versionné avec **Flyway**, et le projet est entièrement conteneurisé avec **Docker Compose**.

---

## Staff Technique

| Membre |
|---|---|---|
| Rayane HARKATI | 
| Marouane Zaid | 
| Saad Mimouni |

---

## Analyse des Besoins

### Besoins fonctionnels

#### Authentification et gestion des comptes
- Un visiteur peut consulter les véhicules disponibles et s'inscrire lui-même en tant que client
- Un client et un admin peuvent se connecter via email et mot de passe — un token JWT est retourné
- Un admin peut créer manuellement un compte client ou un compte mécanicien
- Seul le super administrateur peut créer un compte administrateur
- Le compte super administrateur est initialisé automatiquement au premier démarrage de l'application — il ne peut pas être créé depuis l'interface web
- Tout utilisateur authentifié peut modifier son profil et changer son mot de passe

#### Gestion des clients
- L'admin peut consulter la liste des clients, voir le détail d'un client, modifier ses informations et désactiver ou supprimer son compte
- Chaque client possède un permis de conduire avec sa catégorie (B, BE, C1, C, CE…) qui est vérifié automatiquement lors d'une réservation — un client avec un permis B ne peut pas louer un camion

#### Gestion des véhicules
- L'admin peut ajouter, modifier et supprimer des voitures et des camions
- Chaque véhicule possède un statut : DISPONIBLE, LOUE, EN_MAINTENANCE ou HORS_SERVICE
- Les véhicules peuvent avoir des options (GPS, climatisation, siège bébé, hayon…) — relation Many-to-Many avec la table `vehicule_option`
- L'admin peut signaler une panne sur un véhicule, ce qui crée automatiquement un ordre de maintenance et passe le véhicule en EN_MAINTENANCE

#### Gestion des réservations
- Un client peut réserver un véhicule pour une période donnée, modifier ou annuler sa réservation
- Le système vérifie automatiquement la disponibilité du véhicule et la validité du permis avant de créer la réservation
- Le montant total est calculé automatiquement (durée × prix par jour)
- L'admin valide ou refuse chaque réservation — le client est notifié automatiquement
- L'admin enregistre le retour du véhicule avec la date réelle — des frais de retard sont calculés automatiquement en cas de retard

#### Gestion des paiements
- Un client peut régler sa réservation confirmée en ligne
- Chaque réservation peut inclure une ou plusieurs assurances (responsabilité civile, vol, bris de glace…) — relation Many-to-Many avec la table `reservation_assurance`
- Une facture PDF est générée et envoyée par email automatiquement après chaque paiement confirmé
- L'admin peut confirmer manuellement un paiement en espèces ou déclencher un remboursement

#### Gestion de la maintenance
- L'admin crée un ordre de maintenance et assigne un mécanicien disponible
- Le mécanicien reçoit une notification, démarre la réparation et la clôture en renseignant le coût réel
- À la clôture, le véhicule repasse automatiquement en statut DISPONIBLE

#### Notifications
- Chaque événement métier déclenche une notification : confirmation de réservation, validation, refus, paiement reçu, retour enregistré, ordre de maintenance assigné
- Les notifications sont stockées en base et envoyées par email

#### Rapports (Admin)
- L'admin peut générer des rapports sur les locations, le chiffre d'affaires et l'activité des clients sur une période donnée

### Besoins non fonctionnels

| Besoin | Solution |
|---|---|
| Sécurité | Spring Security + JWT, BCrypt pour les mots de passe, @PreAuthorize par rôle |
| Validation | @Valid sur tous les DTOs entrants, contraintes CHECK en base de données |
| Portabilité | Conteneurisation complète Docker Compose |
| Traçabilité | Flyway pour les migrations SQL, timestamps sur toutes les entités |
| Maintenabilité | Architecture en couches, séparation entité/DTO avec MapStruct |
| Documentation | Swagger UI auto-générée depuis les annotations Spring |

---

## Conception et Modélisation

### Diagramme de Cas d'Utilisations

> 📎 `docs/diagrammes/use_case.png`

Le système identifie **4 acteurs** principaux :

```
Visiteur      → Consulter véhicules, S'inscrire, Se connecter
Client        → Réserver, Annuler, Payer, Consulter mes réservations
Admin         → Valider/Refuser, Gérer véhicules/clients, Retour, Maintenance
Mécanicien    → Réceptionner ordre, Démarrer réparation, Clôturer
```

Relations UML notables :
- `«include»` — Réserver un véhicule **inclut** Vérifier disponibilité et Calculer montant
- `«include»` — Valider réservation **inclut** Notifier le client
- `«extend»` — Enregistrer retour **étend** Calculer frais de retard _(si retard constaté)_
- `«extend»` — Annuler réservation **étend** Calculer remboursement _(si paiement déjà effectué)_
- `«extend»` — Consulter véhicules **étend** Filtrer par type/prix _(optionnel)_

---

### Diagramme de Classes

> 📎 `docs/diagrammes/class_diagram.png`

#### Hiérarchie des utilisateurs

```
Utilisateur (abstract)
│   id, nom, prenom, email, motDePasse, telephone, role, actif
│   + seConnecter(), seDeconnecter(), modifierProfil()
│
├── Admin
│   │   niveauAcces, derniereConnexion
│   │   + creerCompteClient(), creerCompteMecanicien()
│   │   + validerReservation(), refuserReservation()
│   │   + ajouterVehicule(), signalerPanne()
│   │
│   └── SuperAdmin
│           + creerCompteAdmin(), desactiverAdmin()
│
├── Client
│   │   adresse, dateNaissance
│   │   + sInscrire(), reserverVehicule(), reglerPaiement()
│   │
│   └── PermisConduire (composition *--)
│           numero, categorie, dateExpiration, paysEmission
│           + estValide(), autoriseVoiture(), autoriseCamion(tonnage)
│
└── Mecanicien
        specialite, disponible
        + demarrerReparation(), cloturerReparation()
```

#### Hiérarchie des véhicules

```
Vehicule (abstract)
│   id, marque, modele, immatriculation, prixParJour, caution, statut
│   + estDisponible(debut, fin), changerStatut()
│
├── Voiture      — nbPortes, typeCarburant, typeBoite, nbPlaces
└── Camion       — tonnage, volumeM3, longueur, hayonElevateur
```

#### Relations Many-to-Many

| Relation | Table pivot | Attributs propres |
|---|---|---|
| `Vehicule ↔ OptionVehicule` | `vehicule_option` | `disponible`, `date_ajout` |
| `Reservation ↔ Assurance` | `reservation_assurance` | `montant_applique`, `date_souscription` |

#### Énumérations

| Enum | Valeurs |
|---|---|
| `Role` | SUPER_ADMIN, ADMIN, CLIENT, MECANICIEN |
| `NiveauAcces` | SUPER_ADMIN, ADMIN_STANDARD |
| `StatutVehicule` | DISPONIBLE, LOUE, EN_MAINTENANCE, HORS_SERVICE |
| `StatutReservation` | EN_ATTENTE, CONFIRMEE, REFUSEE, EN_COURS, TERMINEE, ANNULEE |
| `StatutPaiement` | EN_ATTENTE, CONFIRME, ECHOUE, REMBOURSE |
| `StatutMaintenance` | SIGNALE, ASSIGNE, EN_COURS, RESOLU |
| `CategoriePermis` | B, BE, C1, C1E, C, CE |

---

### Modèle de Données (MCD)

> 📎 `docs/diagrammes/mcd.png`

#### Stratégie d'héritage : JOINED (table par sous-classe)

Les tables `CLIENT`, `ADMIN` et `MECANICIEN` ne possèdent **pas** de colonne `id` avec AUTO_INCREMENT. Leur clé primaire est à la fois PK et FK vers `UTILISATEUR.id`. JPA gère cela via `@PrimaryKeyJoinColumn`.

```
UTILISATEUR (id AUTO_INCREMENT)  ←──PK=FK──  CLIENT
                                 ←──PK=FK──  ADMIN
                                 ←──PK=FK──  MECANICIEN

VEHICULE    (id AUTO_INCREMENT)  ←──PK=FK──  VOITURE
                                 ←──PK=FK──  CAMION
```

#### Cardinalités principales

```
UTILISATEUR  ||──o{  NOTIFICATION       (1 utilisateur reçoit 0..N notifications)
CLIENT       ||──o{  RESERVATION        (1 client passe 0..N réservations)
VEHICULE     ||──o{  RESERVATION        (1 véhicule est réservé 0..N fois)
RESERVATION  ||──o|  PAIEMENT           (1 réservation génère 0..1 paiement)
PAIEMENT     ||──||  FACTURE            (1 paiement produit 1 facture)
VEHICULE     ||──o{  ORDRE_MAINTENANCE  (1 véhicule fait l'objet de 0..N ordres)
VEHICULE     }o──o{  OPTION_VEHICULE    (M:N via vehicule_option)
RESERVATION  }o──o{  ASSURANCE          (M:N via reservation_assurance)
```

---

## Stack Technique

### Outils de développement

| Catégorie | Technologie | Rôle |
|---|---|---|
| **Langage** | Java 17 | Langage principal backend |
| **Framework** | Spring Boot 3.x | Socle de l'application REST |
| **Sécurité** | Spring Security + JWT | Authentification, autorisation par rôle |
| **ORM** | Spring Data JPA + Hibernate | Mapping objet ↔ base de données |
| **Validation** | Spring Validation | Validation automatique des DTOs |
| **Mapping** | MapStruct | Conversion entité ↔ DTO |
| **Boilerplate** | Lombok | Génération automatique getters/setters/builders |
| **PDF** | OpenPDF | Génération des factures |
| **Email** | Spring Mail + Maildev | Envoi et test des notifications email |
| **Build** | Maven | Gestion des dépendances, compilation, packaging |
| **BDD principale** | MySQL 8 | Stockage relationnel des données |
| **Migration BDD** | Flyway | Versioning du schéma SQL |
| **BDD tests** | H2 | Base en mémoire pour les tests unitaires |
| **Frontend** | React + Vite | Interface utilisateur (SPA) |
| **HTTP Client** | Axios | Appels REST depuis le frontend |
| **CSS** | Tailwind CSS | Stylisation de l'interface |
| **Routing** | React Router | Navigation et routes protégées par rôle |
| **Tests** | JUnit 5 + Mockito | Tests unitaires backend |
| **Doc API** | Swagger / OpenAPI 3 | Documentation interactive auto-générée |

### Outils de partage et collaboration

| Outil | Usage |
|---|---|
| **Git** | Versioning local du code |
| **GitHub** | Hébergement du dépôt, Pull Requests, revue de code |
| **GitHub Actions** | CI/CD — exécution automatique des tests à chaque push |
| **Railway.app** | Hébergement de la base de données MySQL partagée entre les membres |
| **Docker + Docker Compose** | Conteneurisation — même environnement garanti pour tous |
| **Postman** | Tests manuels et partage des collections d'endpoints REST |
| **Discord / WhatsApp** | Communication d'équipe et partage des credentials (jamais sur GitHub) |

> ⚠️ Les credentials de la base de données (host, port, user, password) ne doivent **jamais** être committés sur GitHub. Chaque membre configure son fichier `application-local.properties` localement, ce fichier étant listé dans `.gitignore`.

---

## Architecture du Projet

### Vue d'ensemble

```
┌──────────────────────────────────────────────────────────┐
│                     CLIENT (Navigateur)                   │
│                   React + Vite + Axios                    │
└─────────────────────────┬────────────────────────────────┘
                          │  HTTP + Bearer JWT
                          ▼
┌──────────────────────────────────────────────────────────┐
│                  SPRING BOOT (port 8080)                  │
│                                                          │
│  ┌─────────────┐   ┌─────────────┐   ┌───────────────┐  │
│  │  Security   │──▶│ Controller  │──▶│   Service     │  │
│  │ JWT Filter  │   │@RestCtrl    │   │ @Service      │  │
│  │ @PreAuth... │   │DTO Validation│  │ Logique métier│  │
│  └─────────────┘   └─────────────┘   └───────┬───────┘  │
│                                              │           │
│                         ┌────────────────────┤           │
│                         ▼                    ▼           │
│                  ┌─────────────┐   ┌──────────────────┐  │
│                  │ Repository  │   │ Services transv. │  │
│                  │ JpaRepo...  │   │ NotifService     │  │
│                  └──────┬──────┘   │ PdfService       │  │
│                         │          │ MailService      │  │
│                         │          └──────────────────┘  │
└─────────────────────────┼────────────────────────────────┘
                          │  JPA / Hibernate / Flyway
                          ▼
┌──────────────────────────────────────────────────────────┐
│              MySQL 8  (Railway.app / Docker)              │
│         Schéma versionné avec Flyway migrations           │
└──────────────────────────────────────────────────────────┘
```

### Architecture en couches — responsabilités

| Couche | Annotation | Responsabilité unique |
|---|---|---|
| **Controller** | `@RestController` | Reçoit la requête HTTP, valide le DTO avec `@Valid`, appelle le Service, renvoie `ResponseEntity` |
| **Service** | `@Service` `@Transactional` | Contient toute la logique métier : règles, calculs, coordonne les Repositories et services transversaux |
| **Repository** | `@Repository` | Unique point de contact avec la BDD — traduit les objets Java en SQL via JPA/Hibernate |
| **Service transversal** | `@Service` | Responsabilité technique unique : NotifService (email), PdfService (PDF), MailService |
| **Security** | `Filter` | Intercepte chaque requête, valide le JWT, injecte l'utilisateur dans le contexte Spring |

### Structure des packages

```
src/
├── main/
│   ├── java/com/location/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java       # Chaîne de filtres Spring Security
│   │   │   ├── JwtConfig.java            # Clé secrète, durée du token
│   │   │   └── DataInitializer.java      # Création SUPER_ADMIN au démarrage
│   │   ├── controller/
│   │   │   ├── AuthController.java       # POST /auth/login, /auth/register
│   │   │   ├── ClientController.java     # /api/clients
│   │   │   ├── VehiculeController.java   # /api/vehicules
│   │   │   ├── ReservationController.java# /api/reservations
│   │   │   ├── PaiementController.java   # /api/paiements
│   │   │   └── MaintenanceController.java# /api/maintenance
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── ClientService.java
│   │   │   ├── VehiculeService.java
│   │   │   ├── ReservationService.java
│   │   │   ├── PaiementService.java
│   │   │   ├── MaintenanceService.java
│   │   │   ├── NotificationService.java  # Transversal — email + BDD
│   │   │   └── PdfService.java           # Transversal — génération PDF
│   │   ├── repository/
│   │   │   ├── UtilisateurRepository.java
│   │   │   ├── ClientRepository.java
│   │   │   ├── VehiculeRepository.java
│   │   │   ├── ReservationRepository.java
│   │   │   ├── PaiementRepository.java
│   │   │   └── MaintenanceRepository.java
│   │   ├── model/
│   │   │   ├── Utilisateur.java          # @Entity @Inheritance(JOINED)
│   │   │   ├── Admin.java                # @PrimaryKeyJoinColumn
│   │   │   ├── SuperAdmin.java           # @PrimaryKeyJoinColumn
│   │   │   ├── Client.java               # @PrimaryKeyJoinColumn
│   │   │   ├── Mecanicien.java           # @PrimaryKeyJoinColumn
│   │   │   ├── Vehicule.java             # @Entity @Inheritance(JOINED)
│   │   │   ├── Voiture.java
│   │   │   ├── Camion.java
│   │   │   ├── Reservation.java
│   │   │   ├── Paiement.java
│   │   │   ├── Facture.java
│   │   │   ├── Notification.java
│   │   │   ├── OrdreMaintenance.java
│   │   │   ├── OptionVehicule.java
│   │   │   ├── Assurance.java
│   │   │   └── embeddable/
│   │   │       └── PermisConduire.java   # @Embeddable
│   │   ├── dto/                          # Un DTO par entité (request + response)
│   │   ├── enums/                        # Role, StatutVehicule, CategoriePermis...
│   │   ├── exception/                    # Exceptions métier personnalisées
│   │   │   ├── VehiculeNotFoundException.java
│   │   │   ├── PermisInsuffisantException.java
│   │   │   └── GlobalExceptionHandler.java  # @ControllerAdvice
│   │   └── security/
│   │       ├── JwtFilter.java            # Intercepte et valide le token
│   │       └── UserDetailsServiceImpl.java
│   └── resources/
│       ├── application.properties        # Config commune (à committer)
│       ├── application-local.properties  # Credentials locaux (dans .gitignore)
│       └── db/migration/                 # Scripts Flyway versionnés
│           ├── V1__create_tables.sql
│           ├── V2__insert_enums.sql
│           └── V3__add_constraints.sql
└── test/
    └── java/com/location/
        ├── service/                      # Tests unitaires avec Mockito
        └── controller/                   # Tests intégration avec MockMvc
```

### Docker Compose — 4 services

```yaml
services:

  mysql:          # Base de données MySQL 8
    image: mysql:8.0
    port: 3306

  app:            # API Spring Boot
    build: .
    port: 8080
    depends_on: mysql

  phpmyadmin:     # Interface graphique base de données
    image: phpmyadmin
    port: 8081

  maildev:        # Faux serveur SMTP pour tester les emails
    image: maildev/maildev
    port: 1080    # Interface web des emails
```

### Workflow Git

```
main ──────────────────────────────────────────► (production stable)
  │
develop ───────────────────────────────────────► (intégration)
  │
  ├── feature/auth          (login, register, JWT)
  ├── feature/vehicules      (CRUD voitures/camions, options)
  ├── feature/reservations   (réservation, validation, retour)
  ├── feature/paiements      (paiement, facture PDF)
  └── feature/maintenance    (ordres, mécanicien)
```

Chaque `feature/*` fait l'objet d'une **Pull Request** sur GitHub, relue par un autre membre avant le merge sur `develop`.

### Endpoints REST principaux

```
POST   /api/auth/register           Inscription client
POST   /api/auth/login              Connexion → JWT

GET    /api/vehicules               Liste des véhicules (public)
POST   /api/vehicules               Ajouter un véhicule       [ADMIN]
PUT    /api/vehicules/{id}          Modifier un véhicule      [ADMIN]
DELETE /api/vehicules/{id}          Supprimer un véhicule     [ADMIN]

POST   /api/reservations            Créer une réservation     [CLIENT]
PATCH  /api/reservations/{id}/valider   Valider              [ADMIN]
PATCH  /api/reservations/{id}/refuser   Refuser              [ADMIN]
PATCH  /api/reservations/{id}/retour    Enregistrer retour   [ADMIN]

POST   /api/paiements               Régler un paiement        [CLIENT]
GET    /api/paiements/{id}/facture  Télécharger facture PDF   [CLIENT]

POST   /api/maintenance             Créer ordre maintenance   [ADMIN]
PATCH  /api/maintenance/{id}/cloturer  Clôturer réparation   [MECANICIEN]

GET    /api/clients                 Liste des clients         [ADMIN]
POST   /api/clients                 Créer un client           [ADMIN]
```

> 📖 Documentation complète et interactive disponible sur `http://localhost:8080/swagger-ui.html` après démarrage.

---

## Démarrage rapide

```bash
# 1. Cloner le dépôt
git clone https://github.com/votre-compte/autoloc.git
cd autoloc

# 2. Configurer les credentials (ne jamais committer ce fichier)
cp src/main/resources/application-example.properties \
   src/main/resources/application-local.properties
# → Remplir avec les credentials Railway ou MySQL local

# 3. Lancer tous les services avec Docker
docker-compose up -d

# 4. Accéder à l'application
#  API REST     →  http://localhost:8080/api
#  Swagger UI   →  http://localhost:8080/swagger-ui.html
#  phpMyAdmin   →  http://localhost:8081
#  Maildev      →  http://localhost:1080
```

---

## Organisation du dépôt

```
autoloc/
├── src/                        # Code source Spring Boot
├── frontend/                   # Application React + Vite
├── docs/
│   ├── diagrammes/
│   │   ├── use_case.png
│   │   ├── class_diagram.png
│   │   ├── mcd.png
│   │   └── sequences/
│   └── sql/
│       └── tables_utilisateurs.sql
├── docker-compose.yml
├── Dockerfile
├── .gitignore                  # Inclut application-local.properties
└── README.md
```

---

<p align="center">
  Projet académique — Génie Logiciel / Développement Web
</p>

