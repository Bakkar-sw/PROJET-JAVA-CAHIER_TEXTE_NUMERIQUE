package com.cahiertexte.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cahiertexte.model.Cours;

/**
 * DAO pour la gestion des cours
 * Gère toutes les opérations CRUD sur la table 'cours'
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class CoursDAO {
    
    private Connection connection;
    
    public CoursDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Récupérer un cours par son ID
     */
    public Cours getCoursById(int coursId) {
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "WHERE c.cours_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, coursId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCoursFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getCoursById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer tous les cours
     */
    public List<Cours> getAllCours() {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "ORDER BY c.date_cours DESC, c.heure_debut_prevue";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                coursList.add(extractCoursFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getAllCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return coursList;
    }
    
    /**
     * Récupérer les cours d'une classe
     */
    public List<Cours> getCoursByClasse(String classe) {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "WHERE c.classe = ? " +
                     "ORDER BY c.date_cours DESC, c.heure_debut_prevue";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, classe);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                coursList.add(extractCoursFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getCoursByClasse : " + e.getMessage());
            e.printStackTrace();
        }
        
        return coursList;
    }
    
    /**
     * Récupérer les cours d'un professeur
     */
    public List<Cours> getCoursByProfesseur(int professeurId) {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "WHERE c.professeur_id = ? " +
                     "ORDER BY c.date_cours DESC, c.heure_debut_prevue";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, professeurId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                coursList.add(extractCoursFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getCoursByProfesseur : " + e.getMessage());
            e.printStackTrace();
        }
        
        return coursList;
    }
    
    /**
     * Récupérer les cours d'une matière
     */
    public List<Cours> getCoursByMatiere(int matiereId) {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "WHERE c.matiere_id = ? " +
                     "ORDER BY c.date_cours, c.heure_debut_prevue";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, matiereId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                coursList.add(extractCoursFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getCoursByMatiere : " + e.getMessage());
            e.printStackTrace();
        }
        
        return coursList;
    }
    
    /**
     * Récupérer les cours non validés
     */
    public List<Cours> getCoursNonValides() {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT c.*, m.nom_matiere, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM cours c " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "JOIN users u ON c.professeur_id = u.user_id " +
                     "WHERE c.est_valide = 0 AND c.statut_cours = 'REALISE' " +
                     "ORDER BY c.date_saisie ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                coursList.add(extractCoursFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getCoursNonValides : " + e.getMessage());
            e.printStackTrace();
        }
        
        return coursList;
    }
    
    /**
     * Créer un nouveau cours
     */
    public boolean createCours(Cours cours) {
        String sql = "INSERT INTO cours (matiere_id, professeur_id, classe, date_cours, " +
                     "heure_debut_prevue, heure_fin_prevue, statut_cours, salle) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, cours.getMatiereId());
            pstmt.setInt(2, cours.getProfesseurId());
            pstmt.setString(3, cours.getClasse());
            pstmt.setDate(4, cours.getDateCours());
            pstmt.setTime(5, cours.getHeureDebutPrevue());
            pstmt.setTime(6, cours.getHeureFinPrevue());
            pstmt.setString(7, cours.getStatutCours() != null ? cours.getStatutCours() : "PLANIFIE");
            pstmt.setString(8, cours.getSalle());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cours.setCoursId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur createCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mettre à jour les informations de réalisation d'un cours
     */
    public boolean updateCoursRealisation(Cours cours) {
        String sql = "UPDATE cours SET heure_debut_reelle = ?, heure_fin_reelle = ?, " +
                     "duree_effective = ?, contenu_cours = ?, objectifs = ?, travaux_donnes = ?, " +
                     "ressources = ?, commentaire_professeur = ?, statut_cours = ?, " +
                     "responsable_saisie_id = ?, date_saisie = GETDATE() " +
                     "WHERE cours_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTime(1, cours.getHeureDebutReelle());
            pstmt.setTime(2, cours.getHeureFinReelle());
            pstmt.setBigDecimal(3, cours.getDureeEffective());
            pstmt.setString(4, cours.getContenuCours());
            pstmt.setString(5, cours.getObjectifs());
            pstmt.setString(6, cours.getTravauxDonnes());
            pstmt.setString(7, cours.getRessources());
            pstmt.setString(8, cours.getCommentaireProfesseur());
            pstmt.setString(9, "REALISE");
            pstmt.setInt(10, cours.getResponsableSaisieId());
            pstmt.setInt(11, cours.getCoursId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateCoursRealisation : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Valider un cours
     */
    public boolean validerCours(int coursId) {
        String sql = "UPDATE cours SET est_valide = 1, date_validation = GETDATE() WHERE cours_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, coursId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur validerCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Modifier un cours
     */
    public boolean updateCours(Cours cours) {
        String sql = "UPDATE cours SET matiere_id = ?, professeur_id = ?, classe = ?, " +
                     "date_cours = ?, heure_debut_prevue = ?, heure_fin_prevue = ?, " +
                     "salle = ?, statut_cours = ? WHERE cours_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, cours.getMatiereId());
            pstmt.setInt(2, cours.getProfesseurId());
            pstmt.setString(3, cours.getClasse());
            pstmt.setDate(4, cours.getDateCours());
            pstmt.setTime(5, cours.getHeureDebutPrevue());
            pstmt.setTime(6, cours.getHeureFinPrevue());
            pstmt.setString(7, cours.getSalle());
            pstmt.setString(8, cours.getStatutCours());
            pstmt.setInt(9, cours.getCoursId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer un cours
     */
    public boolean deleteCours(int coursId) {
        String sql = "DELETE FROM cours WHERE cours_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, coursId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur deleteCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Extraire un cours depuis un ResultSet
     */
    private Cours extractCoursFromResultSet(ResultSet rs) throws SQLException {
        Cours cours = new Cours();
        cours.setCoursId(rs.getInt("cours_id"));
        cours.setMatiereId(rs.getInt("matiere_id"));
        cours.setProfesseurId(rs.getInt("professeur_id"));
        cours.setClasse(rs.getString("classe"));
        cours.setDateCours(rs.getDate("date_cours"));
        cours.setHeureDebutPrevue(rs.getTime("heure_debut_prevue"));
        cours.setHeureFinPrevue(rs.getTime("heure_fin_prevue"));
        cours.setHeureDebutReelle(rs.getTime("heure_debut_reelle"));
        cours.setHeureFinReelle(rs.getTime("heure_fin_reelle"));
        cours.setDureeEffective(rs.getBigDecimal("duree_effective"));
        cours.setContenuCours(rs.getString("contenu_cours"));
        cours.setObjectifs(rs.getString("objectifs"));
        cours.setTravauxDonnes(rs.getString("travaux_donnes"));
        cours.setRessources(rs.getString("ressources"));
        cours.setCommentaireProfesseur(rs.getString("commentaire_professeur"));
        cours.setStatutCours(rs.getString("statut_cours"));
        cours.setEstValide(rs.getBoolean("est_valide"));
        cours.setDateValidation(rs.getTimestamp("date_validation"));
        cours.setSalle(rs.getString("salle"));
        cours.setResponsableSaisieId(rs.getInt("responsable_saisie_id"));
        cours.setDateSaisie(rs.getTimestamp("date_saisie"));
        cours.setDateCreation(rs.getTimestamp("date_creation"));
        cours.setDateModification(rs.getTimestamp("date_modification"));
        
        // Informations supplémentaires
        cours.setNomMatiere(rs.getString("nom_matiere"));
        cours.setNomProfesseur(rs.getString("prof_nom"));
        cours.setPrenomProfesseur(rs.getString("prof_prenom"));
        
        return cours;
    }
    
    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        System.out.println("=== TEST CoursDAO ===\n");
        
        CoursDAO coursDAO = new CoursDAO();
        
        // Test : Récupérer tous les cours
        System.out.println("Liste des cours :");
        List<Cours> coursList = coursDAO.getAllCours();
        for (Cours c : coursList) {
            System.out.println("  - " + c.getNomMatiere() + " (" + c.getClasse() + ") - " + 
                             c.getDateCours() + " " + c.getHeureDebutPrevue() + 
                             " - Statut: " + c.getStatutCours());
        }
        
        // Test : Cours d'une classe
        System.out.println("\nCours de CI_M1 :");
        List<Cours> coursCIM1 = coursDAO.getCoursByClasse("CI_M1");
        System.out.println("Nombre : " + coursCIM1.size());
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}