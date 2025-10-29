package com.cahiertexte.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.dao.JustificatifDAO;
import com.cahiertexte.dao.PresenceDAO;
import com.cahiertexte.model.Justificatif;
import com.cahiertexte.model.Presence;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;

/**
 * Servlet de soumission de justificatif d'absence
 * URL: /soumettre-justificatif?presenceId=X
 */
@WebServlet("/soumettre-justificatif")
public class SoumettreJustificatifServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private PresenceDAO presenceDAO;
    private JustificatifDAO justificatifDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        presenceDAO = new PresenceDAO();
        justificatifDAO = new JustificatifDAO();
    }
    
    /**
     * GET : Afficher le formulaire de soumission
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
        
        // Vérifier que c'est un étudiant
        if (!user.isEtudiant()) {
            session.setAttribute("errorMessage", "Accès réservé aux étudiants");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer presenceId
        String presenceIdParam = request.getParameter("presenceId");
        if (presenceIdParam == null || presenceIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Absence non spécifiée");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        int presenceId;
        try {
            presenceId = Integer.parseInt(presenceIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID d'absence invalide");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        // Récupérer la présence (absence)
        Presence presence = presenceDAO.getPresenceById(presenceId);
        if (presence == null) {
            session.setAttribute("errorMessage", "Absence non trouvée");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        // Vérifier que c'est bien l'absence de cet étudiant
        if (presence.getEtudiantId() != user.getUserId()) {
            session.setAttribute("errorMessage", "Cette absence ne vous concerne pas");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        // Vérifier que c'est bien une absence
        if (!presence.isAbsent()) {
            session.setAttribute("errorMessage", "Ceci n'est pas une absence");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        // Préparer les données pour la JSP
        request.setAttribute("user", user);
        request.setAttribute("presence", presence);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/soumettre-justificatif.jsp").forward(request, response);
    }
    
    /**
     * POST : Enregistrer le justificatif
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
        
        if (!user.isEtudiant()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Récupérer les données du formulaire
        String presenceIdParam = request.getParameter("presenceId");
        String typeJustificatif = request.getParameter("typeJustificatif");
        String motif = request.getParameter("motif");
        
        // Validation
        if (presenceIdParam == null || typeJustificatif == null || motif == null || motif.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tous les champs sont obligatoires");
            response.sendRedirect(request.getContextPath() + "/soumettre-justificatif?presenceId=" + presenceIdParam);
            return;
        }
        
        int presenceId = Integer.parseInt(presenceIdParam);
        
        // Récupérer la présence
        Presence presence = presenceDAO.getPresenceById(presenceId);
        if (presence == null || presence.getEtudiantId() != user.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
            return;
        }
        
        // Créer le justificatif
        Justificatif justificatif = new Justificatif();
        justificatif.setEtudiantId(user.getUserId());
        justificatif.setCoursId(presence.getCoursId());
        justificatif.setMotif(motif.trim());
        justificatif.setTypeJustificatif(typeJustificatif);
        justificatif.setStatut("EN_ATTENTE");
        
        // Enregistrer
        boolean success = justificatifDAO.createJustificatif(justificatif);
        
        if (success) {
            session.setAttribute("successMessage", "Justificatif soumis avec succès ! Il sera examiné par le responsable de formation.");
            response.sendRedirect(request.getContextPath() + "/dashboard-etudiant");
        } else {
            session.setAttribute("errorMessage", "Erreur lors de la soumission du justificatif");
            response.sendRedirect(request.getContextPath() + "/soumettre-justificatif?presenceId=" + presenceId);
        }
    }
}