package com.cahiertexte.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cahiertexte.model.User;

/**
 * DAO pour la gestion des utilisateurs
 * Gère toutes les opérations CRUD sur la table 'users'
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class UserDAO {
    
    private Connection connection;
    
    /**
     * Constructeur - Récupère la connexion à la BD
     */
    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Authentifier un utilisateur (login)
     * 
     * @param username Nom d'utilisateur
     * @param hashedPassword Mot de passe hashé
     * @return User si trouvé, null sinon
     */
    public User authenticate(String username, String hashedPassword) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND statut = 'ACTIF'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                
                // Mettre à jour la dernière connexion
                updateLastLogin(user.getUserId());
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer un utilisateur par son ID
     * 
     * @param userId ID de l'utilisateur
     * @return User ou null
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getUserById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer un utilisateur par son username
     * 
     * @param username Nom d'utilisateur
     * @return User ou null
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getUserByUsername : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupérer tous les utilisateurs
     * 
     * @return Liste de tous les utilisateurs
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getAllUsers : " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Récupérer les utilisateurs par rôle
     * 
     * @param role Le rôle recherché
     * @return Liste des utilisateurs
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY nom, prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getUsersByRole : " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Récupérer les étudiants d'une classe
     * 
     * @param classe La classe (CI_M1, CI_M2, etc.)
     * @return Liste des étudiants
     */
    public List<User> getEtudiantsByClasse(String classe) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'ETUDIANT' AND classe = ? ORDER BY nom, prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, classe);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getEtudiantsByClasse : " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Récupérer le responsable d'une classe
     * 
     * @param classe La classe
     * @return User responsable ou null
     */
    public User getResponsableByClasse(String classe) {
        String sql = "SELECT * FROM users WHERE role = 'RESPONSABLE_CLASSE' AND classe = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, classe);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getResponsableByClasse : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Créer un nouvel utilisateur
     * 
     * @param user L'utilisateur à créer
     * @return true si succès, false sinon
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, nom, prenom, email, telephone, role, classe, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNom());
            pstmt.setString(4, user.getPrenom());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getTelephone());
            pstmt.setString(7, user.getRole());
            pstmt.setString(8, user.getClasse());
            pstmt.setString(9, user.getStatut() != null ? user.getStatut() : "ACTIF");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupérer l'ID généré
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur createUser : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Modifier un utilisateur existant
     * 
     * @param user L'utilisateur à modifier
     * @return true si succès, false sinon
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, nom = ?, prenom = ?, email = ?, " +
                     "telephone = ?, role = ?, classe = ?, statut = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getNom());
            pstmt.setString(3, user.getPrenom());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getTelephone());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getClasse());
            pstmt.setString(8, user.getStatut());
            pstmt.setInt(9, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updateUser : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Modifier le mot de passe d'un utilisateur
     * 
     * @param userId ID de l'utilisateur
     * @param newHashedPassword Nouveau mot de passe hashé
     * @return true si succès, false sinon
     */
    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur updatePassword : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Supprimer un utilisateur
     * 
     * @param userId ID de l'utilisateur
     * @return true si succès, false sinon
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur deleteUser : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mettre à jour la dernière connexion d'un utilisateur
     * 
     * @param userId ID de l'utilisateur
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET derniere_connexion = GETDATE() WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erreur updateLastLogin : " + e.getMessage());
        }
    }
    
    /**
     * Vérifier si un username existe déjà
     * 
     * @param username Le username à vérifier
     * @return true si existe, false sinon
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur usernameExists : " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extraire un objet User depuis un ResultSet
     * 
     * @param rs Le ResultSet
     * @return User
     * @throws SQLException
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setTelephone(rs.getString("telephone"));
        user.setRole(rs.getString("role"));
        user.setClasse(rs.getString("classe"));
        user.setStatut(rs.getString("statut"));
        user.setDateCreation(rs.getTimestamp("date_creation"));
        user.setDerniereConnexion(rs.getTimestamp("derniere_connexion"));
        
        return user;
    }
    
    /**
     * Méthode main pour tester le DAO
     */
    public static void main(String[] args) {
        System.out.println("=== TEST UserDAO ===\n");
        
        UserDAO userDAO = new UserDAO();
        
        // Test 1 : Récupérer tous les utilisateurs
        System.out.println("1. Liste de tous les utilisateurs :");
        List<User> users = userDAO.getAllUsers();
        System.out.println("Nombre total : " + users.size());
        for (User u : users) {
            System.out.println("  - " + u.getNomComplet() + " (" + u.getRole() + ")");
        }
        
        // Test 2 : Authentification
        System.out.println("\n2. Test d'authentification :");
        String hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f";
        User authenticatedUser = userDAO.authenticate("prof.samb", hashedPassword);
        if (authenticatedUser != null) {
            System.out.println("✓ Authentification réussie : " + authenticatedUser.getNomComplet());
        } else {
            System.out.println("✗ Authentification échouée");
        }
        
        // Test 3 : Récupérer les étudiants d'une classe
        System.out.println("\n3. Étudiants de la classe CI_M1 :");
        List<User> etudiants = userDAO.getEtudiantsByClasse("CI_M1");
        System.out.println("Nombre : " + etudiants.size());
        for (User e : etudiants) {
            System.out.println("  - " + e.getNomComplet());
        }
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}