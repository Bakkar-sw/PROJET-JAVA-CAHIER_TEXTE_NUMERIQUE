package com.cahiertexte.model;

import java.sql.Timestamp;

/**
 * Classe entité représentant un justificatif d'absence
 * Correspond à la table 'justificatifs' dans la base de données
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class Justificatif {
    
    private int justificatifId;
    private int etudiantId;
    private int coursId;
    
    // Informations du justificatif
    private String motif;
    private String typeJustificatif; // MEDICAL, ADMINISTRATIF, FAMILIAL, AUTRE
    private String fichierPath;
    private String fichierNom;
    
    // Workflow de validation
    private String statut; // EN_ATTENTE, VU_RESPONSABLE_CLASSE, ACCEPTE, REFUSE
    private String avisResponsableClasse;
    private Timestamp dateAvisResponsable;
    private String commentaireValidation;
    private int validePar;
    private Timestamp dateValidation;
    
    // Dates
    private Timestamp dateSoumission;
    private Timestamp dateModification;
    
    // Attributs supplémentaires (non en base) pour affichage
    private String nomEtudiant;
    private String prenomEtudiant;
    private String classeEtudiant;
    private String nomMatiere;
    private java.sql.Date dateCours;
    private String nomValidateur;
    
    // Constructeur par défaut
    public Justificatif() {
    }
    
    // Constructeur avec paramètres essentiels
    public Justificatif(int etudiantId, int coursId, String motif, String typeJustificatif) {
        this.etudiantId = etudiantId;
        this.coursId = coursId;
        this.motif = motif;
        this.typeJustificatif = typeJustificatif;
        this.statut = "EN_ATTENTE";
    }
    
    // Getters et Setters
    public int getJustificatifId() {
        return justificatifId;
    }
    
    public void setJustificatifId(int justificatifId) {
        this.justificatifId = justificatifId;
    }
    
    public int getEtudiantId() {
        return etudiantId;
    }
    
    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }
    
    public int getCoursId() {
        return coursId;
    }
    
    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public String getTypeJustificatif() {
        return typeJustificatif;
    }
    
    public void setTypeJustificatif(String typeJustificatif) {
        this.typeJustificatif = typeJustificatif;
    }
    
    public String getFichierPath() {
        return fichierPath;
    }
    
    public void setFichierPath(String fichierPath) {
        this.fichierPath = fichierPath;
    }
    
    public String getFichierNom() {
        return fichierNom;
    }
    
    public void setFichierNom(String fichierNom) {
        this.fichierNom = fichierNom;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getAvisResponsableClasse() {
        return avisResponsableClasse;
    }
    
    public void setAvisResponsableClasse(String avisResponsableClasse) {
        this.avisResponsableClasse = avisResponsableClasse;
    }
    
    public Timestamp getDateAvisResponsable() {
        return dateAvisResponsable;
    }
    
    public void setDateAvisResponsable(Timestamp dateAvisResponsable) {
        this.dateAvisResponsable = dateAvisResponsable;
    }
    
    public String getCommentaireValidation() {
        return commentaireValidation;
    }
    
    public void setCommentaireValidation(String commentaireValidation) {
        this.commentaireValidation = commentaireValidation;
    }
    
    public int getValidePar() {
        return validePar;
    }
    
    public void setValidePar(int validePar) {
        this.validePar = validePar;
    }
    
    public Timestamp getDateValidation() {
        return dateValidation;
    }
    
    public void setDateValidation(Timestamp dateValidation) {
        this.dateValidation = dateValidation;
    }
    
    public Timestamp getDateSoumission() {
        return dateSoumission;
    }
    
    public void setDateSoumission(Timestamp dateSoumission) {
        this.dateSoumission = dateSoumission;
    }
    
    public Timestamp getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
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
    
    public String getNomValidateur() {
        return nomValidateur;
    }
    
    public void setNomValidateur(String nomValidateur) {
        this.nomValidateur = nomValidateur;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifier si le justificatif est en attente
     */
    public boolean isEnAttente() {
        return "EN_ATTENTE".equals(statut);
    }
    
    /**
     * Vérifier si vu par le responsable de classe
     */
    public boolean isVuResponsableClasse() {
        return "VU_RESPONSABLE_CLASSE".equals(statut);
    }
    
    /**
     * Vérifier si accepté
     */
    public boolean isAccepte() {
        return "ACCEPTE".equals(statut);
    }
    
    /**
     * Vérifier si refusé
     */
    public boolean isRefuse() {
        return "REFUSE".equals(statut);
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
     * Vérifier si un fichier est attaché
     */
    public boolean hasFichier() {
        return fichierPath != null && !fichierPath.isEmpty();
    }
    
    /**
     * Obtenir le libellé du statut en français
     */
    public String getStatutLibelle() {
        switch (statut) {
            case "EN_ATTENTE": return "En attente";
            case "VU_RESPONSABLE_CLASSE": return "Vu par responsable";
            case "ACCEPTE": return "Accepté";
            case "REFUSE": return "Refusé";
            default: return statut;
        }
    }
    
    /**
     * Obtenir le libellé du type en français
     */
    public String getTypeLibelle() {
        switch (typeJustificatif) {
            case "MEDICAL": return "Médical";
            case "ADMINISTRATIF": return "Administratif";
            case "FAMILIAL": return "Familial";
            case "AUTRE": return "Autre";
            default: return typeJustificatif;
        }
    }
    
    @Override
    public String toString() {
        return "Justificatif{" +
                "justificatifId=" + justificatifId +
                ", etudiantId=" + etudiantId +
                ", coursId=" + coursId +
                ", typeJustificatif='" + typeJustificatif + '\'' +
                ", statut='" + statut + '\'' +
                ", dateSoumission=" + dateSoumission +
                '}';
    }
}