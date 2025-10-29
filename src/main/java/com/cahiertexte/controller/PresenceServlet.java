package com.cahiertexte.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.model.Cours;
import com.cahiertexte.model.Presence;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;
import com.cahiertexte.service.PresenceService;

/**
 * Servlet de gestion des présences
 * URL: /presences?coursId=X
 * Actions: afficher, saisir, modifier les présences d'un cours
 */
@WebServlet("/presences")
public class PresenceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    private PresenceService presenceService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
        presenceService = new PresenceService();
    }
    
    /**
     * GET : Afficher la page de gestion des présences
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
            request.setAttribute("error", "ID du cours manquant");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        int coursId;
        try {
            coursId = Integer.parseInt(coursIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID du cours invalide");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null) {
            request.setAttribute("error", "Cours non trouvé");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Vérifier les droits d'accès
        if (!hasAccessToPresences(user, cours)) {
            request.setAttribute("error", "Vous n'avez pas accès à ce cours");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer les présences existantes
        List<Presence> presences = presenceService.getPresencesByCours(coursId);
        
        // Si aucune présence n'existe, les initialiser
        if (presences.isEmpty()) {
            boolean initialized = presenceService.initializerPresencesCours(coursId, user.getUserId());
            if (initialized) {
                presences = presenceService.getPresencesByCours(coursId);
            }
        }
        
        // Préparer les données pour la JSP
        request.setAttribute("user", user);
        request.setAttribute("cours", cours);
        request.setAttribute("presences", presences);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/presences.jsp").forward(request, response);
    }
    
    /**
     * POST : Enregistrer les présences
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
        
        // Récupérer le coursId
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        int coursId = Integer.parseInt(coursIdParam);
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null || !hasAccessToPresences(user, cours)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer toutes les présences du cours
        List<Presence> presences = presenceService.getPresencesByCours(coursId);
        
        boolean success = true;
        
        // Parcourir chaque présence et mettre à jour
        for (Presence presence : presences) {
            String presenceIdStr = String.valueOf(presence.getPresenceId());
            
            // Récupérer le statut sélectionné
            String statut = request.getParameter("statut_" + presenceIdStr);
            String commentaire = request.getParameter("commentaire_" + presenceIdStr);
            
            if (statut != null) {
                // Mettre à jour selon le statut
                switch (statut) {
                    case "PRESENT":
                        presenceService.marquerPresent(presence.getPresenceId());
                        break;
                    case "ABSENT":
                        presenceService.marquerAbsent(presence.getPresenceId());
                        break;
                    case "RETARD_ACCEPTE":
                        presenceService.marquerRetardAccepte(presence.getPresenceId(), null, commentaire);
                        break;
                    case "RETARD_REFUSE":
                        presenceService.marquerRetardRefuse(presence.getPresenceId(), null, commentaire);
                        break;
                }
            }
        }
        
        // Message de succès
        session.setAttribute("successMessage", "Présences enregistrées avec succès !");
        
        // Rediriger vers la même page pour voir les modifications
        response.sendRedirect(request.getContextPath() + "/presences?coursId=" + coursId);
    }
    
    /**
     * Vérifier si l'utilisateur a accès aux présences de ce cours
     */
    private boolean hasAccessToPresences(User user, Cours cours) {
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