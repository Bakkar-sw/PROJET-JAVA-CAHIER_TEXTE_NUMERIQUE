package com.cahiertexte.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cahiertexte.model.Presence;

/**
 * DAO pour la gestion des présences
 * Gère toutes les opérations CRUD sur la table 'presences'
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class PresenceDAO {
    
    private Connection connection;
    
    public PresenceDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Récupérer une présence par son ID
     */
    public Presence getPresenceById(int presenceId) {
        String sql = "SELECT p.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours " +
                     "FROM presences p " +
                     "JOIN users u ON p.etudiant_id = u.user_id " +
                     "JOIN cours c ON p.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "WHERE p.presence_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, presenceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPresenceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getPresenceById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer toutes les présences d'un cours
     */
    public List<Presence> getPresencesByCours(int coursId) {
        List<Presence> presences = new ArrayList<>();
        String sql = "SELECT p.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours " +
                     "FROM presences p " +
                     "JOIN users u ON p.etudiant_id = u.user_id " +
                     "JOIN cours c ON p.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "WHERE p.cours_id = ? " +
                     "ORDER BY u.nom, u.prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, coursId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                presences.add(extractPresenceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getPresencesByCours : " + e.getMessage());
            e.printStackTrace();
        }
        
        return presences;
    }
    
    /**
     * Récupérer toutes les présences d'un étudiant
     */
    public List<Presence> getPresencesByEtudiant(int etudiantId) {
        List<Presence> presences = new ArrayList<>();
        String sql = "SELECT p.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours " +
                     "FROM presences p " +
                     "JOIN users u ON p.etudiant_id = u.user_id " +
                     "JOIN cours c ON p.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "WHERE p.etudiant_id = ? " +
                     "ORDER BY c.date_cours DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                presences.add(extractPresenceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getPresencesByEtudiant : " + e.getMessage());
            e.printStackTrace();
        }
        
        return presences;
    }
    
    /**
     * Récupérer les absences d'un étudiant
     */
    public List<Presence> getAbsencesByEtudiant(int etudiantId) {
        List<Presence> absences = new ArrayList<>();
        String sql = "SELECT p.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours " +
                     "FROM presences p " +
                     "JOIN users u ON p.etudiant_id = u.user_id " +
                     "JOIN cours c ON p.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "WHERE p.etudiant_id = ? AND p.statut = 'ABSENT' " +
                     "ORDER BY c.date_cours DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                absences.add(extractPresenceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getAbsencesByEtudiant : " + e.getMessage());
            e.printStackTrace();
        }
        
        return absences;
    }
    
    /**
     * Créer une nouvelle présence
     */
    public boolean createPresence(Presence presence) {
        String sql = "INSERT INTO presences (cours_id, etudiant_id, statut, commentaire, " +
                     "heure_arrivee, saisi_par) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, presence.getCoursId());
            pstmt.setInt(2, presence.getEtudiantId());
            pstmt.setString(3, presence.getStatut());
            pstmt.setString(4, presence.getCommentaire());
            
            if (presence.getHeureArrivee() != null) {
                pstmt.setTime(5, presence.getHeureArrivee());
            } else {
                pstmt.setNull(5, Types.TIME);
            }
            
            if (presence.getSaisiPar() > 0) {
                pstmt.setInt(6, presence.getSaisiPar());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    presence.setPresenceId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur createPresence : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Créer les présences pour tous les étudiants d'une classe pour un cours
     */
    public boolean createPresencesForCours(int coursId, String classe, int saisiPar) {
        // Récupérer tous les étudiants de la classe
        UserDAO userDAO = new UserDAO();
        var etudiants = userDAO.getEtudiantsByClasse(classe);
        
        boolean success = true;
        for (var etudiant : etudiants) {
            Presence presence = new Presence();
            presence.setCoursId(coursId);
            presence.setEtudiantId(etudiant.getUserId());
            presence.setStatut("PRESENT"); // Par défaut présent
            presence.setSaisiPar(saisiPar);
            
            if (!createPresence(presence)) {
                success = false;
            }
        }
        
        return success;
    }
    
    /**
     * Modifier une présence
     */
    public boolean updatePresence(Presence presence) {
        String sql = "UPDATE presences SET statut = ?, commentaire = ?, heure_arrivee = ? " +
                     "WHERE presence_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, presence.getStatut());
            pstmt.setString(2, presence.getCommentaire());
            
            if (presence.getHeureArrivee() != null) {
                pstmt.setTime(3, presence.getHeureArrivee());
            } else {
                pstmt.setNull(3, Types.TIME);
            }
            
            pstmt.setInt(4, presence.getPresenceId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updatePresence : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer une présence
     */
    public boolean deletePresence(int presenceId) {
        String sql = "DELETE FROM presences WHERE presence_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, presenceId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur deletePresence : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Calculer le taux de présence d'un étudiant
     */
    public double getTauxPresence(int etudiantId) {
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN statut = 'PRESENT' THEN 1 ELSE 0 END) as presents " +
                     "FROM presences WHERE etudiant_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int presents = rs.getInt("presents");
                
                if (total > 0) {
                    return (presents * 100.0) / total;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getTauxPresence : " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Compter le nombre d'absences d'un étudiant
     */
    public int countAbsences(int etudiantId) {
        String sql = "SELECT COUNT(*) as nb_absences FROM presences " +
                     "WHERE etudiant_id = ? AND statut = 'ABSENT'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("nb_absences");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur countAbsences : " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Extraire une présence depuis un ResultSet
     */
    private Presence extractPresenceFromResultSet(ResultSet rs) throws SQLException {
        Presence presence = new Presence();
        presence.setPresenceId(rs.getInt("presence_id"));
        presence.setCoursId(rs.getInt("cours_id"));
        presence.setEtudiantId(rs.getInt("etudiant_id"));
        presence.setStatut(rs.getString("statut"));
        presence.setCommentaire(rs.getString("commentaire"));
        presence.setHeureArrivee(rs.getTime("heure_arrivee"));
        presence.setDateSaisie(rs.getTimestamp("date_saisie"));
        presence.setSaisiPar(rs.getInt("saisi_par"));
        
        // Informations supplémentaires
        presence.setNomEtudiant(rs.getString("nom"));
        presence.setPrenomEtudiant(rs.getString("prenom"));
        presence.setClasseEtudiant(rs.getString("classe"));
        presence.setNomMatiere(rs.getString("nom_matiere"));
        presence.setDateCours(rs.getDate("date_cours"));
        
        return presence;
    }
    
    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        System.out.println("=== TEST PresenceDAO ===\n");
        
        PresenceDAO presenceDAO = new PresenceDAO();
        
        // Test : Récupérer les présences d'un cours
        System.out.println("Présences du cours ID 1 :");
        List<Presence> presences = presenceDAO.getPresencesByCours(1);
        for (Presence p : presences) {
            System.out.println("  - " + p.getEtudiantComplet() + " : " + p.getStatutLibelle());
        }
        
        // Test : Taux de présence d'un étudiant
        System.out.println("\nTaux de présence étudiant ID 5 : " + 
                         String.format("%.2f", presenceDAO.getTauxPresence(5)) + "%");
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}