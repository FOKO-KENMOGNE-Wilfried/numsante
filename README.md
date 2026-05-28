Carnet de Santé Numérique – Backend API
API RESTful centralisée pour le projet de carnet de santé numérique mobile. Ce backend gère les
patients, les professionnels de santé, les admissions, l’historique médical et les examens, avec
une sécurité renforcée (JWT, biométrie simulée, traçabilité).
_________________________________________________________________________________
Technologies
- Java 17 + Spring Boot 4.0.6
- Gradle (build tool)
- PostgreSQL (base de données)
- Spring Security + JWT (JSON Web Token)
- Spring Data JPA / Hibernate
- Swagger / OpenAPI 3 (documentation interactive)
- Lombok
Prérequis
- Java 17 JDK (ou compatible)
- Gradle 8+ (ou wrapper fourni)
- PostgreSQL 14+
- Un IDE (IntelliJ IDEA recommandé)

Installation et configuration

1. Cloner le projet
   git clone &lt;url-du-repo&gt;
   cd numsante
2. Créer la base de données
   Connectez-vous à PostgreSQL et créez la base care_db :
   CREATE DATABASE care_db;
   Vous pouvez également utiliser la commande createdb :
   createdb -U postgres care_db
3. Configurer application.properties
   Modifiez src/main/resources/application.properties avec vos propres paramètres :
   spring.application.name=numsante
   server.port=8081 # Changez le port si 8080 est déjà utilisé (ex: Jenkins)

# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/care_db
spring.datasource.username=postgres
spring.datasource.password=votre_mot_de_passe
spring.datasource.driver-class-name=org.postgresql.Driver
# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
# JWT (clé secrète en base64, au moins 256 bits)
jwt.secret=dGhpc2lzYXZlcnlzZWNyZXRrZXlmb3Jqd3QyNTZiaXRzMTIzNDU2Nzg=
jwt.expiration=86400000
# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
Remarque : Le server.servlet.context-path est volontairement omis pour que l’API soit accessible sous /auth
/patients, etc.
Si vous préférez un contexte /api/v1, ajoutez server.servlet.context-path=/api/v1 et adaptez les URLs en
conséquence.
4. Générer une clé JWT sécurisée (optionnel)
   Sur Linux/Mac :
   openssl rand -base64 32
   Copiez le résultat et remplacez la valeur de jwt.secret dans application.properties.
   Lancement de l’application
   Avec le wrapper Gradle (recommandé)
   ./gradlew bootRun
   L’application démarre sur http://localhost:8081 (ou le port configuré).
   Depuis IntelliJ
1. Ouvrez le projet, attendez l’import Gradle.
2. Lancez la classe NumsanteApplication (clic droit → Run).
   ⚠️ En cas d&#39;erreur ClassNotFoundException: org.slf4j.LoggerFactory dans IntelliJ :
   Changez la configuration de lancement pour utiliser Gradle au lieu d’IntelliJ IDEA (Run → Edit Configurations
   → Build and run → Run using: Gradle).
   Documentation Swagger

Une fois l’application lancée, ouvrez : http://localhost:8081/swagger-ui.html (ou avec le contexte
/api/v1 si configuré).
Vous y trouverez tous les endpoints, leurs descriptions, les corps de requête attendus et la possibilité de
les tester directement.
Authentification (pour les tests)
Un jeu de données de test est inséré automatiquement au premier démarrage grâce au DataInitializer :
- Hôpital : Hôpital Général (code unique HG-CMR)
- Médecin : dr_mballa / mot de passe passer123
1. Obtenir un token JWT
   Dans Swagger, utilisez l&#39;endpoint POST /auth/login-professionnel avec le corps suivant :
   {
   &quot;identifiantPro&quot;: &quot;dr_mballa&quot;,
   &quot;motDePasse&quot;: &quot;passer123&quot;
   }
   Copiez le token reçu dans la réponse.
2. Autoriser Swagger
   Cliquez sur le bouton Authorize (cadenas) en haut à droite, saisissez Bearer &lt;votre_token&gt;, puis validez.
   Tous les endpoints protégés sont désormais accessibles.
   Quelques endpoints clés

- Méthode Endpoint Description
- POST /auth/login-professionnel Connexion d’un professionnel
(identifiant/mot de passe)
- POST /auth/login-biometrique Connexion biométrique simulée
- POST /auth/enregistrer-biometrie Enregistrer une clé publique

biométrique

- POST /admission/scan-carte Scanner le QR code d’un patient
- POST /admission/creer-passage Créer un nouveau passage

(admission)

- GET /patients/{id}/historique Historique complet d’un patient
- PUT /passages/{id}/constantes Mettre à jour les constantes vitales
- PUT /passages/{id}/consultation Ajouter diagnostic et prescription
- POST /laboratoire/ajouter-examen Ajouter un résultat d’examen
- Consultez Swagger pour la liste exhaustive et les schémas détaillés.
- Structure du projet (principaux packages)

⚙️ Personnalisation et déploiement
- Base de données : ddl-auto=update est pratique pour le développement. Pour la production,
préférez validate et gérez les migrations avec un outil comme Flyway ou Liquibase.
- Sécurité : Les clés JWT et mots de passe doivent être stockés dans des variables d’environnement ou
un coffre-fort de mots de passe (Vault).
- Biométrie : L’authentification biométrique est simulée ; l’intégration réelle avec les API natives
(Android/iOS) nécessite l’échange de défis signés.

Contribution
Projet réalisé dans le cadre de l’UE Projet Développement Mobile (UCAC-ICAM).
Encadreurs : M. Mbiagoup Njeutcha / M. Humphrey Ojong.

Licence
Usage académique. Tous droits réservés.