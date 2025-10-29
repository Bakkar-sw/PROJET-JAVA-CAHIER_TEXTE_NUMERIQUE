package com.cahiertexte.model;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Classe entité représentant la présence d'un étudiant à un cours
 * Correspond à la table 'presences' dans la base de données
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class Presence {
    
    private int presenceId;
    private int coursId;
    private int etudiantId;
    private String statut; // PRESENT, ABSENT, RETARD_ACCEPTE, RETARD_REFUSE
    private String commentaire;
    private Time heureArrivee;
    private Timestamp dateSaisie;
    private int saisiPar;
    
    // Attributs supplémentaires (non en base) pour affichage
    private String nomEtudiant;
    private String prenomEtudiant;
    private String classeEtudiant;
    private String nomMatiere;
    private java.sql.Date dateCours;
    
    // Constructeur par défaut
    public Presence() {
    }
    
    // Constructeur avec paramètres essentiels
    public Presence(int coursId, int etudiantId, String statut) {
        this.coursId = coursId;
        this.etudiantId = etudiantId;
        this.statut = statut;
    }
    
    // Constructeur complet
    public Presence(int presenceId, int coursId, int etudiantId, String statut, 
                   String commentaire, Time heureArrivee, int saisiPar) {
        this.presenceId = presenceId;
        this.coursId = coursId;
        this.etudiantId = etudiantId;
        this.statut = statut;
        this.commentaire = commentaire;
        this.heureArrivee = heureArrivee;
        this.saisiPar = saisiPar;
    }
    
    // Getters et Setters
    public int getPresenceId() {
        return presenceId;
    }
    
    public void setPresenceId(int presenceId) {
        this.presenceId = presenceId;
    }
    
    public int getCoursId() {
        return coursId;
    }
    
    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }
    
    public int getEtudiantId() {
        return etudiantId;
    }
    
    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public Time getHeureArrivee() {
        return heureArrivee;
    }
    
    public void setHeureArrivee(Time heureArrivee) {
        this.heureArrivee = heureArrivee;
    }
    
    public Timestamp getDateSaisie() {
        return dateSaisie;
    }
    
    public void setDateSaisie(Timestamp dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
    
    public int getSaisiPar() {
        return saisiPar;
    }
    
    public void setSaisiPar(int saisiPar) {
        this.saisiPar = saisiPar;
    }
    
    public String getNomEtudiant() {
        return nomEtudiant;
    }
    
    public void setNomEtudiant(String nomEtudiant) {
        this.nomEtudiant = nomEtudiant;
    }
    
    public String getPrenomEtudiant() {
        return prenomEtudiant;
    }
    
    public void setPrenomEtudiant(String prenomEtudiant) {
        this.prenomEtudiant = prenomEtudiant;
    }
    
    public String getClasseEtudiant() {
        return classeEtudiant;
    }
    
    public void setClasseEtudiant(String classeEtudiant) {
        this.classeEtudiant = classeEtudiant;
    }
    
    public String getNomMatiere() {
        return nomMatiere;
    }
    
    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }
    
    public java.sql.Date getDateCours() {
        return dateCours;
    }
    
    public void setDateCours(java.sql.Date dateCours) {
        this.dateCours = dateCours;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifier si l'étudiant est présent
     */
    public boolean isPresent() {
        return "PRESENT".equals(statut);
    }
    
    /**
     * Vérifier si l'étudiant est absent
     */
    public boolean isAbsent() {
        return "ABSENT".equals(statut);
    }
    
    /**
     * Vérifier si l'étudiant est en retard accepté
     */
    public boolean isRetardAccepte() {
        return "RETARD_ACCEPTE".equals(statut);
    }
    
    /**
     * Vérifier si l'étudiant est en retard refusé
     */
    public boolean isRetardRefuse() {
        return "RETARD_REFUSE".equals(statut);
    }
    
    /**
     * Obtenir le nom complet de l'étudiant
     */
    public String getEtudiantComplet() {
        if (prenomEtudiant != null && nomEtudiant != null) {
            return prenomEtudiant + " " + nomEtudiant;
        }
        return "";
    }
    
    /**
     * Obtenir le libellé du statut en français
     */
    public String getStatutLibelle() {
        switch (statut) {
            case "PRESENT": return "Présent";
            case "ABSENT": return "Absent";
            case "RETARD_ACCEPTE": return "Retard accepté";
            case "RETARD_REFUSE": return "Retard refusé";
            default: return statut;
        }
    }
    
    @Override
    public String toString() {
        return "Presence{" +
                "presenceId=" + presenceId +
                ", coursId=" + coursId +
                ", etudiantId=" + etudiantId +
                ", statut='" + statut + '\'' +
                ", heureArrivee=" + heureArrivee +
                '}';
    }
}