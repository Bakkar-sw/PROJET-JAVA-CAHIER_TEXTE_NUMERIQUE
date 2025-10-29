package com.cahiertexte.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cahiertexte.model.Justificatif;

/**
 * DAO pour la gestion des justificatifs d'absence
 * Gère toutes les opérations CRUD sur la table 'justificatifs'
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class JustificatifDAO {
    
    private Connection connection;
    
    public JustificatifDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Récupérer un justificatif par son ID
     */
    public Justificatif getJustificatifById(int justificatifId) {
        String sql = "SELECT j.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours, " +
                     "v.nom as valid_nom, v.prenom as valid_prenom " +
                     "FROM justificatifs j " +
                     "JOIN users u ON j.etudiant_id = u.user_id " +
                     "JOIN cours c ON j.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "LEFT JOIN users v ON j.valide_par = v.user_id " +
                     "WHERE j.justificatif_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, justificatifId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractJustificatifFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getJustificatifById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer tous les justificatifs
     */
    public List<Justificatif> getAllJustificatifs() {
        List<Justificatif> justificatifs = new ArrayList<>();
        String sql = "SELECT j.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours, " +
                     "v.nom as valid_nom, v.prenom as valid_prenom " +
                     "FROM justificatifs j " +
                     "JOIN users u ON j.etudiant_id = u.user_id " +
                     "JOIN cours c ON j.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "LEFT JOIN users v ON j.valide_par = v.user_id " +
                     "ORDER BY j.date_soumission DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                justificatifs.add(extractJustificatifFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getAllJustificatifs : " + e.getMessage());
            e.printStackTrace();
        }
        
        return justificatifs;
    }
    
    /**
     * Récupérer les justificatifs d'un étudiant
     */
    public List<Justificatif> getJustificatifsByEtudiant(int etudiantId) {
        List<Justificatif> justificatifs = new ArrayList<>();
        String sql = "SELECT j.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours, " +
                     "v.nom as valid_nom, v.prenom as valid_prenom " +
                     "FROM justificatifs j " +
                     "JOIN users u ON j.etudiant_id = u.user_id " +
                     "JOIN cours c ON j.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "LEFT JOIN users v ON j.valide_par = v.user_id " +
                     "WHERE j.etudiant_id = ? " +
                     "ORDER BY j.date_soumission DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                justificatifs.add(extractJustificatifFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getJustificatifsByEtudiant : " + e.getMessage());
            e.printStackTrace();
        }
        
        return justificatifs;
    }
    
    /**
     * Récupérer les justificatifs par statut
     */
    public List<Justificatif> getJustificatifsByStatut(String statut) {
        List<Justificatif> justificatifs = new ArrayList<>();
        String sql = "SELECT j.*, u.nom, u.prenom, u.classe, m.nom_matiere, c.date_cours, " +
                     "v.nom as valid_nom, v.prenom as valid_prenom " +
                     "FROM justificatifs j " +
                     "JOIN users u ON j.etudiant_id = u.user_id " +
                     "JOIN cours c ON j.cours_id = c.cours_id " +
                     "JOIN matieres m ON c.matiere_id = m.matiere_id " +
                     "LEFT JOIN users v ON j.valide_par = v.user_id " +
                     "WHERE j.statut = ? " +
                     "ORDER BY j.date_soumission ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                justificatifs.add(extractJustificatifFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getJustificatifsByStatut : " + e.getMessage());
            e.printStackTrace();
        }
        
        return justificatifs;
    }
    
    /**
     * Récupérer les justificatifs en attente
     */
    public List<Justificatif> getJustificatifsEnAttente() {
        return getJustificatifsByStatut("EN_ATTENTE");
    }
    
    /**
     * Créer un nouveau justificatif
     */
    public boolean createJustificatif(Justificatif justificatif) {
        String sql = "INSERT INTO justificatifs (etudiant_id, cours_id, motif, type_justificatif, " +
                     "fichier_path, fichier_nom, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, justificatif.getEtudiantId());
            pstmt.setInt(2, justificatif.getCoursId());
            pstmt.setString(3, justificatif.getMotif());
            pstmt.setString(4, justificatif.getTypeJustificatif());
            pstmt.setString(5, justificatif.getFichierPath());
            pstmt.setString(6, justificatif.getFichierNom());
            pstmt.setString(7, justificatif.getStatut() != null ? justificatif.getStatut() : "EN_ATTENTE");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    justificatif.setJustificatifId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur createJustificatif : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mettre à jour l'avis du responsable de classe
     */
    public boolean updateAvisResponsable(int justificatifId, String avis) {
        String sql = "UPDATE justificatifs SET statut = 'VU_RESPONSABLE_CLASSE', " +
                     "avis_responsable_classe = ?, date_avis_responsable = GETDATE() " +
                     "WHERE justificatif_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, avis);
            pstmt.setInt(2, justificatifId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateAvisResponsable : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Valider ou refuser un justificatif (par responsable de formation)
     */
    public boolean validerJustificatif(int justificatifId, boolean accepte, 
                                       String commentaire, int validePar) {
        String sql = "UPDATE justificatifs SET statut = ?, commentaire_validation = ?, " +
                     "valide_par = ?, date_validation = GETDATE() WHERE justificatif_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accepte ? "ACCEPTE" : "REFUSE");
            pstmt.setString(2, commentaire);
            pstmt.setInt(3, validePar);
            pstmt.setInt(4, justificatifId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur validerJustificatif : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Modifier un justificatif
     */
    public boolean updateJustificatif(Justificatif justificatif) {
        String sql = "UPDATE justificatifs SET motif = ?, type_justificatif = ?, " +
                     "fichier_path = ?, fichier_nom = ? WHERE justificatif_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, justificatif.getMotif());
            pstmt.setString(2, justificatif.getTypeJustificatif());
            pstmt.setString(3, justificatif.getFichierPath());
            pstmt.setString(4, justificatif.getFichierNom());
            pstmt.setInt(5, justificatif.getJustificatifId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateJustificatif : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer un justificatif
     */
    public boolean deleteJustificatif(int justificatifId) {
        String sql = "DELETE FROM justificatifs WHERE justificatif_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, justificatifId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur deleteJustificatif : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Extraire un justificatif depuis un ResultSet
     */
    private Justificatif extractJustificatifFromResultSet(ResultSet rs) throws SQLException {
        Justificatif justif = new Justificatif();
        justif.setJustificatifId(rs.getInt("justificatif_id"));
        justif.setEtudiantId(rs.getInt("etudiant_id"));
        justif.setCoursId(rs.getInt("cours_id"));
        justif.setMotif(rs.getString("motif"));
        justif.setTypeJustificatif(rs.getString("type_justificatif"));
        justif.setFichierPath(rs.getString("fichier_path"));
        justif.setFichierNom(rs.getString("fichier_nom"));
        justif.setStatut(rs.getString("statut"));
        justif.setAvisResponsableClasse(rs.getString("avis_responsable_classe"));
        justif.setDateAvisResponsable(rs.getTimestamp("date_avis_responsable"));
        justif.setCommentaireValidation(rs.getString("commentaire_validation"));
        justif.setValidePar(rs.getInt("valide_par"));
        justif.setDateValidation(rs.getTimestamp("date_validation"));
        justif.setDateSoumission(rs.getTimestamp("date_soumission"));
        justif.setDateModification(rs.getTimestamp("date_modification"));
        
        // Informations supplémentaires
        justif.setNomEtudiant(rs.getString("nom"));
        justif.setPrenomEtudiant(rs.getString("prenom"));
        justif.setClasseEtudiant(rs.getString("classe"));
        justif.setNomMatiere(rs.getString("nom_matiere"));
        justif.setDateCours(rs.getDate("date_cours"));
        
        String validNom = rs.getString("valid_nom");
        String validPrenom = rs.getString("valid_prenom");
        if (validNom != null && validPrenom != null) {
            justif.setNomValidateur(validPrenom + " " + validNom);
        }
        
        return justif;
    }
    
    /**
     * Test du DAO
     */
    public static void main(String[] args) {
        System.out.println("=== TEST JustificatifDAO ===\n");
        
        JustificatifDAO justifDAO = new JustificatifDAO();
        
        // Test : Récupérer tous les justificatifs
        System.out.println("Liste des justificatifs :");
        List<Justificatif> justificatifs = justifDAO.getAllJustificatifs();
        System.out.println("Nombre total : " + justificatifs.size());
        
        for (Justificatif j : justificatifs) {
            System.out.println("  - " + j.getEtudiantComplet() + " - " + 
                             j.getTypeLibelle() + " - Statut: " + j.getStatutLibelle());
        }
        
        // Test : Justificatifs en attente
        System.out.println("\nJustificatifs en attente :");
        List<Justificatif> enAttente = justifDAO.getJustificatifsEnAttente();
        System.out.println("Nombre : " + enAttente.size());
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}