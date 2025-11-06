#📚 Système de Numérisation des Cahiers de Texte - TDSI

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Servlet](https://img.shields.io/badge/Servlet-4.0-blue.svg)](https://javaee.github.io/servlet-spec/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red.svg)](https://www.microsoft.com/sql-server)
[![Tomcat](https://img.shields.io/badge/Tomcat-9.0-yellow.svg)](https://tomcat.apache.org/)

Application web J2EE de gestion numérique des cahiers de texte pour l'Institut TDSI (Université Cheikh Anta Diop de Dakar).

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Technologies](#-technologies)
- [Auteurs](#-auteurs)
- [Licence](#-licence)

## 🎯 À propos

Ce projet vise à digitaliser la gestion des cahiers de texte au sein de l'Institut TDSI. Il permet de :
- Planifier et suivre les cours
- Saisir le contenu pédagogique de chaque séance
- Gérer les présences et absences des étudiants
- Valider les cours par les professeurs
- Gérer les justificatifs d'absence
- Générer des statistiques et rapports

### Classes concernées
- **CI_M1** - Cyber-sécurité et Investigation Numérique (Master 1)
- **CI_M2** - Cyber-sécurité et Investigation Numérique (Master 2)
- **MCS_M1** - Management de la Cyber-sécurité (Master 1)
- **MCS_M2** - Management de la Cyber-sécurité (Master 2)

## ✨ Fonctionnalités

### 🎓 Pour les Responsables de Formation
- ✅ Gestion complète des utilisateurs (CRUD)
- ✅ Gestion des matières et volumes horaires
- ✅ Validation des cours réalisés
- ✅ Traitement des justificatifs d'absence
- ✅ Visualisation des alertes (matières < 12h restantes)
- ✅ Accès aux statistiques globales

### 👨‍🏫 Pour les Responsables de Classe
- ✅ Planification de nouveaux cours
- ✅ Saisie du cahier de texte
- ✅ Gestion des présences
- ✅ Consultation des statistiques de leur classe
- ✅ Vue d'ensemble des cours planifiés/réalisés

### 👨‍💼 Pour les Professeurs
- ✅ Consultation de leurs cours
- ✅ Saisie du contenu pédagogique
- ✅ Validation de leurs cours
- ✅ Gestion des présences
- ✅ Statistiques de leurs matières

### 🎓 Pour les Étudiants
- ✅ Consultation des cours de leur classe
- ✅ Visualisation de leurs absences
- ✅ Soumission de justificatifs d'absence
- ✅ Suivi de leur taux de présence

## 🏗️ Architecture

Le projet suit une architecture **MVC (Model-View-Controller)** en 3 couches :
```
┌─────────────────────────────────────┐
│         COUCHE PRÉSENTATION         │
│    (JSP + HTML/CSS + JavaScript)    │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│       COUCHE CONTRÔLEUR             │
│  (Servlets + Services métier)       │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│         COUCHE DONNÉES              │
│    (DAO + Base de données)          │
└─────────────────────────────────────┘
```

### Composants principaux

#### 📦 Model (Entités)
- `User` - Utilisateurs du système
- `Matiere` - Matières enseignées
- `Cours` - Séances de cours
- `Presence` - Présences/absences
- `Justificatif` - Justificatifs d'absence

#### 🎮 Controllers (Servlets)
- `LoginServlet` - Authentification
- `DashboardServlet` - Redirection selon le rôle
- `DashboardFormationServlet` - Tableau de bord responsable formation
- `DashboardClasseServlet` - Tableau de bord responsable classe
- `DashboardProfesseurServlet` - Tableau de bord professeur
- `DashboardEtudiantServlet` - Tableau de bord étudiant
- `CahierTexteServlet` - Saisie cahier de texte
- `PresenceServlet` - Gestion des présences
- `ValidationCoursServlet` - Validation par professeur
- `SoumettreJustificatifServlet` - Soumission justificatifs
- `ValiderJustificatifServlet` - Validation justificatifs

#### 💾 DAO (Data Access Objects)
- `UserDAO` - Gestion des utilisateurs
- `MatiereDAO` - Gestion des matières
- `CoursDAO` - Gestion des cours
- `PresenceDAO` - Gestion des présences
- `JustificatifDAO` - Gestion des justificatifs

#### 🔧 Services
- `AuthenticationService` - Gestion authentification et sessions
- `CoursService` - Logique métier cours
- `PresenceService` - Logique métier présences

## 📋 Prérequis

- **Java SE Development Kit (JDK)** 21 ou supérieur
- **Apache Tomcat** 9.0 ou supérieur
- **Microsoft SQL Server** 2019 ou supérieur
- **Eclipse IDE for Enterprise Java Developers** (recommandé) ou IntelliJ IDEA
- **Microsoft JDBC Driver for SQL Server** 13.2 ou supérieur

## 🚀 Installation

### 1. Cloner le dépôt
```bash
git clone https://github.com/votre-username/cahier-texte-tdsi.git
cd cahier-texte-tdsi
```

### 2. Configuration de SQL Server

Créez une nouvelle base de données :
```sql
CREATE DATABASE cahier_texte_db;
GO

USE cahier_texte_db;
GO
```

Exécutez le script SQL de création des tables (disponible dans `/database/schema.sql`)

### 3. Créer un utilisateur SQL Server
```sql
CREATE LOGIN admincahiertxt WITH PASSWORD = 'VotreMotDePasse';
GO

USE cahier_texte_db;
GO

CREATE USER admincahiertxt FOR LOGIN admincahiertxt;
GO

ALTER ROLE db_owner ADD MEMBER admincahiertxt;
GO
```

### 4. Télécharger le driver JDBC

Téléchargez le **Microsoft JDBC Driver for SQL Server** depuis :
https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server

Placez le fichier JAR dans le répertoire de votre projet.

### 5. Configuration dans Eclipse

1. **Importer le projet** : `File → Import → Existing Projects into Workspace`
2. **Ajouter le JDBC Driver** au Build Path :
   - Clic droit sur le projet → `Build Path → Configure Build Path`
   - Onglet `Libraries` → `Add External JARs`
   - Sélectionner le fichier `mssql-jdbc-13.2.0.jre8.jar`

3. **Configurer Tomcat** :
   - `Window → Preferences → Server → Runtime Environments`
   - `Add` → `Apache Tomcat v9.0`
   - Spécifier le chemin d'installation de Tomcat

## ⚙️ Configuration

### Fichier `DatabaseConnection.java`

Modifiez les paramètres de connexion dans `src/main/java/com/cahiertexte/dao/DatabaseConnection.java` :
```java
private static final String SERVER = "VOTRE_SERVEUR\\INSTANCE";
private static final String DATABASE = "cahier_texte_db";
private static final String USERNAME = "admincahiertxt";
private static final String PASSWORD = "VotreMotDePasse";
```

### Fichier `.classpath`

Vérifiez que le chemin vers le driver JDBC est correct :
```xml
<classpathentry kind="lib" path="C:/chemin/vers/mssql-jdbc-13.2.0.jre8.jar"/>
```

## 🎮 Utilisation

### Démarrer l'application

1. **Dans Eclipse** :
   - Clic droit sur le projet → `Run As → Run on Server`
   - Sélectionner Tomcat 9.0
   - L'application s'ouvre à : `http://localhost:8080/CahierTexteApp`

2. **Tester la connexion** :
   - Accéder à : `http://localhost:8080/CahierTexteApp/test`
   - Vérifier que la connexion à la base de données est établie

### Comptes de test

Après exécution du script de données de test :

| Rôle | Username | Mot de passe |
|------|----------|--------------|
| Responsable Formation | `rf.diop` | `password123` |
| Responsable Classe | `rc.fall` | `password123` |
| Professeur | `prof.samb` | `password123` |
| Étudiant | `etud.ndiaye` | `password123` |

## 📁 Structure du projet
```
CahierTexteApp/
├── src/main/java/
│   └── com/cahiertexte/
│       ├── controller/          # Servlets
│       │   ├── LoginServlet.java
│       │   ├── DashboardServlet.java
│       │   ├── CahierTexteServlet.java
│       │   ├── PresenceServlet.java
│       │   └── ...
│       ├── dao/                 # Data Access Objects
│       │   ├── DatabaseConnection.java
│       │   ├── UserDAO.java
│       │   ├── CoursDAO.java
│       │   └── ...
│       ├── model/               # Entités
│       │   ├── User.java
│       │   ├── Cours.java
│       │   ├── Matiere.java
│       │   └── ...
│       ├── service/             # Logique métier
│       │   ├── AuthenticationService.java
│       │   ├── CoursService.java
│       │   └── ...
│       └── util/                # Utilitaires
│           └── PasswordUtil.java
├── src/main/webapp/
│   ├── views/                   # Pages JSP
│   │   ├── login.jsp
│   │   ├── dashboard-formation.jsp
│   │   ├── dashboard-classe.jsp
│   │   └── ...
│   ├── WEB-INF/
│   │   └── web.xml
│   └── index.jsp
├── database/
│   ├── schema.sql              # Script de création des tables
│   └── data.sql                # Script de données de test
├── .classpath
├── .project
└── README.md
```

## 🛠️ Technologies

### Backend
- **Java EE** - Plateforme d'entreprise
- **Servlets 4.0** - Gestion des requêtes HTTP
- **JSP** - Pages dynamiques
- **JDBC** - Connexion base de données

### Base de données
- **Microsoft SQL Server** - SGBD relationnel
- **T-SQL** - Langage de requêtes

### Frontend
- **HTML5** - Structure
- **CSS3** - Styling
- **JavaScript** - Interactivité

### Serveur d'application
- **Apache Tomcat 9.0** - Conteneur de servlets

### Outils de développement
- **Eclipse IDE** - Environnement de développement
- **Maven** (optionnel) - Gestion des dépendances

## 📊 Modèle de données

### Tables principales

- **users** - Utilisateurs du système
- **matieres** - Matières enseignées
- **cours** - Séances de cours
- **presences** - Présences/absences
- **justificatifs** - Justificatifs d'absence

### Relations
```
users (1) ──── (*) cours (professeur)
users (1) ──── (*) matieres (professeur)
users (*) ──── (*) presences
cours (1) ──── (*) presences
cours (1) ──── (*) justificatifs
matieres (1) ──── (*) cours
```

## 🔒 Sécurité

- **Hashage des mots de passe** : SHA-256
- **Gestion des sessions** : HttpSession
- **Contrôle d'accès basé sur les rôles** (RBAC)
- **Validation des entrées utilisateur**
- **Protection contre les injections SQL** : PreparedStatements

## 📈 Fonctionnalités avancées

### Système d'alertes
- Alerte automatique si volume horaire < 12h restantes
- Notification des absences critiques (≥ 3 absences)

### Statistiques
- Taux de présence par étudiant
- Progression des matières (heures réalisées/totales)
- Top 5 des absences par classe
- Statistiques globales par classe

### Workflow de validation
1. Planification du cours (Responsable classe)
2. Saisie du cahier de texte (Responsable classe)
3. Gestion des présences (Responsable classe)
4. Validation du cours (Professeur)

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Auteurs

**Projet TDSI** - Institut TDSI, Université Cheikh Anta Diop de Dakar

Année Académique 2024-2025

## 📞 Support

Pour toute question ou problème :
- 📧 Email : support@tdsi.ucad.sn
- 🐛 Issues : [GitHub Issues](https://github.com/votre-username/cahier-texte-tdsi/issues)

## 🙏 Remerciements

- Institut TDSI - Université Cheikh Anta Diop de Dakar
- Équipe pédagogique
- Tous les contributeurs au projet

---

**Note** : Ce projet a été développé dans un cadre éducatif pour l'Institut TDSI (Université Cheikh Anta Diop de Dakar).
