package com.cahiertexte.model;

import java.sql.Timestamp;

/**
 * Classe entité représentant un utilisateur du système
 * Correspond à la table 'users' dans la base de données
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class User {
    
    // Attributs correspondant aux colonnes de la table
    private int userId;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role; // RESPONSABLE_CLASSE, RESPONSABLE_FORMATION, PROFESSEUR, ETUDIANT
    private String classe; // CI_M1, CI_M2, MCS_M1, MCS_M2
    private String statut; // ACTIF, INACTIF, SUSPENDU
    private Timestamp dateCreation;
    private Timestamp derniereConnexion;
    
    // Constructeur par défaut
    public User() {
    }
    
    // Constructeur avec paramètres essentiels
    public User(String username, String password, String nom, String prenom, String email, String role) {
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.statut = "ACTIF";
    }
    
    // Constructeur complet
    public User(int userId, String username, String password, String nom, String prenom, 
                String email, String telephone, String role, String classe, String statut) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.classe = classe;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Timestamp getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Timestamp getDerniereConnexion() {
        return derniereConnexion;
    }
    
    public void setDerniereConnexion(Timestamp derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
    
    // Méthodes utilitaires
    
    /**
     * Obtenir le nom complet (nom + prénom)
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    /**
     * Vérifier si l'utilisateur est actif
     */
    public boolean isActif() {
        return "ACTIF".equals(statut);
    }
    
    /**
     * Vérifier le type de rôle
     */
    public boolean isResponsableClasse() {
        return "RESPONSABLE_CLASSE".equals(role);
    }
    
    public boolean isResponsableFormation() {
        return "RESPONSABLE_FORMATION".equals(role);
    }
    
    public boolean isProfesseur() {
        return "PROFESSEUR".equals(role);
    }
    
    public boolean isEtudiant() {
        return "ETUDIANT".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", classe='" + classe + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}