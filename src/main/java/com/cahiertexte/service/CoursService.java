package com.cahiertexte.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cahiertexte.dao.CoursDAO;
import com.cahiertexte.dao.MatiereDAO;
import com.cahiertexte.model.Cours;
import com.cahiertexte.model.Matiere;

/**
 * Service de gestion des cours
 * Contient la logique métier pour la gestion des cours et du planning
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class CoursService {
    
    private CoursDAO coursDAO;
    private MatiereDAO matiereDAO;
    
    public CoursService() {
        this.coursDAO = new CoursDAO();
        this.matiereDAO = new MatiereDAO();
    }
    
    /**
     * Créer un nouveau cours (planification)
     */
    public boolean createCours(int matiereId, int professeurId, String classe, 
                              Date dateCours, Time heureDebut, Time heureFin, String salle) {
        // Validation
        if (matiereId <= 0 || professeurId <= 0 || classe == null || dateCours == null) {
            System.out.println("✗ Erreur : Paramètres invalides pour créer un cours");
            return false;
        }
        
        // Vérifier que la matière existe
        Matiere matiere = matiereDAO.getMatiereById(matiereId);
        if (matiere == null) {
            System.out.println("✗ Erreur : Matière non trouvée ID: " + matiereId);
            return false;
        }
        
        // Créer le cours
        Cours cours = new Cours(matiereId, professeurId, classe, dateCours, 
                               heureDebut, heureFin, salle);
        
        boolean success = coursDAO.createCours(cours);
        
        if (success) {
            System.out.println("✓ Cours créé avec succès ID: " + cours.getCoursId());
        }
        
        return success;
    }
    /**
     * Récupérer un cours par son ID
     */
    public Cours getCoursById(int coursId) {
        return coursDAO.getCoursById(coursId);
    }
    /**
     * Saisir les informations de réalisation d'un cours (cahier de texte)
     */
    public boolean saisirCahierTexte(int coursId, Time heureDebutReelle, Time heureFinReelle,
                                     String contenu, String objectifs, String travaux, 
                                     String ressources, String commentaire, int responsableSaisieId) {
        // Récupérer le cours
        Cours cours = coursDAO.getCoursById(coursId);
        if (cours == null) {
            System.out.println("✗ Erreur : Cours non trouvé ID: " + coursId);
            return false;
        }
        
        // Vérifier que le cours n'est pas déjà validé
        if (cours.isEstValide()) {
            System.out.println("✗ Erreur : Le cours est déjà validé, modification impossible");
            return false;
        }
        
        // Calculer la durée effective
        BigDecimal duree = calculerDuree(heureDebutReelle, heureFinReelle);
        
        // Mettre à jour le cours
        cours.setHeureDebutReelle(heureDebutReelle);
        cours.setHeureFinReelle(heureFinReelle);
        cours.setDureeEffective(duree);
        cours.setContenuCours(contenu);
        cours.setObjectifs(objectifs);
        cours.setTravauxDonnes(travaux);
        cours.setRessources(ressources);
        cours.setCommentaireProfesseur(commentaire);
        cours.setResponsableSaisieId(responsableSaisieId);
        
        boolean success = coursDAO.updateCoursRealisation(cours);
        
        if (success) {
            System.out.println("✓ Cahier de texte saisi avec succès pour cours ID: " + coursId);
            System.out.println("  Durée effective : " + duree + " heures");
        }
        
        return success;
    }
    
    /**
     * Valider un cours (par le professeur)
     */
    public boolean validerCours(int coursId, int professeurId) {
        Cours cours = coursDAO.getCoursById(coursId);
        
        if (cours == null) {
            System.out.println("✗ Erreur : Cours non trouvé");
            return false;
        }
        
        // Vérifier que c'est bien le professeur du cours
        if (cours.getProfesseurId() != professeurId) {
            System.out.println("✗ Erreur : Ce professeur ne peut pas valider ce cours");
            return false;
        }
        
        // Vérifier que le cours a été saisi
        if (!cours.isRealise()) {
            System.out.println("✗ Erreur : Le cours doit être réalisé avant validation");
            return false;
        }
        
        boolean success = coursDAO.validerCours(coursId);
        
        if (success) {
            System.out.println("✓ Cours validé avec succès ID: " + coursId);
        }
        
        return success;
    }
    
    /**
     * Récupérer les cours d'une classe
     */
    public List<Cours> getCoursByClasse(String classe) {
        return coursDAO.getCoursByClasse(classe);
    }
    
    /**
     * Récupérer les cours d'un professeur
     */
    public List<Cours> getCoursByProfesseur(int professeurId) {
        return coursDAO.getCoursByProfesseur(professeurId);
    }
    
    /**
     * Récupérer les cours non validés
     */
    public List<Cours> getCoursNonValides() {
        return coursDAO.getCoursNonValides();
    }
    
    /**
     * Calculer les heures effectuées et restantes pour une matière
     */
    public Map<String, Object> getStatistiquesMatiere(int matiereId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Récupérer la matière
        Matiere matiere = matiereDAO.getMatiereById(matiereId);
        if (matiere == null) {
            return stats;
        }
        
        // Récupérer tous les cours de cette matière
        List<Cours> coursList = coursDAO.getCoursByMatiere(matiereId);
        
        BigDecimal heuresRealisees = BigDecimal.ZERO;
        int nbCoursRealises = 0;
        int nbCoursValides = 0;
        
        for (Cours cours : coursList) {
            if (cours.isRealise() && cours.getDureeEffective() != null) {
                heuresRealisees = heuresRealisees.add(cours.getDureeEffective());
                nbCoursRealises++;
                
                if (cours.isEstValide()) {
                    nbCoursValides++;
                }
            }
        }
        
        BigDecimal heuresRestantes = matiere.getVolumeHoraireTotal().subtract(heuresRealisees);
        double pourcentage = 0;
        
        if (matiere.getVolumeHoraireTotal().compareTo(BigDecimal.ZERO) > 0) {
            pourcentage = heuresRealisees.multiply(new BigDecimal(100))
                         .divide(matiere.getVolumeHoraireTotal(), 2, BigDecimal.ROUND_HALF_UP)
                         .doubleValue();
        }
        
        stats.put("matiere", matiere);
        stats.put("volumeTotal", matiere.getVolumeHoraireTotal());
        stats.put("heuresRealisees", heuresRealisees);
        stats.put("heuresRestantes", heuresRestantes);
        stats.put("pourcentageRealise", pourcentage);
        stats.put("nbCoursTotal", coursList.size());
        stats.put("nbCoursRealises", nbCoursRealises);
        stats.put("nbCoursValides", nbCoursValides);
        stats.put("nbCoursEnAttente", nbCoursRealises - nbCoursValides);
        
        return stats;
    }
    
    /**
     * Vérifier si une matière nécessite une alerte (12h restantes)
     */
    public boolean needsAlerte12h(int matiereId) {
        Map<String, Object> stats = getStatistiquesMatiere(matiereId);
        
        if (stats.isEmpty()) {
            return false;
        }
        
        BigDecimal heuresRestantes = (BigDecimal) stats.get("heuresRestantes");
        
        // Alerte si <= 12h restantes
        return heuresRestantes.compareTo(new BigDecimal(12)) <= 0;
    }
    
    /**
     * Récupérer toutes les matières nécessitant une alerte
     */
    public List<Map<String, Object>> getMatieresAvecAlerte() {
        List<Map<String, Object>> alertes = new ArrayList<>();
        
        List<Matiere> matieres = matiereDAO.getAllMatieres();
        
        for (Matiere matiere : matieres) {
            Map<String, Object> stats = getStatistiquesMatiere(matiere.getMatiereId());
            
            if (needsAlerte12h(matiere.getMatiereId())) {
                alertes.add(stats);
            }
        }
        
        return alertes;
    }
    
    /**
     * Calculer la durée entre deux heures (en heures décimales)
     */
    private BigDecimal calculerDuree(Time debut, Time fin) {
        if (debut == null || fin == null) {
            return BigDecimal.ZERO;
        }
        
        long diffMillis = fin.getTime() - debut.getTime();
        long diffMinutes = diffMillis / (60 * 1000);
        
        // Convertir en heures avec 2 décimales
        BigDecimal duree = new BigDecimal(diffMinutes).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
        
        return duree;
    }
    
    /**
     * Annuler un cours
     */
    public boolean annulerCours(int coursId, String motif) {
        Cours cours = coursDAO.getCoursById(coursId);
        
        if (cours == null) {
            System.out.println("✗ Erreur : Cours non trouvé");
            return false;
        }
        
        if (cours.isEstValide()) {
            System.out.println("✗ Erreur : Impossible d'annuler un cours déjà validé");
            return false;
        }
        
        cours.setStatutCours("ANNULE");
        cours.setCommentaireProfesseur("Annulé - Motif : " + motif);
        
        boolean success = coursDAO.updateCours(cours);
        
        if (success) {
            System.out.println("✓ Cours annulé ID: " + coursId);
        }
        
        return success;
    }
    
    /**
     * Modifier un cours planifié
     */
    public boolean modifierCours(int coursId, Date nouvelleDate, Time nouvelleHeureDebut, 
                                Time nouvelleHeureFin, String nouvelleSalle) {
        Cours cours = coursDAO.getCoursById(coursId);
        
        if (cours == null) {
            System.out.println("✗ Erreur : Cours non trouvé");
            return false;
        }
        
        if (cours.isEstValide()) {
            System.out.println("✗ Erreur : Impossible de modifier un cours validé");
            return false;
        }
        
        if (nouvelleDate != null) cours.setDateCours(nouvelleDate);
        if (nouvelleHeureDebut != null) cours.setHeureDebutPrevue(nouvelleHeureDebut);
        if (nouvelleHeureFin != null) cours.setHeureFinPrevue(nouvelleHeureFin);
        if (nouvelleSalle != null) cours.setSalle(nouvelleSalle);
        
        boolean success = coursDAO.updateCours(cours);
        
        if (success) {
            System.out.println("✓ Cours modifié ID: " + coursId);
        }
        
        return success;
    }
    
    /**
     * Récupérer les statistiques globales des cours
     */
    public Map<String, Object> getStatistiquesGlobales(String classe) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Cours> coursList = coursDAO.getCoursByClasse(classe);
        
        int nbTotal = coursList.size();
        int nbPlanifies = 0;
        int nbRealises = 0;
        int nbValides = 0;
        int nbAnnules = 0;
        
        for (Cours cours : coursList) {
            if (cours.isPlanifie()) nbPlanifies++;
            if (cours.isRealise()) nbRealises++;
            if (cours.isEstValide()) nbValides++;
            if (cours.isAnnule()) nbAnnules++;
        }
        
        stats.put("nbTotal", nbTotal);
        stats.put("nbPlanifies", nbPlanifies);
        stats.put("nbRealises", nbRealises);
        stats.put("nbValides", nbValides);
        stats.put("nbAnnules", nbAnnules);
        stats.put("nbEnAttenteValidation", nbRealises - nbValides);
        
        return stats;
    }
    
    /**
     * Test du service
     */
    public static void main(String[] args) {
        System.out.println("=== TEST CoursService ===\n");
        
        CoursService coursService = new CoursService();
        
        // Test 1 : Récupérer les cours d'une classe
        System.out.println("1. Cours de la classe CI_M1 :");
        List<Cours> cours = coursService.getCoursByClasse("CI_M1");
        System.out.println("   Nombre de cours : " + cours.size());
        
        // Test 2 : Statistiques d'une matière
        System.out.println("\n2. Statistiques de la matière ID 1 :");
        Map<String, Object> stats = coursService.getStatistiquesMatiere(1);
        System.out.println("   Volume total : " + stats.get("volumeTotal") + "h");
        System.out.println("   Heures réalisées : " + stats.get("heuresRealisees") + "h");
        System.out.println("   Heures restantes : " + stats.get("heuresRestantes") + "h");
        System.out.println("   Pourcentage : " + stats.get("pourcentageRealise") + "%");
        
        // Test 3 : Vérifier les alertes
        System.out.println("\n3. Matières nécessitant une alerte (12h restantes) :");
        List<Map<String, Object>> alertes = coursService.getMatieresAvecAlerte();
        System.out.println("   Nombre d'alertes : " + alertes.size());
        
        // Test 4 : Statistiques globales
        System.out.println("\n4. Statistiques globales CI_M1 :");
        Map<String, Object> statsGlobales = coursService.getStatistiquesGlobales("CI_M1");
        System.out.println("   Total cours : " + statsGlobales.get("nbTotal"));
        System.out.println("   Planifiés : " + statsGlobales.get("nbPlanifies"));
        System.out.println("   Réalisés : " + statsGlobales.get("nbRealises"));
        System.out.println("   Validés : " + statsGlobales.get("nbValides"));
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}