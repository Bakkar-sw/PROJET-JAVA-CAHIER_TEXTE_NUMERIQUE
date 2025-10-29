package com.cahiertexte.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
 * Servlet Dashboard Étudiant
 * URL: /dashboard-etudiant
 */
@WebServlet("/dashboard-etudiant")
public class DashboardEtudiantServlet extends HttpServlet {
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
        if (!"ETUDIANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Préparer les données
        request.setAttribute("user", user);
        
        // Cours de la classe de l'étudiant
        List<Cours> coursList = coursService.getCoursByClasse(user.getClasse());
        request.setAttribute("coursList", coursList);
        
        // Statistiques de présence
        Map<String, Object> statsPresence = presenceService.getStatistiquesEtudiant(user.getUserId());
        request.setAttribute("statsPresence", statsPresence);
        
        // Mes absences
        List<Presence> absences = presenceService.getAbsencesByEtudiant(user.getUserId());
        request.setAttribute("mesAbsences", absences);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/dashboard-etudiant.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}