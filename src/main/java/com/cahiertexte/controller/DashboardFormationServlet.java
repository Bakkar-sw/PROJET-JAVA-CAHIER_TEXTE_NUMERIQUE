package com.cahiertexte.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.dao.JustificatifDAO;
import com.cahiertexte.dao.MatiereDAO;
import com.cahiertexte.dao.UserDAO;
import com.cahiertexte.model.Cours;
import com.cahiertexte.model.Justificatif;
import com.cahiertexte.model.Matiere;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;

/**
 * Servlet Dashboard Responsable de Formation
 * Gestion complète : Utilisateurs, Matières, Cours, Justificatifs
 * 
 * URL: /dashboard-formation
 * 
 * @author Projet TDSI
 * @version 2.0
 */
@WebServlet("/dashboard-formation")
public class DashboardFormationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    private JustificatifDAO justificatifDAO;
    private UserDAO userDAO;
    private MatiereDAO matiereDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
        justificatifDAO = new JustificatifDAO();
        userDAO = new UserDAO();
        matiereDAO = new MatiereDAO();
    }
    
    /**
     * Afficher le dashboard avec toutes les données
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Récupérer l'utilisateur
        User user = authService.getLoggedInUser(session);
        
        // Vérifier le rôle
        if (!"RESPONSABLE_FORMATION".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Préparer les données pour la vue
        request.setAttribute("user", user);
        
        // === DASHBOARD ===
        // Cours non validés
        List<Cours> coursNonValides = coursService.getCoursNonValides();
        request.setAttribute("coursNonValides", coursNonValides);
        
        // Alertes matières (< 12h restantes)
        List<Map<String, Object>> alertes = coursService.getMatieresAvecAlerte();
        request.setAttribute("alertes", alertes);
        
        // Justificatifs en attente
        List<Justificatif> justificatifsEnAttente = justificatifDAO.getJustificatifsEnAttente();
        request.setAttribute("justificatifsEnAttente", justificatifsEnAttente);
        
        // === GESTION UTILISATEURS ===
        List<User> allUsers = userDAO.getAllUsers();
        request.setAttribute("allUsers", allUsers);
        
        // === GESTION MATIÈRES ===
        List<Matiere> allMatieres = matiereDAO.getAllMatieres();
        request.setAttribute("allMatieres", allMatieres);
        
        // === JUSTIFICATIFS (tous statuts) ===
        List<Justificatif> allJustificatifs = justificatifDAO.getAllJustificatifs();
        request.setAttribute("allJustificatifs", allJustificatifs);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/dashboard-formation.jsp").forward(request, response);
    }
    
    /**
     * Gérer les actions CRUD
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = authService.getLoggedInUser(session);
        if (!"RESPONSABLE_FORMATION".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            if (action != null) {
                switch (action) {
                    // === GESTION UTILISATEURS ===
                    case "createUser":
                        createUser(request, session);
                        break;
                    case "updateUser":
                        updateUser(request, session);
                        break;
                    case "deleteUser":
                        deleteUser(request, session);
                        break;
                    
                    // === GESTION MATIÈRES ===
                    case "createMatiere":
                        createMatiere(request, session);
                        break;
                    case "updateMatiere":
                        updateMatiere(request, session);
                        break;
                    case "deleteMatiere":
                        deleteMatiere(request, session);
                        break;
                    
                    default:
                        session.setAttribute("errorMessage", "Action inconnue");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'action : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        
        // Rediriger vers le dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard-formation");
    }
    
    // ========== MÉTHODES GESTION UTILISATEURS ==========
    
    private void createUser(HttpServletRequest request, HttpSession session) {
        try {
            User newUser = new User();
            newUser.setUsername(request.getParameter("username"));
            newUser.setPassword(hashPassword(request.getParameter("password")));
            newUser.setNom(request.getParameter("nom"));
            newUser.setPrenom(request.getParameter("prenom"));
            newUser.setEmail(request.getParameter("email"));
            newUser.setTelephone(request.getParameter("telephone"));
            newUser.setRole(request.getParameter("role"));
            newUser.setClasse(request.getParameter("classe"));
            newUser.setStatut("ACTIF");
            
            boolean success = userDAO.createUser(newUser);
            
            if (success) {
                session.setAttribute("successMessage", "✓ Utilisateur créé avec succès !");
            } else {
                session.setAttribute("errorMessage", "✗ Erreur lors de la création de l'utilisateur");
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    private void updateUser(HttpServletRequest request, HttpSession session) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = userDAO.getUserById(userId);
            
            if (user != null) {
                user.setUsername(request.getParameter("username"));
                user.setNom(request.getParameter("nom"));
                user.setPrenom(request.getParameter("prenom"));
                user.setEmail(request.getParameter("email"));
                user.setTelephone(request.getParameter("telephone"));
                user.setRole(request.getParameter("role"));
                user.setClasse(request.getParameter("classe"));
                user.setStatut(request.getParameter("statut"));
                
                // Mettre à jour le mot de passe si fourni
                String newPassword = request.getParameter("password");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    userDAO.updatePassword(userId, hashPassword(newPassword));
                }
                
                boolean success = userDAO.updateUser(user);
                
                if (success) {
                    session.setAttribute("successMessage", "✓ Utilisateur modifié avec succès !");
                } else {
                    session.setAttribute("errorMessage", "✗ Erreur lors de la modification");
                }
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpSession session) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            boolean success = userDAO.deleteUser(userId);
            
            if (success) {
                session.setAttribute("successMessage", "✓ Utilisateur supprimé avec succès !");
            } else {
                session.setAttribute("errorMessage", "✗ Erreur lors de la suppression");
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    // ========== MÉTHODES GESTION MATIÈRES ==========
    
    private void createMatiere(HttpServletRequest request, HttpSession session) {
        try {
            // Récupérer et valider les paramètres
            String codeMatiere = request.getParameter("codeMatiere");
            String nomMatiere = request.getParameter("nomMatiere");
            String classe = request.getParameter("classe");
            String volumeHoraire = request.getParameter("volumeHoraire");
            String coefficient = request.getParameter("coefficient");
            String semestre = request.getParameter("semestre");
            String anneeAcademique = request.getParameter("anneeAcademique");
            String profIdStr = request.getParameter("professeurId");
            
            // Debug
            System.out.println("=== CREATE MATIERE ===");
            System.out.println("Code: " + codeMatiere);
            System.out.println("Nom: " + nomMatiere);
            System.out.println("Classe: " + classe);
            System.out.println("Volume: " + volumeHoraire);
            System.out.println("Coef: " + coefficient);
            System.out.println("Semestre: " + semestre);
            System.out.println("Année: " + anneeAcademique);
            System.out.println("ProfId: " + profIdStr);
            
            // Validation
            if (codeMatiere == null || nomMatiere == null || classe == null || 
                volumeHoraire == null || coefficient == null || semestre == null || anneeAcademique == null) {
                session.setAttribute("errorMessage", "✗ Tous les champs obligatoires doivent être remplis");
                return;
            }
            
            Matiere matiere = new Matiere();
            matiere.setCodeMatiere(codeMatiere);
            matiere.setNomMatiere(nomMatiere);
            matiere.setClasse(classe);
            matiere.setVolumeHoraireTotal(new BigDecimal(volumeHoraire));
            matiere.setCoefficient(Integer.parseInt(coefficient));
            matiere.setSemestre(semestre);
            matiere.setAnneeAcademique(anneeAcademique);
            
            if (profIdStr != null && !profIdStr.isEmpty() && !profIdStr.equals("")) {
                matiere.setProfesseurId(Integer.parseInt(profIdStr));
            } else {
                matiere.setProfesseurId(0);
            }
            
            boolean success = matiereDAO.createMatiere(matiere);
            
            if (success) {
                System.out.println("✓ Matière créée avec succès ID: " + matiere.getMatiereId());
                session.setAttribute("successMessage", "✓ Matière créée avec succès !");
            } else {
                System.err.println("✗ Échec de la création en base de données");
                session.setAttribute("errorMessage", "✗ Erreur lors de la création de la matière en base");
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur format nombre : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "✗ Erreur : Format de nombre invalide");
        } catch (Exception e) {
            System.err.println("Erreur création matière : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    private void updateMatiere(HttpServletRequest request, HttpSession session) {
        try {
            String matiereIdStr = request.getParameter("matiereId");
            
            // Debug
            System.out.println("=== UPDATE MATIERE ===");
            System.out.println("MatiereId: " + matiereIdStr);
            
            if (matiereIdStr == null || matiereIdStr.isEmpty()) {
                session.setAttribute("errorMessage", "✗ ID de matière manquant");
                return;
            }
            
            int matiereId = Integer.parseInt(matiereIdStr);
            Matiere matiere = matiereDAO.getMatiereById(matiereId);
            
            if (matiere == null) {
                System.err.println("✗ Matière non trouvée ID: " + matiereId);
                session.setAttribute("errorMessage", "✗ Matière non trouvée");
                return;
            }
            
            // Récupérer les paramètres
            String codeMatiere = request.getParameter("codeMatiere");
            String nomMatiere = request.getParameter("nomMatiere");
            String classe = request.getParameter("classe");
            String volumeHoraire = request.getParameter("volumeHoraire");
            String coefficient = request.getParameter("coefficient");
            String semestre = request.getParameter("semestre");
            String anneeAcademique = request.getParameter("anneeAcademique");
            String profIdStr = request.getParameter("professeurId");
            
            System.out.println("Code: " + codeMatiere);
            System.out.println("Nom: " + nomMatiere);
            System.out.println("Classe: " + classe);
            System.out.println("Volume: " + volumeHoraire);
            System.out.println("Coef: " + coefficient);
            System.out.println("Semestre: " + semestre);
            System.out.println("Année: " + anneeAcademique);
            System.out.println("ProfId: " + profIdStr);
            
            // Validation
            if (codeMatiere == null || nomMatiere == null || classe == null || 
                volumeHoraire == null || coefficient == null || semestre == null || anneeAcademique == null) {
                session.setAttribute("errorMessage", "✗ Tous les champs obligatoires doivent être remplis");
                return;
            }
            
            matiere.setCodeMatiere(codeMatiere);
            matiere.setNomMatiere(nomMatiere);
            matiere.setClasse(classe);
            matiere.setVolumeHoraireTotal(new BigDecimal(volumeHoraire));
            matiere.setCoefficient(Integer.parseInt(coefficient));
            matiere.setSemestre(semestre);
            matiere.setAnneeAcademique(anneeAcademique);
            
            if (profIdStr != null && !profIdStr.isEmpty() && !profIdStr.equals("")) {
                matiere.setProfesseurId(Integer.parseInt(profIdStr));
            } else {
                matiere.setProfesseurId(0);
            }
            
            boolean success = matiereDAO.updateMatiere(matiere);
            
            if (success) {
                System.out.println("✓ Matière modifiée avec succès ID: " + matiereId);
                session.setAttribute("successMessage", "✓ Matière modifiée avec succès !");
            } else {
                System.err.println("✗ Échec de la modification en base de données");
                session.setAttribute("errorMessage", "✗ Erreur lors de la modification en base");
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur format nombre : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "✗ Erreur : Format de nombre invalide");
        } catch (Exception e) {
            System.err.println("Erreur modification matière : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    private void deleteMatiere(HttpServletRequest request, HttpSession session) {
        try {
            int matiereId = Integer.parseInt(request.getParameter("matiereId"));
            boolean success = matiereDAO.deleteMatiere(matiereId);
            
            if (success) {
                session.setAttribute("successMessage", "✓ Matière supprimée avec succès !");
            } else {
                session.setAttribute("errorMessage", "✗ Erreur lors de la suppression");
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
    }
    
    // ========== UTILITAIRES ==========
    
    /**
     * Hasher un mot de passe (SHA-256)
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
}