package com.cahiertexte.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.dao.MatiereDAO;
import com.cahiertexte.dao.UserDAO;
import com.cahiertexte.model.Cours;
import com.cahiertexte.model.Matiere;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;
import com.cahiertexte.service.PresenceService;

/**
 * Servlet Dashboard Responsable de Classe
 * Gère l'affichage du dashboard et la planification de nouveaux cours
 * 
 * URL: /dashboard-classe
 * 
 * @author Projet TDSI
 * @version 2.0
 */
@WebServlet("/dashboard-classe")
public class DashboardClasseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    private PresenceService presenceService;
    private MatiereDAO matiereDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
        presenceService = new PresenceService();
        matiereDAO = new MatiereDAO();
        userDAO = new UserDAO();
    }
    
    /**
     * Afficher le dashboard avec formulaire de planification
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Récupérer l'utilisateur connecté
        User user = authService.getLoggedInUser(session);
        
        // Vérifier le rôle (uniquement RESPONSABLE_CLASSE)
        if (!"RESPONSABLE_CLASSE".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        String classe = user.getClasse();
        
        // Préparer les données pour la vue
        request.setAttribute("user", user);
        
        // Liste des cours de la classe
        List<Cours> coursList = coursService.getCoursByClasse(classe);
        request.setAttribute("coursList", coursList);
        
        // Statistiques globales des cours
        Map<String, Object> statsGlobales = coursService.getStatistiquesGlobales(classe);
        request.setAttribute("statsGlobales", statsGlobales);
        
        // Statistiques de présence (avec gestion d'erreur)
        try {
            Map<String, Object> statsPresence = presenceService.getStatistiquesGlobalesClasse(classe);
            request.setAttribute("statsPresence", statsPresence);
        } catch (Exception e) {
            // Si la méthode n'existe pas encore, on ignore
            System.out.println("Info : Statistiques de présence non disponibles");
        }
        
        // Liste des matières de la classe (pour le formulaire)
        List<Matiere> matieresList = matiereDAO.getMatieresByClasse(classe);
        request.setAttribute("matieresList", matieresList);
        
        // Liste des professeurs (pour le formulaire)
        List<User> professeursList = userDAO.getUsersByRole("PROFESSEUR");
        request.setAttribute("professeursList", professeursList);
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/dashboard-classe.jsp").forward(request, response);
    }
    
    /**
     * Planifier un nouveau cours
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
        
        // Vérifier le rôle
        if (!"RESPONSABLE_CLASSE".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            // Récupérer les paramètres du formulaire
            int matiereId = Integer.parseInt(request.getParameter("matiereId"));
            int professeurId = Integer.parseInt(request.getParameter("professeurId"));
            String classe = user.getClasse(); // Classe du responsable
            Date dateCours = Date.valueOf(request.getParameter("dateCours"));
            Time heureDebut = Time.valueOf(request.getParameter("heureDebut") + ":00");
            Time heureFin = Time.valueOf(request.getParameter("heureFin") + ":00");
            String salle = request.getParameter("salle");
            
            // Créer le cours via le service
            boolean success = coursService.createCours(
                matiereId, 
                professeurId, 
                classe, 
                dateCours, 
                heureDebut, 
                heureFin, 
                salle
            );
            
            if (success) {
                // Message de succès
                session.setAttribute("successMessage", "✓ Cours planifié avec succès !");
            } else {
                // Message d'erreur
                session.setAttribute("errorMessage", "✗ Erreur lors de la planification du cours");
            }
            
        } catch (Exception e) {
            // Gestion des erreurs
            System.err.println("Erreur lors de la création du cours : " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "✗ Erreur : " + e.getMessage());
        }
        
        // Rediriger vers le dashboard (rechargement avec le nouveau cours)
        response.sendRedirect(request.getContextPath() + "/dashboard-classe");
    }
}