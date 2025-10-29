package com.cahiertexte.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Classe entité représentant un cours (séance)
 * Correspond à la table 'cours' dans la base de données
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class Cours {
    
    private int coursId;
    private int matiereId;
    private int professeurId;
    private String classe; // CI_M1, CI_M2, MCS_M1, MCS_M2
    
    // Planification
    private Date dateCours;
    private Time heureDebutPrevue;
    private Time heureFinPrevue;
    
    // Réalisation
    private Time heureDebutReelle;
    private Time heureFinReelle;
    private BigDecimal dureeEffective;
    
    // Contenu pédagogique
    private String contenuCours;
    private String objectifs;
    private String travauxDonnes;
    private String ressources;
    private String commentaireProfesseur;
    
    // Statuts
    private String statutCours; // PLANIFIE, REALISE, ANNULE, RATTRAPAGE
    private boolean estValide;
    private Timestamp dateValidation;
    
    // Gestion
    private String salle;
    private int responsableSaisieId;
    private Timestamp dateSaisie;
    private Timestamp dateCreation;
    private Timestamp dateModification;
    
    // Attributs supplémentaires (non en base) pour affichage
    private String nomMatiere;
    private String nomProfesseur;
    private String prenomProfesseur;
    
    // Constructeur par défaut
    public Cours() {
    }
    
    // Constructeur pour planification de cours
    public Cours(int matiereId, int professeurId, String classe, Date dateCours, 
                 Time heureDebutPrevue, Time heureFinPrevue, String salle) {
        this.matiereId = matiereId;
        this.professeurId = professeurId;
        this.classe = classe;
        this.dateCours = dateCours;
        this.heureDebutPrevue = heureDebutPrevue;
        this.heureFinPrevue = heureFinPrevue;
        this.salle = salle;
        this.statutCours = "PLANIFIE";
        this.estValide = false;
    }
    
    // Getters et Setters
    public int getCoursId() {
        return coursId;
    }
    
    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }
    
    public int getMatiereId() {
        return matiereId;
    }
    
    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }
    
    public int getProfesseurId() {
        return professeurId;
    }
    
    public void setProfesseurId(int professeurId) {
        this.professeurId = professeurId;
    }
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public Date getDateCours() {
        return dateCours;
    }
    
    public void setDateCours(Date dateCours) {
        this.dateCours = dateCours;
    }
    
    public Time getHeureDebutPrevue() {
        return heureDebutPrevue;
    }
    
    public void setHeureDebutPrevue(Time heureDebutPrevue) {
        this.heureDebutPrevue = heureDebutPrevue;
    }
    
    public Time getHeureFinPrevue() {
        return heureFinPrevue;
    }
    
    public void setHeureFinPrevue(Time heureFinPrevue) {
        this.heureFinPrevue = heureFinPrevue;
    }
    
    public Time getHeureDebutReelle() {
        return heureDebutReelle;
    }
    
    public void setHeureDebutReelle(Time heureDebutReelle) {
        this.heureDebutReelle = heureDebutReelle;
    }
    
    public Time getHeureFinReelle() {
        return heureFinReelle;
    }
    
    public void setHeureFinReelle(Time heureFinReelle) {
        this.heureFinReelle = heureFinReelle;
    }
    
    public BigDecimal getDureeEffective() {
        return dureeEffective;
    }
    
    public void setDureeEffective(BigDecimal dureeEffective) {
        this.dureeEffective = dureeEffective;
    }
    
    public String getContenuCours() {
        return contenuCours;
    }
    
    public void setContenuCours(String contenuCours) {
        this.contenuCours = contenuCours;
    }
    
    public String getObjectifs() {
        return objectifs;
    }
    
    public void setObjectifs(String objectifs) {
        this.objectifs = objectifs;
    }
    
    public String getTravauxDonnes() {
        return travauxDonnes;
    }
    
    public void setTravauxDonnes(String travauxDonnes) {
        this.travauxDonnes = travauxDonnes;
    }
    
    public String getRessources() {
        return ressources;
    }
    
    public void setRessources(String ressources) {
        this.ressources = ressources;
    }
    
    public String getCommentaireProfesseur() {
        return commentaireProfesseur;
    }
    
    public void setCommentaireProfesseur(String commentaireProfesseur) {
        this.commentaireProfesseur = commentaireProfesseur;
    }
    
    public String getStatutCours() {
        return statutCours;
    }
    
    public void setStatutCours(String statutCours) {
        this.statutCours = statutCours;
    }
    
    public boolean isEstValide() {
        return estValide;
    }
    
    public void setEstValide(boolean estValide) {
        this.estValide = estValide;
    }
    
    public Timestamp getDateValidation() {
        return dateValidation;
    }
    
    public void setDateValidation(Timestamp dateValidation) {
        this.dateValidation = dateValidation;
    }
    
    public String getSalle() {
        return salle;
    }
    
    public void setSalle(String salle) {
        this.salle = salle;
    }
    
    public int getResponsableSaisieId() {
        return responsableSaisieId;
    }
    
    public void setResponsableSaisieId(int responsableSaisieId) {
        this.responsableSaisieId = responsableSaisieId;
    }
    
    public Timestamp getDateSaisie() {
        return dateSaisie;
    }
    
    public void setDateSaisie(Timestamp dateSaisie) {
        this.dateSaisie = dateSaisie;
    }
    
    public Timestamp getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Timestamp getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
    }
    
    public String getNomMatiere() {
        return nomMatiere;
    }
    
    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }
    
    public String getNomProfesseur() {
        return nomProfesseur;
    }
    
    public void setNomProfesseur(String nomProfesseur) {
        this.nomProfesseur = nomProfesseur;
    }
    
    public String getPrenomProfesseur() {
        return prenomProfesseur;
    }
    
    public void setPrenomProfesseur(String prenomProfesseur) {
        this.prenomProfesseur = prenomProfesseur;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifier si le cours est planifié
     */
    public boolean isPlanifie() {
        return "PLANIFIE".equals(statutCours);
    }
    
    /**
     * Vérifier si le cours est réalisé
     */
    public boolean isRealise() {
        return "REALISE".equals(statutCours);
    }
    
    /**
     * Vérifier si le cours est annulé
     */
    public boolean isAnnule() {
        return "ANNULE".equals(statutCours);
    }
    
    /**
     * Vérifier si c'est un cours de rattrapage
     */
    public boolean isRattrapage() {
        return "RATTRAPAGE".equals(statutCours);
    }
    
    /**
     * Obtenir le nom complet du professeur
     */
    public String getProfesseurComplet() {
        if (prenomProfesseur != null && nomProfesseur != null) {
            return prenomProfesseur + " " + nomProfesseur;
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Cours{" +
                "coursId=" + coursId +
                ", matiereId=" + matiereId +
                ", classe='" + classe + '\'' +
                ", dateCours=" + dateCours +
                ", statutCours='" + statutCours + '\'' +
                ", estValide=" + estValide +
                ", salle='" + salle + '\'' +
                '}';
    }
}