package com.cahiertexte.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.model.Cours;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;

/**
 * Servlet de saisie du cahier de texte
 * URL: /cahier-texte?coursId=X
 */
@WebServlet("/cahier-texte")
public class CahierTexteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
    }
    
    /**
     * GET : Afficher le formulaire de saisie
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = authService.getLoggedInUser(session);
        
        // Récupérer le coursId
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null || coursIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "ID du cours manquant");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        int coursId;
        try {
            coursId = Integer.parseInt(coursIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID du cours invalide");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null) {
            session.setAttribute("errorMessage", "Cours non trouvé");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Vérifier les droits d'accès
        if (!hasAccessToCahierTexte(user, cours)) {
            session.setAttribute("errorMessage", "Vous n'avez pas accès à ce cours");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Vérifier si le cours est déjà validé
        if (cours.isEstValide()) {
            session.setAttribute("errorMessage", "Ce cours est déjà validé, modification impossible");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Préparer les données pour la JSP
        request.setAttribute("user", user);
        request.setAttribute("cours", cours);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/cahier-texte.jsp").forward(request, response);
    }
    
    /**
     * POST : Enregistrer la saisie du cahier de texte
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Définir l'encodage pour les caractères français
        request.setCharacterEncoding("UTF-8");
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = authService.getLoggedInUser(session);
        
        // Récupérer le coursId
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        int coursId = Integer.parseInt(coursIdParam);
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null || !hasAccessToCahierTexte(user, cours)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer les données du formulaire
        String heureDebutStr = request.getParameter("heureDebut");
        String heureFinStr = request.getParameter("heureFin");
        String contenu = request.getParameter("contenu");
        String objectifs = request.getParameter("objectifs");
        String travaux = request.getParameter("travaux");
        String ressources = request.getParameter("ressources");
        String commentaire = request.getParameter("commentaire");
        
        // Validation
        if (heureDebutStr == null || heureFinStr == null || 
            heureDebutStr.isEmpty() || heureFinStr.isEmpty()) {
            session.setAttribute("errorMessage", "Les heures de début et fin sont obligatoires");
            response.sendRedirect(request.getContextPath() + "/cahier-texte?coursId=" + coursId);
            return;
        }
        
        if (contenu == null || contenu.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Le contenu du cours est obligatoire");
            response.sendRedirect(request.getContextPath() + "/cahier-texte?coursId=" + coursId);
            return;
        }
        
        try {
            // Convertir les heures
            Time heureDebut = Time.valueOf(heureDebutStr + ":00");
            Time heureFin = Time.valueOf(heureFinStr + ":00");
            
            // Vérifier que l'heure de fin est après l'heure de début
            if (heureFin.before(heureDebut)) {
                session.setAttribute("errorMessage", "L'heure de fin doit être après l'heure de début");
                response.sendRedirect(request.getContextPath() + "/cahier-texte?coursId=" + coursId);
                return;
            }
            
            // Saisir le cahier de texte via le service
            boolean success = coursService.saisirCahierTexte(
                coursId, 
                heureDebut, 
                heureFin, 
                contenu, 
                objectifs, 
                travaux, 
                ressources, 
                commentaire, 
                user.getUserId()
            );
            
            if (success) {
                session.setAttribute("successMessage", "Cahier de texte enregistré avec succès !");
                
                // Rediriger vers le dashboard
                if (user.isResponsableClasse()) {
                    response.sendRedirect(request.getContextPath() + "/dashboard-classe");
                } else if (user.isProfesseur()) {
                    response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }
            } else {
                session.setAttribute("errorMessage", "Erreur lors de l'enregistrement");
                response.sendRedirect(request.getContextPath() + "/cahier-texte?coursId=" + coursId);
            }
            
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", "Format d'heure invalide");
            response.sendRedirect(request.getContextPath() + "/cahier-texte?coursId=" + coursId);
        }
    }
    
    /**
     * Vérifier si l'utilisateur a accès à la saisie du cahier de texte
     */
    private boolean hasAccessToCahierTexte(User user, Cours cours) {
        // Responsable de classe : accès à sa classe uniquement
        if (user.isResponsableClasse()) {
            return cours.getClasse().equals(user.getClasse());
        }
        
        // Professeur : accès à ses cours uniquement
        if (user.isProfesseur()) {
            return cours.getProfesseurId() == user.getUserId();
        }
        
        // Responsable de formation : accès à tout
        if (user.isResponsableFormation()) {
            return true;
        }
        
        return false;
    }
}