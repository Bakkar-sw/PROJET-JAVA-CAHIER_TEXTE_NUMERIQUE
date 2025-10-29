package com.cahiertexte.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.dao.JustificatifDAO;
import com.cahiertexte.model.Justificatif;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;

/**
 * Servlet de validation des justificatifs par le responsable de formation
 * URL: /valider-justificatif?justificatifId=X
 */
@WebServlet("/valider-justificatif")
public class ValiderJustificatifServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private JustificatifDAO justificatifDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        justificatifDAO = new JustificatifDAO();
    }
    
    /**
     * GET : Afficher le détail du justificatif
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
        
        // Vérifier que c'est le responsable de formation
        if (!user.isResponsableFormation()) {
            session.setAttribute("errorMessage", "Accès réservé au responsable de formation");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer justificatifId
        String justifIdParam = request.getParameter("justificatifId");
        if (justifIdParam == null || justifIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Justificatif non spécifié");
            response.sendRedirect(request.getContextPath() + "/dashboard-formation");
            return;
        }
        
        int justificatifId;
        try {
            justificatifId = Integer.parseInt(justifIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID justificatif invalide");
            response.sendRedirect(request.getContextPath() + "/dashboard-formation");
            return;
        }
        
        // Récupérer le justificatif
        Justificatif justificatif = justificatifDAO.getJustificatifById(justificatifId);
        if (justificatif == null) {
            session.setAttribute("errorMessage", "Justificatif non trouvé");
            response.sendRedirect(request.getContextPath() + "/dashboard-formation");
            return;
        }
        
        // Préparer les données pour la JSP
        request.setAttribute("user", user);
        request.setAttribute("justificatif", justificatif);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/valider-justificatif.jsp").forward(request, response);
    }
    
    /**
     * POST : Accepter ou refuser le justificatif
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
        
        if (!user.isResponsableFormation()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer les données
        String justifIdParam = request.getParameter("justificatifId");
        String action = request.getParameter("action");
        String commentaire = request.getParameter("commentaire");
        
        if (justifIdParam == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard-formation");
            return;
        }
        
        int justificatifId = Integer.parseInt(justifIdParam);
        boolean accepte = "accepter".equals(action);
        
        // Valider le justificatif
        boolean success = justificatifDAO.validerJustificatif(
            justificatifId, 
            accepte, 
            commentaire, 
            user.getUserId()
        );
        
        if (success) {
            String message = accepte ? "Justificatif accepté avec succès" : "Justificatif refusé";
            session.setAttribute("successMessage", message);
        } else {
            session.setAttribute("errorMessage", "Erreur lors de la validation");
        }
        
        // Rediriger vers le dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard-formation");
    }
}