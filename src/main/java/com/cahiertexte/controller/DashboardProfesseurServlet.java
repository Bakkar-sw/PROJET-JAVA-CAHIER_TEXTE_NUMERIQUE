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
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;

/**
 * Servlet Dashboard Professeur
 * URL: /dashboard-professeur
 */
@WebServlet("/dashboard-professeur")
public class DashboardProfesseurServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
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
        if (!"PROFESSEUR".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Préparer les données
        request.setAttribute("user", user);
        
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
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/dashboard-professeur.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}