package com.cahiertexte.service;

import com.cahiertexte.dao.UserDAO;
import com.cahiertexte.model.User;
import com.cahiertexte.util.PasswordUtil;
import javax.servlet.http.HttpSession;

/**
 * Service de gestion de l'authentification
 * Gère la connexion, déconnexion et vérification des permissions
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class AuthenticationService {
    
    private UserDAO userDAO;
    
    // Constantes pour les clés de session
    public static final String SESSION_USER = "user";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_ROLE = "userRole";
    public static final String SESSION_USER_CLASSE = "userClasse";
    public static final String SESSION_USER_NOM = "userNom";
    
    /**
     * Constructeur
     */
    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authentifier un utilisateur
     * 
     * @param username Nom d'utilisateur
     * @param password Mot de passe en clair
     * @return User si authentification réussie, null sinon
     */
    public User authenticate(String username, String password) {
        // Vérifier que les champs ne sont pas vides
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.out.println("Authentification échouée : champs vides");
            return null;
        }
        
        // Hasher le mot de passe
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // Vérifier dans la base de données
        User user = userDAO.authenticate(username, hashedPassword);
        
        if (user != null) {
            System.out.println("✓ Authentification réussie pour : " + user.getUsername() + 
                             " (Role: " + user.getRole() + ")");
        } else {
            System.out.println("✗ Authentification échouée pour : " + username);
        }
        
        return user;
    }
    
    /**
     * Créer une session pour un utilisateur authentifié
     * 
     * @param session La session HTTP
     * @param user L'utilisateur authentifié
     */
    public void createUserSession(HttpSession session, User user) {
        if (session == null || user == null) {
            return;
        }
        
        // Stocker les informations utilisateur en session
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_USER_ID, user.getUserId());
        session.setAttribute(SESSION_USER_ROLE, user.getRole());
        session.setAttribute(SESSION_USER_CLASSE, user.getClasse());
        session.setAttribute(SESSION_USER_NOM, user.getNomComplet());
        
        // Durée de session : 2 heures (en secondes)
        session.setMaxInactiveInterval(2 * 60 * 60);
        
        System.out.println("✓ Session créée pour : " + user.getUsername());
    }
    
    /**
     * Détruire la session d'un utilisateur (déconnexion)
     * 
     * @param session La session HTTP
     */
    public void destroyUserSession(HttpSession session) {
        if (session != null) {
            String username = (String) session.getAttribute("username");
            session.invalidate();
            System.out.println("✓ Session détruite pour : " + username);
        }
    }
    
    /**
     * Vérifier si un utilisateur est connecté
     * 
     * @param session La session HTTP
     * @return true si connecté, false sinon
     */
    public boolean isUserLoggedIn(HttpSession session) {
        if (session == null) {
            return false;
        }
        
        User user = (User) session.getAttribute(SESSION_USER);
        return user != null;
    }
    
    /**
     * Récupérer l'utilisateur en session
     * 
     * @param session La session HTTP
     * @return User ou null
     */
    public User getLoggedInUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        return (User) session.getAttribute(SESSION_USER);
    }
    
    /**
     * Récupérer l'ID de l'utilisateur en session
     * 
     * @param session La session HTTP
     * @return userId ou 0 si non connecté
     */
    public int getLoggedInUserId(HttpSession session) {
        if (session == null) {
            return 0;
        }
        
        Integer userId = (Integer) session.getAttribute(SESSION_USER_ID);
        return userId != null ? userId : 0;
    }
    
    /**
     * Récupérer le rôle de l'utilisateur en session
     * 
     * @param session La session HTTP
     * @return Le rôle ou null
     */
    public String getLoggedInUserRole(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        return (String) session.getAttribute(SESSION_USER_ROLE);
    }
    
    /**
     * Vérifier si l'utilisateur a un rôle spécifique
     * 
     * @param session La session HTTP
     * @param role Le rôle à vérifier
     * @return true si l'utilisateur a ce rôle, false sinon
     */
    public boolean hasRole(HttpSession session, String role) {
        String userRole = getLoggedInUserRole(session);
        return role != null && role.equals(userRole);
    }
    
    /**
     * Vérifier si l'utilisateur est responsable de formation
     */
    public boolean isResponsableFormation(HttpSession session) {
        return hasRole(session, "RESPONSABLE_FORMATION");
    }
    
    /**
     * Vérifier si l'utilisateur est responsable de classe
     */
    public boolean isResponsableClasse(HttpSession session) {
        return hasRole(session, "RESPONSABLE_CLASSE");
    }
    
    /**
     * Vérifier si l'utilisateur est professeur
     */
    public boolean isProfesseur(HttpSession session) {
        return hasRole(session, "PROFESSEUR");
    }
    
    /**
     * Vérifier si l'utilisateur est étudiant
     */
    public boolean isEtudiant(HttpSession session) {
        return hasRole(session, "ETUDIANT");
    }
    
    /**
     * Vérifier si l'utilisateur a accès à une classe spécifique
     * 
     * @param session La session HTTP
     * @param classe La classe à vérifier
     * @return true si l'utilisateur a accès, false sinon
     */
    public boolean hasAccessToClasse(HttpSession session, String classe) {
        User user = getLoggedInUser(session);
        
        if (user == null) {
            return false;
        }
        
        // Responsable de formation a accès à toutes les classes
        if (user.isResponsableFormation()) {
            return true;
        }
        
        // Responsable de classe et étudiants : accès à leur classe uniquement
        if (user.isResponsableClasse() || user.isEtudiant()) {
            return classe != null && classe.equals(user.getClasse());
        }
        
        // Professeurs : vérifier via leurs matières (à implémenter si nécessaire)
        // Pour l'instant, on donne accès à toutes les classes pour les profs
        if (user.isProfesseur()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Changer le mot de passe d'un utilisateur
     * 
     * @param userId ID de l'utilisateur
     * @param oldPassword Ancien mot de passe
     * @param newPassword Nouveau mot de passe
     * @return true si succès, false sinon
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Récupérer l'utilisateur
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        // Vérifier l'ancien mot de passe
        String oldHashedPassword = PasswordUtil.hashPassword(oldPassword);
        if (!user.getPassword().equals(oldHashedPassword)) {
            System.out.println("✗ Ancien mot de passe incorrect");
            return false;
        }
        
        // Valider le nouveau mot de passe
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("✗ Nouveau mot de passe trop court (min 6 caractères)");
            return false;
        }
        
        // Hasher le nouveau mot de passe
        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        
        // Mettre à jour dans la base de données
        boolean success = userDAO.updatePassword(userId, newHashedPassword);
        
        if (success) {
            System.out.println("✓ Mot de passe changé pour user ID: " + userId);
        }
        
        return success;
    }
    
    /**
     * Vérifier la force d'un mot de passe
     * 
     * @param password Le mot de passe à vérifier
     * @return Message décrivant la force du mot de passe
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Faible : Le mot de passe doit contenir au moins 6 caractères";
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (hasLetter) strength++;
        if (hasDigit) strength++;
        if (hasSpecial) strength++;
        
        if (strength >= 4) {
            return "Fort : Excellent mot de passe";
        } else if (strength >= 2) {
            return "Moyen : Ajoutez des chiffres ou caractères spéciaux";
        } else {
            return "Faible : Utilisez des lettres, chiffres et caractères spéciaux";
        }
    }
    
    /**
     * Méthode main pour tester le service
     */
    public static void main(String[] args) {
        System.out.println("=== TEST AuthenticationService ===\n");
        
        AuthenticationService authService = new AuthenticationService();
        
        // Test 1 : Authentification réussie
        System.out.println("1. Test d'authentification réussie :");
        User user = authService.authenticate("prof.samb", "password123");
        if (user != null) {
            System.out.println("   ✓ Utilisateur : " + user.getNomComplet());
            System.out.println("   ✓ Rôle : " + user.getRole());
        }
        
        // Test 2 : Authentification échouée
        System.out.println("\n2. Test d'authentification échouée :");
        User userFail = authService.authenticate("prof.samb", "wrongpassword");
        if (userFail == null) {
            System.out.println("   ✓ Authentification correctement refusée");
        }
        
        // Test 3 : Vérification de force de mot de passe
        System.out.println("\n3. Test de force de mot de passe :");
        System.out.println("   '123' : " + authService.checkPasswordStrength("123"));
        System.out.println("   'password' : " + authService.checkPasswordStrength("password"));
        System.out.println("   'Pass123!' : " + authService.checkPasswordStrength("Pass123!"));
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}