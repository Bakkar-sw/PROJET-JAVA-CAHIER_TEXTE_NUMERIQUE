package com.cahiertexte.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cahiertexte.model.Matiere;

/**
 * DAO pour la gestion des matières
 * Gère toutes les opérations CRUD sur la table 'matieres'
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class MatiereDAO {
    
    private Connection connection;
    
    public MatiereDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Récupérer une matière par son ID
     */
    public Matiere getMatiereById(int matiereId) {
        String sql = "SELECT m.*, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM matieres m " +
                     "LEFT JOIN users u ON m.professeur_id = u.user_id " +
                     "WHERE m.matiere_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, matiereId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMatiereFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getMatiereById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer toutes les matières
     */
    public List<Matiere> getAllMatieres() {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT m.*, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM matieres m " +
                     "LEFT JOIN users u ON m.professeur_id = u.user_id " +
                     "ORDER BY m.classe, m.nom_matiere";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                matieres.add(extractMatiereFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getAllMatieres : " + e.getMessage());
            e.printStackTrace();
        }
        
        return matieres;
    }
    
    /**
     * Récupérer les matières d'une classe
     */
    public List<Matiere> getMatieresByClasse(String classe) {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT m.*, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM matieres m " +
                     "LEFT JOIN users u ON m.professeur_id = u.user_id " +
                     "WHERE m.classe = ? " +
                     "ORDER BY m.nom_matiere";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, classe);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                matieres.add(extractMatiereFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getMatieresByClasse : " + e.getMessage());
            e.printStackTrace();
        }
        
        return matieres;
    }
    
    /**
     * Récupérer les matières d'un professeur
     */
    public List<Matiere> getMatieresByProfesseur(int professeurId) {
        List<Matiere> matieres = new ArrayList<>();
        String sql = "SELECT m.*, u.nom as prof_nom, u.prenom as prof_prenom " +
                     "FROM matieres m " +
                     "LEFT JOIN users u ON m.professeur_id = u.user_id " +
                     "WHERE m.professeur_id = ? " +
                     "ORDER BY m.classe, m.nom_matiere";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, professeurId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                matieres.add(extractMatiereFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getMatieresByProfesseur : " + e.getMessage());
            e.printStackTrace();
        }
        
        return matieres;
    }
    
    /**
     * Créer une nouvelle matière
     */
    public boolean createMatiere(Matiere matiere) {
        String sql = "INSERT INTO matieres (code_matiere, nom_matiere, classe, volume_horaire_total, " +
                     "coefficient, semestre, annee_academique, professeur_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, matiere.getCodeMatiere());
            pstmt.setString(2, matiere.getNomMatiere());
            pstmt.setString(3, matiere.getClasse());
            pstmt.setBigDecimal(4, matiere.getVolumeHoraireTotal());
            pstmt.setInt(5, matiere.getCoefficient());
            pstmt.setString(6, matiere.getSemestre());
            pstmt.setString(7, matiere.getAnneeAcademique());
            
            if (matiere.getProfesseurId() > 0) {
                pstmt.setInt(8, matiere.getProfesseurId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    matiere.setMatiereId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur createMatiere : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Modifier une matière
     */
    public boolean updateMatiere(Matiere matiere) {
        String sql = "UPDATE matieres SET code_matiere = ?, nom_matiere = ?, classe = ?, " +
                     "volume_horaire_total = ?, coefficient = ?, semestre = ?, " +
                     "annee_academique = ?, professeur_id = ? WHERE matiere_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, matiere.getCodeMatiere());
            pstmt.setString(2, matiere.getNomMatiere());
            pstmt.setString(3, matiere.getClasse());
            pstmt.setBigDecimal(4, matiere.getVolumeHoraireTotal());
            pstmt.setInt(5, matiere.getCoefficient());
            pstmt.setString(6, matiere.getSemestre());
            pstmt.setString(7, matiere.getAnneeAcademique());
            
            if (matiere.getProfesseurId() > 0) {
                pstmt.setInt(8, matiere.getProfesseurId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            pstmt.setInt(9, matiere.getMatiereId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateMatiere : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer une matière
     */
    public boolean deleteMatiere(int matiereId) {
        String sql = "DELETE FROM matieres WHERE matiere_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, matiereId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur deleteMatiere : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Extraire une matière depuis un ResultSet
     */
    private Matiere extractMatiereFromResultSet(ResultSet rs) throws SQLException {
        Matiere matiere = new Matiere();
        matiere.setMatiereId(rs.getInt("matiere_id"));
        matiere.setCodeMatiere(rs.getString("code_matiere"));
        matiere.setNomMatiere(rs.getString("nom_matiere"));
        matiere.setClasse(rs.getString("classe"));
        matiere.setVolumeHoraireTotal(rs.getBigDecimal("volume_horaire_total"));
        matiere.setCoefficient(rs.getInt("coefficient"));
        matiere.setSemestre(rs.getString("semestre"));
        matiere.setAnneeAcademique(rs.getString("annee_academique"));
        matiere.setProfesseurId(rs.getInt("professeur_id"));
        matiere.setDateCreation(rs.getTimestamp("date_creation"));
        
        // Nom du professeur si disponible
        String profNom = rs.getString("prof_nom");
        String profPrenom = rs.getString("prof_prenom");
        if (profNom != null && profPrenom != null) {
            matiere.setNomProfesseur(profPrenom + " " + profNom);
        }
        
        return matiere;
    }
    
    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        System.out.println("=== TEST MatiereDAO ===\n");
        
        MatiereDAO matiereDAO = new MatiereDAO();
        
        // Test : Récupérer toutes les matières
        System.out.println("Liste des matières :");
        List<Matiere> matieres = matiereDAO.getAllMatieres();
        for (Matiere m : matieres) {
            System.out.println("  - " + m.getNomMatiere() + " (" + m.getClasse() + ") - " + 
                             m.getVolumeHoraireTotal() + "h - Prof: " + m.getNomProfesseur());
        }
        
        // Test : Matières d'une classe
        System.out.println("\nMatières de CI_M1 :");
        List<Matiere> matieresCIM1 = matiereDAO.getMatieresByClasse("CI_M1");
        for (Matiere m : matieresCIM1) {
            System.out.println("  - " + m.getNomMatiere() + " (" + m.getVolumeHoraireTotal() + "h)");
        }
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}