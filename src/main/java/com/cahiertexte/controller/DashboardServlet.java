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
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;
import com.cahiertexte.service.PresenceService;

/**
 * Servlet Dashboard - Redirige vers le bon dashboard selon le rôle
 * URL: /dashboard
 * 
 * @author Projet TDSI
 * @version 1.0
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
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
        
        // Vérifier si l'utilisateur est connecté
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Récupérer l'utilisateur connecté
        User user = authService.getLoggedInUser(session);
        
        // Préparer les données pour le dashboard
        prepareCommonData(request, user);
        
        // Rediriger vers le bon dashboard selon le rôle
        String jspPage = getDashboardPageByRole(user.getRole());
        request.getRequestDispatcher(jspPage).forward(request, response);
    }
    
    /**
     * Préparer les données communes à tous les dashboards
     */
    private void prepareCommonData(HttpServletRequest request, User user) {
        request.setAttribute("user", user);
        
        // Données spécifiques selon le rôle
        switch (user.getRole()) {
            case "RESPONSABLE_CLASSE":
                prepareResponsableClasseData(request, user);
                break;
            case "RESPONSABLE_FORMATION":
                prepareResponsableFormationData(request, user);
                break;
            case "PROFESSEUR":
                prepareProfesseurData(request, user);
                break;
            case "ETUDIANT":
                prepareEtudiantData(request, user);
                break;
        }
    }
    
    /**
     * Données pour responsable de classe
     */
    private void prepareResponsableClasseData(HttpServletRequest request, User user) {
        String classe = user.getClasse();
        
        // Cours de la classe
        List<Cours> coursList = coursService.getCoursByClasse(classe);
        request.setAttribute("coursList", coursList);
        
        // Statistiques
        Map<String, Object> stats = coursService.getStatistiquesGlobales(classe);
        request.setAttribute("statsGlobales", stats);
        
        // Statistiques de présence
        Map<String, Object> statsPresence = presenceService.getStatistiquesGlobalesClasse(classe);
        request.setAttribute("statsPresence", statsPresence);
    }
    
    /**
     * Données pour responsable de formation
     */
    private void prepareResponsableFormationData(HttpServletRequest request, User user) {
        // Cours non validés
        List<Cours> coursNonValides = coursService.getCoursNonValides();
        request.setAttribute("coursNonValides", coursNonValides);
        
        // Alertes matières (12h restantes)
        List<Map<String, Object>> alertes = coursService.getMatieresAvecAlerte();
        request.setAttribute("alertes", alertes);
    }
    
    /**
     * Données pour professeur
     */
    private void prepareProfesseurData(HttpServletRequest request, User user) {
        // Cours du professeur
        List<Cours> mesCours = coursService.getCoursByProfesseur(user.getUserId());
        request.setAttribute("mesCours", mesCours);
        
        // Cours à valider
        int nbAValider = 0;
        for (Cours cours : mesCours) {
            if (cours.isRealise() && !cours.isEstValide()) {
                nbAValider++;
            }
        }
        request.setAttribute("nbCoursAValider", nbAValider);
    }
    
    /**
     * Données pour étudiant
     */
    private void prepareEtudiantData(HttpServletRequest request, User user) {
        // Cours de la classe de l'étudiant
        List<Cours> coursList = coursService.getCoursByClasse(user.getClasse());
        request.setAttribute("coursList", coursList);
        
        // Statistiques de présence
        Map<String, Object> statsPresence = presenceService.getStatistiquesEtudiant(user.getUserId());
        request.setAttribute("statsPresence", statsPresence);
        
        // Mes absences
        List<com.cahiertexte.model.Presence> absences = presenceService.getAbsencesByEtudiant(user.getUserId());
        request.setAttribute("mesAbsences", absences);
    }
    
    /**
     * Déterminer la page JSP selon le rôle
     */
    private String getDashboardPageByRole(String role) {
        switch (role) {
            case "RESPONSABLE_FORMATION":
                return "/views/dashboard-formation.jsp";
            case "RESPONSABLE_CLASSE":
                return "/views/dashboard-classe.jsp";
            case "PROFESSEUR":
                return "/views/dashboard-professeur.jsp";
            case "ETUDIANT":
                return "/views/dashboard-etudiant.jsp";
            default:
                return "/views/dashboard.jsp";
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}