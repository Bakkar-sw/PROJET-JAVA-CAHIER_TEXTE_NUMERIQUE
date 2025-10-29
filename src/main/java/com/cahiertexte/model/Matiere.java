package com.cahiertexte.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Classe entité représentant une matière enseignée
 * Correspond à la table 'matieres' dans la base de données
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class Matiere {
    
    private int matiereId;
    private String codeMatiere;
    private String nomMatiere;
    private String classe; // CI_M1, CI_M2, MCS_M1, MCS_M2
    private BigDecimal volumeHoraireTotal;
    private int coefficient;
    private String semestre; // SEMESTRE_1, SEMESTRE_2
    private String anneeAcademique; // Ex: 2024-2025
    private int professeurId;
    private Timestamp dateCreation;
    
    // Attribut supplémentaire (non en base) pour affichage
    private String nomProfesseur;
    
    // Constructeur par défaut
    public Matiere() {
    }
    
    // Constructeur avec paramètres essentiels
    public Matiere(String codeMatiere, String nomMatiere, String classe, 
                   BigDecimal volumeHoraireTotal, String semestre, String anneeAcademique) {
        this.codeMatiere = codeMatiere;
        this.nomMatiere = nomMatiere;
        this.classe = classe;
        this.volumeHoraireTotal = volumeHoraireTotal;
        this.semestre = semestre;
        this.anneeAcademique = anneeAcademique;
        this.coefficient = 1; // Par défaut
    }
    
    // Constructeur complet
    public Matiere(int matiereId, String codeMatiere, String nomMatiere, String classe,
                   BigDecimal volumeHoraireTotal, int coefficient, String semestre, 
                   String anneeAcademique, int professeurId) {
        this.matiereId = matiereId;
        this.codeMatiere = codeMatiere;
        this.nomMatiere = nomMatiere;
        this.classe = classe;
        this.volumeHoraireTotal = volumeHoraireTotal;
        this.coefficient = coefficient;
        this.semestre = semestre;
        this.anneeAcademique = anneeAcademique;
        this.professeurId = professeurId;
    }
    
    // Getters et Setters
    public int getMatiereId() {
        return matiereId;
    }
    
    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }
    
    public String getCodeMatiere() {
        return codeMatiere;
    }
    
    public void setCodeMatiere(String codeMatiere) {
        this.codeMatiere = codeMatiere;
    }
    
    public String getNomMatiere() {
        return nomMatiere;
    }
    
    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public BigDecimal getVolumeHoraireTotal() {
        return volumeHoraireTotal;
    }
    
    public void setVolumeHoraireTotal(BigDecimal volumeHoraireTotal) {
        this.volumeHoraireTotal = volumeHoraireTotal;
    }
    
    public int getCoefficient() {
        return coefficient;
    }
    
    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }
    
    public String getSemestre() {
        return semestre;
    }
    
    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }
    
    public String getAnneeAcademique() {
        return anneeAcademique;
    }
    
    public void setAnneeAcademique(String anneeAcademique) {
        this.anneeAcademique = anneeAcademique;
    }
    
    public int getProfesseurId() {
        return professeurId;
    }
    
    public void setProfesseurId(int professeurId) {
        this.professeurId = professeurId;
    }
    
    public Timestamp getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public String getNomProfesseur() {
        return nomProfesseur;
    }
    
    public void setNomProfesseur(String nomProfesseur) {
        this.nomProfesseur = nomProfesseur;
    }
    
    // Méthodes utilitaires
    
    /**
     * Obtenir le libellé complet de la matière
     */
    public String getLibelleComplet() {
        return codeMatiere + " - " + nomMatiere;
    }
    
    /**
     * Vérifier si la matière a un professeur assigné
     */
    public boolean hasAssignedProfesseur() {
        return professeurId > 0;
    }
    
    @Override
    public String toString() {
        return "Matiere{" +
                "matiereId=" + matiereId +
                ", codeMatiere='" + codeMatiere + '\'' +
                ", nomMatiere='" + nomMatiere + '\'' +
                ", classe='" + classe + '\'' +
                ", volumeHoraireTotal=" + volumeHoraireTotal +
                ", coefficient=" + coefficient +
                ", semestre='" + semestre + '\'' +
                ", anneeAcademique='" + anneeAcademique + '\'' +
                '}';
    }
}