package com.cahiertexte.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.dao.MatiereDAO;
import com.cahiertexte.model.Matiere;
import com.cahiertexte.model.User;
import com.cahiertexte.service.AuthenticationService;
import com.cahiertexte.service.CoursService;
import com.cahiertexte.service.PresenceService;

/**
 * Servlet de génération de rapports et statistiques
 * URL: /rapports
 */
@WebServlet("/rapports")
public class RapportsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    private CoursService coursService;
    private PresenceService presenceService;
    private MatiereDAO matiereDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
        coursService = new CoursService();
        presenceService = new PresenceService();
        matiereDAO = new MatiereDAO();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérifier l'authentification
        HttpSession session = request.getSession(false);
        if (session == null || !authService.isUserLoggedIn(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = authService.getLoggedInUser(session);
        
        // Vérifier le rôle (accessible à tous mais adapté selon le rôle)
        request.setAttribute("user", user);
        
        // Statistiques globales selon le rôle
        if (user.isResponsableFormation()) {
            prepareStatsResponsableFormation(request);
        } else if (user.isResponsableClasse()) {
            prepareStatsResponsableClasse(request, user);
        } else if (user.isProfesseur()) {
            prepareStatsProfesseur(request, user);
        } else if (user.isEtudiant()) {
            prepareStatsEtudiant(request, user);
        }
        
        // Forward vers la page JSP
        request.getRequestDispatcher("/views/rapports.jsp").forward(request, response);
    }
    
    /**
     * Préparer les statistiques pour le responsable de formation
     */
    private void prepareStatsResponsableFormation(HttpServletRequest request) {
        // Statistiques par classe
        String[] classes = {"CI_M1", "CI_M2", "MCS_M1", "MCS_M2"};
        List<Map<String, Object>> statsParClasse = new ArrayList<>();
        
        for (String classe : classes) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("classe", classe);
            stats.put("statsCours", coursService.getStatistiquesGlobales(classe));
            stats.put("statsPresence", presenceService.getStatistiquesGlobalesClasse(classe));
            statsParClasse.add(stats);
        }
        
        request.setAttribute("statsParClasse", statsParClasse);
        
        // Top absences par classe
        Map<String, List<Map<String, Object>>> topAbsencesParClasse = new HashMap<>();
        for (String classe : classes) {
            topAbsencesParClasse.put(classe, presenceService.getTop5Absences(classe));
        }
        request.setAttribute("topAbsencesParClasse", topAbsencesParClasse);
        
        // Alertes matières
        List<Map<String, Object>> alertes = coursService.getMatieresAvecAlerte();
        request.setAttribute("alertesMatiere", alertes);
        
        // Statistiques des matières
        List<Matiere> matieres = matiereDAO.getAllMatieres();
        List<Map<String, Object>> statsMatieres = new ArrayList<>();
        for (Matiere m : matieres) {
            statsMatieres.add(coursService.getStatistiquesMatiere(m.getMatiereId()));
        }
        request.setAttribute("statsMatieres", statsMatieres);
    }
    
    /**
     * Préparer les statistiques pour le responsable de classe
     */
    private void prepareStatsResponsableClasse(HttpServletRequest request, User user) {
        String classe = user.getClasse();
        
        // Stats globales
        request.setAttribute("statsCours", coursService.getStatistiquesGlobales(classe));
        request.setAttribute("statsPresence", presenceService.getStatistiquesGlobalesClasse(classe));
        
        // Top absences
        request.setAttribute("topAbsences", presenceService.getTop5Absences(classe));
        
        // Étudiants critiques
        request.setAttribute("etudiantsCritiques", presenceService.getEtudiantsAbsencesCritiques(classe));
    }
    
    /**
     * Préparer les statistiques pour le professeur
     */
    private void prepareStatsProfesseur(HttpServletRequest request, User user) {
        // Statistiques des matières enseignées
        List<Matiere> matieres = matiereDAO.getMatieresByProfesseur(user.getUserId());
        List<Map<String, Object>> statsMatieres = new ArrayList<>();
        
        for (Matiere m : matieres) {
            statsMatieres.add(coursService.getStatistiquesMatiere(m.getMatiereId()));
        }
        
        request.setAttribute("statsMatieres", statsMatieres);
    }
    
    /**
     * Préparer les statistiques pour l'étudiant
     */
    private void prepareStatsEtudiant(HttpServletRequest request, User user) {
        // Statistiques personnelles
        request.setAttribute("statsPersonnelles", presenceService.getStatistiquesEtudiant(user.getUserId()));
        
        // Statistiques de la classe
        request.setAttribute("statsClasse", presenceService.getStatistiquesGlobalesClasse(user.getClasse()));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}