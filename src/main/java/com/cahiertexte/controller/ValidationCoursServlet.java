package com.cahiertexte.controller;

import java.io.IOException;

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
 * Servlet de validation des cours par le professeur
 * URL: /validation-cours?coursId=X
 */
@WebServlet("/validation-cours")
public class ValidationCoursServlet extends HttpServlet {
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
     * GET : Afficher le détail du cours à valider
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
        
        // Vérifier que c'est un professeur
        if (!user.isProfesseur()) {
            session.setAttribute("errorMessage", "Accès réservé aux professeurs");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer le coursId
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null || coursIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "ID du cours manquant");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        int coursId;
        try {
            coursId = Integer.parseInt(coursIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID du cours invalide");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null) {
            session.setAttribute("errorMessage", "Cours non trouvé");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        // Vérifier que c'est bien le professeur de ce cours
        if (cours.getProfesseurId() != user.getUserId()) {
            session.setAttribute("errorMessage", "Vous n'êtes pas le professeur de ce cours");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        // Vérifier que le cours est réalisé mais non validé
        if (!cours.isRealise()) {
            session.setAttribute("errorMessage", "Ce cours n'a pas encore été saisi");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        if (cours.isEstValide()) {
            session.setAttribute("errorMessage", "Ce cours est déjà validé");
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        // Préparer les données pour la JSP
        request.setAttribute("user", user);
        request.setAttribute("cours", cours);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/validation-cours.jsp").forward(request, response);
    }
    
    /**
     * POST : Valider le cours
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Définir l'encodage
        request.setCharacterEncoding("UTF-8");
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = authService.getLoggedInUser(session);
        
        // Vérifier que c'est un professeur
        if (!user.isProfesseur()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer le coursId
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        int coursId = Integer.parseInt(coursIdParam);
        
        // Récupérer le cours
        Cours cours = coursService.getCoursById(coursId);
        if (cours == null || cours.getProfesseurId() != user.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
            return;
        }
        
        // Valider le cours
        boolean success = coursService.validerCours(coursId, user.getUserId());
        
        if (success) {
            session.setAttribute("successMessage", "Cours validé avec succès !");
        } else {
            session.setAttribute("errorMessage", "Erreur lors de la validation du cours");
        }
        
        // Rediriger vers le dashboard professeur
        response.sendRedirect(request.getContextPath() + "/dashboard-professeur");
    }
}