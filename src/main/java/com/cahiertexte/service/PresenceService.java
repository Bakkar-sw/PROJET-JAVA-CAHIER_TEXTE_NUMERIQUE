package com.cahiertexte.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cahiertexte.dao.PresenceDAO;
import com.cahiertexte.dao.UserDAO;
import com.cahiertexte.dao.CoursDAO;
import com.cahiertexte.model.Presence;
import com.cahiertexte.model.User;
import com.cahiertexte.model.Cours;

/**
 * Service de gestion des présences
 * Contient la logique métier pour la gestion des présences/absences
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class PresenceService {
    
    private PresenceDAO presenceDAO;
    private UserDAO userDAO;
    private CoursDAO coursDAO;
    
    // Seuil d'absence critique (en nombre)
    private static final int SEUIL_ABSENCE_CRITIQUE = 3;
    
    public PresenceService() {
        this.presenceDAO = new PresenceDAO();
        this.userDAO = new UserDAO();
        this.coursDAO = new CoursDAO();
    }
    
    /**
     * Initialiser les présences pour un cours (tous les étudiants présents par défaut)
     */
    public boolean initializerPresencesCours(int coursId, int responsableSaisieId) {
        // Récupérer le cours
        Cours cours = coursDAO.getCoursById(coursId);
        if (cours == null) {
            System.out.println("✗ Erreur : Cours non trouvé ID: " + coursId);
            return false;
        }
        
        // Vérifier si les présences n'existent pas déjà
        List<Presence> existantes = presenceDAO.getPresencesByCours(coursId);
        if (!existantes.isEmpty()) {
            System.out.println("⚠ Les présences existent déjà pour ce cours");
            return false;
        }
        
        // Créer les présences pour tous les étudiants de la classe
        boolean success = presenceDAO.createPresencesForCours(coursId, cours.getClasse(), responsableSaisieId);
        
        if (success) {
            System.out.println("✓ Présences initialisées pour le cours ID: " + coursId);
        }
        
        return success;
    }
    
    /**
     * Marquer un étudiant comme présent
     */
    public boolean marquerPresent(int presenceId) {
        return updateStatutPresence(presenceId, "PRESENT", null, null);
    }
    
    /**
     * Marquer un étudiant comme absent
     */
    public boolean marquerAbsent(int presenceId) {
        return updateStatutPresence(presenceId, "ABSENT", null, null);
    }
    
    /**
     * Marquer un étudiant en retard accepté
     */
    public boolean marquerRetardAccepte(int presenceId, Time heureArrivee, String motif) {
        return updateStatutPresence(presenceId, "RETARD_ACCEPTE", heureArrivee, motif);
    }
    
    /**
     * Marquer un étudiant en retard refusé
     */
    public boolean marquerRetardRefuse(int presenceId, Time heureArrivee, String motif) {
        return updateStatutPresence(presenceId, "RETARD_REFUSE", heureArrivee, motif);
    }
    
    /**
     * Mettre à jour le statut d'une présence
     */
    private boolean updateStatutPresence(int presenceId, String statut, Time heureArrivee, String commentaire) {
        Presence presence = presenceDAO.getPresenceById(presenceId);
        
        if (presence == null) {
            System.out.println("✗ Erreur : Présence non trouvée ID: " + presenceId);
            return false;
        }
        
        presence.setStatut(statut);
        presence.setHeureArrivee(heureArrivee);
        presence.setCommentaire(commentaire);
        
        boolean success = presenceDAO.updatePresence(presence);
        
        if (success) {
            System.out.println("✓ Présence mise à jour : " + statut + " (ID: " + presenceId + ")");
        }
        
        return success;
    }
    
    /**
     * Récupérer les présences d'un cours
     */
    public List<Presence> getPresencesByCours(int coursId) {
        return presenceDAO.getPresencesByCours(coursId);
    }
    
    /**
     * Récupérer les absences d'un étudiant
     */
    public List<Presence> getAbsencesByEtudiant(int etudiantId) {
        return presenceDAO.getAbsencesByEtudiant(etudiantId);
    }
    
    /**
     * Calculer les statistiques de présence d'un étudiant
     */
    public Map<String, Object> getStatistiquesEtudiant(int etudiantId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Presence> presences = presenceDAO.getPresencesByEtudiant(etudiantId);
        
        int nbTotal = presences.size();
        int nbPresents = 0;
        int nbAbsents = 0;
        int nbRetardsAcceptes = 0;
        int nbRetardsRefuses = 0;
        
        for (Presence p : presences) {
            if (p.isPresent()) nbPresents++;
            else if (p.isAbsent()) nbAbsents++;
            else if (p.isRetardAccepte()) nbRetardsAcceptes++;
            else if (p.isRetardRefuse()) nbRetardsRefuses++;
        }
        
        double tauxPresence = nbTotal > 0 ? (nbPresents * 100.0) / nbTotal : 0;
        
        stats.put("nbTotal", nbTotal);
        stats.put("nbPresents", nbPresents);
        stats.put("nbAbsents", nbAbsents);
        stats.put("nbRetardsAcceptes", nbRetardsAcceptes);
        stats.put("nbRetardsRefuses", nbRetardsRefuses);
        stats.put("tauxPresence", String.format("%.2f", tauxPresence));
        stats.put("estCritique", nbAbsents >= SEUIL_ABSENCE_CRITIQUE);
        
        return stats;
    }
    
    /**
     * Récupérer les étudiants avec absences critiques dans une classe
     */
    public List<Map<String, Object>> getEtudiantsAbsencesCritiques(String classe) {
        List<Map<String, Object>> etudiantsCritiques = new ArrayList<>();
        
        List<User> etudiants = userDAO.getEtudiantsByClasse(classe);
        
        for (User etudiant : etudiants) {
            int nbAbsences = presenceDAO.countAbsences(etudiant.getUserId());
            
            if (nbAbsences >= SEUIL_ABSENCE_CRITIQUE) {
                Map<String, Object> data = new HashMap<>();
                data.put("etudiant", etudiant);
                data.put("nbAbsences", nbAbsences);
                data.put("stats", getStatistiquesEtudiant(etudiant.getUserId()));
                
                etudiantsCritiques.add(data);
            }
        }
        
        return etudiantsCritiques;
    }
    
    /**
     * Calculer les statistiques de présence pour un cours
     */
    public Map<String, Object> getStatistiquesCours(int coursId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Presence> presences = presenceDAO.getPresencesByCours(coursId);
        
        int nbTotal = presences.size();
        int nbPresents = 0;
        int nbAbsents = 0;
        int nbRetards = 0;
        
        for (Presence p : presences) {
            if (p.isPresent()) nbPresents++;
            else if (p.isAbsent()) nbAbsents++;
            else if (p.isRetardAccepte() || p.isRetardRefuse()) nbRetards++;
        }
        
        double tauxPresence = nbTotal > 0 ? (nbPresents * 100.0) / nbTotal : 0;
        
        stats.put("nbTotal", nbTotal);
        stats.put("nbPresents", nbPresents);
        stats.put("nbAbsents", nbAbsents);
        stats.put("nbRetards", nbRetards);
        stats.put("tauxPresence", String.format("%.2f", tauxPresence));
        
        return stats;
    }
    
    /**
     * Récupérer le top 5 des étudiants avec le plus d'absences
     */
    public List<Map<String, Object>> getTop5Absences(String classe) {
        List<Map<String, Object>> top5 = new ArrayList<>();
        
        List<User> etudiants = userDAO.getEtudiantsByClasse(classe);
        
        // Créer une liste avec tous les étudiants et leur nombre d'absences
        for (User etudiant : etudiants) {
            Map<String, Object> data = new HashMap<>();
            data.put("etudiant", etudiant);
            data.put("nbAbsences", presenceDAO.countAbsences(etudiant.getUserId()));
            top5.add(data);
        }
        
        // Trier par nombre d'absences décroissant
        top5.sort((a, b) -> {
            int absA = (int) a.get("nbAbsences");
            int absB = (int) b.get("nbAbsences");
            return Integer.compare(absB, absA);
        });
        
        // Garder uniquement les 5 premiers
        if (top5.size() > 5) {
            top5 = top5.subList(0, 5);
        }
        
        return top5;
    }
    
    /**
     * Vérifier si un étudiant a dépassé le seuil d'absences
     */
    public boolean hasDepasseSeuilAbsences(int etudiantId) {
        int nbAbsences = presenceDAO.countAbsences(etudiantId);
        return nbAbsences >= SEUIL_ABSENCE_CRITIQUE;
    }
    
    /**
     * Calculer le taux de présence moyen d'une classe
     */
    public double getTauxPresenceMoyenClasse(String classe) {
        List<User> etudiants = userDAO.getEtudiantsByClasse(classe);
        
        if (etudiants.isEmpty()) {
            return 0;
        }
        
        double sommeTaux = 0;
        
        for (User etudiant : etudiants) {
            double taux = presenceDAO.getTauxPresence(etudiant.getUserId());
            sommeTaux += taux;
        }
        
        return sommeTaux / etudiants.size();
    }
    
    /**
     * Récupérer les statistiques globales de présence pour une classe
     */
    public Map<String, Object> getStatistiquesGlobalesClasse(String classe) {
        Map<String, Object> stats = new HashMap<>();
        
        List<User> etudiants = userDAO.getEtudiantsByClasse(classe);
        
        int nbEtudiants = etudiants.size();
        int nbEtudiantsCritiques = 0;
        int totalAbsences = 0;
        
        for (User etudiant : etudiants) {
            int nbAbsences = presenceDAO.countAbsences(etudiant.getUserId());
            totalAbsences += nbAbsences;
            
            if (nbAbsences >= SEUIL_ABSENCE_CRITIQUE) {
                nbEtudiantsCritiques++;
            }
        }
        
        double tauxPresenceMoyen = getTauxPresenceMoyenClasse(classe);
        double moyenneAbsencesParEtudiant = nbEtudiants > 0 ? (double) totalAbsences / nbEtudiants : 0;
        
        stats.put("nbEtudiants", nbEtudiants);
        stats.put("nbEtudiantsCritiques", nbEtudiantsCritiques);
        stats.put("totalAbsences", totalAbsences);
        stats.put("moyenneAbsencesParEtudiant", String.format("%.2f", moyenneAbsencesParEtudiant));
        stats.put("tauxPresenceMoyen", String.format("%.2f", tauxPresenceMoyen));
        stats.put("seuilAbsenceCritique", SEUIL_ABSENCE_CRITIQUE);
        
        return stats;
    }
    
    /**
     * Test du service
     */
    public static void main(String[] args) {
        System.out.println("=== TEST PresenceService ===\n");
        
        PresenceService presenceService = new PresenceService();
        
        // Test 1 : Statistiques d'un étudiant
        System.out.println("1. Statistiques de l'étudiant ID 9 :");
        Map<String, Object> statsEtud = presenceService.getStatistiquesEtudiant(9);
        System.out.println("   Cours suivis : " + statsEtud.get("nbTotal"));
        System.out.println("   Présents : " + statsEtud.get("nbPresents"));
        System.out.println("   Absents : " + statsEtud.get("nbAbsents"));
        System.out.println("   Taux présence : " + statsEtud.get("tauxPresence") + "%");
        System.out.println("   Critique : " + statsEtud.get("estCritique"));
        
        // Test 2 : Statistiques globales d'une classe
        System.out.println("\n2. Statistiques globales classe CI_M1 :");
        Map<String, Object> statsClasse = presenceService.getStatistiquesGlobalesClasse("CI_M1");
        System.out.println("   Nombre étudiants : " + statsClasse.get("nbEtudiants"));
        System.out.println("   Étudiants critiques : " + statsClasse.get("nbEtudiantsCritiques"));
        System.out.println("   Total absences : " + statsClasse.get("totalAbsences"));
        System.out.println("   Moyenne absences/étudiant : " + statsClasse.get("moyenneAbsencesParEtudiant"));
        System.out.println("   Taux présence moyen : " + statsClasse.get("tauxPresenceMoyen") + "%");
        
        // Test 3 : Top 5 absences
        System.out.println("\n3. Top 5 absences CI_M1 :");
        List<Map<String, Object>> top5 = presenceService.getTop5Absences("CI_M1");
        for (int i = 0; i < top5.size(); i++) {
            Map<String, Object> data = top5.get(i);
            User etud = (User) data.get("etudiant");
            int nbAbs = (int) data.get("nbAbsences");
            System.out.println("   " + (i+1) + ". " + etud.getNomComplet() + " : " + nbAbs + " absences");
        }
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}