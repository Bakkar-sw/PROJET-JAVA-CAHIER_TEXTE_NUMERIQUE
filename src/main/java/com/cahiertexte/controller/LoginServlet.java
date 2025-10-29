package com.cahiertexte.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;

/**
 * Servlet de gestion de la connexion
 * URL: /login
 * 
 * @author Projet TDSI
 * @version 1.0
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
    }
    
    /**
     * Afficher la page de connexion (GET)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier si l'utilisateur est déjà connecté
        HttpSession session = request.getSession(false);
        if (session != null && authService.isUserLoggedIn(session)) {
            // Rediriger vers le dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Afficher la page de login
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }
    
    /**
     * Traiter la soumission du formulaire de connexion (POST)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Récupérer les paramètres du formulaire
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validation basique
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Veuillez remplir tous les champs");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }
        
        // Tentative d'authentification
        User user = authService.authenticate(username.trim(), password);
        
        if (user != null) {
            // Authentification réussie
            HttpSession session = request.getSession(true);
            authService.createUserSession(session, user);
            
            // Message de succès
            session.setAttribute("successMessage", "Bienvenue " + user.getNomComplet() + " !");
            
            // Redirection selon le rôle
            String redirectURL = getRedirectURLByRole(user.getRole());
            response.sendRedirect(request.getContextPath() + redirectURL);
            
        } else {
            // Authentification échouée
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            request.setAttribute("username", username); // Garder le username saisi
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Déterminer l'URL de redirection selon le rôle
     */
    private String getRedirectURLByRole(String role) {
        switch (role) {
            case "RESPONSABLE_FORMATION":
                return "/dashboard-formation";
            case "RESPONSABLE_CLASSE":
                return "/dashboard-classe";
            case "PROFESSEUR":
                return "/dashboard-professeur";
            case "ETUDIANT":
                return "/dashboard-etudiant";
            default:
                return "/dashboard";
        }
    }
}