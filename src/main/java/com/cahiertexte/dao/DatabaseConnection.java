package com.cahiertexte.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // Configuration SQL Server - INSTANCE NOMMÉE
    private static final String SERVER = "PHOENIX\\SQLSERVER";  // Mon instance nomme
    private static final String DATABASE = "cahier_texte_db";
    private static final String USERNAME = "admincahiertxt";
    private static final String PASSWORD = "xxxxxxxx";  // defaut x
    
    // URL de connexion pour instance nommée
    private static final String DB_URL = "jdbc:sqlserver://" + SERVER + 
                                         ";databaseName=" + DATABASE + 
                                         ";encrypt=false;trustServerCertificate=true";
    
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    // Instance unique (Singleton)
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        try {
            Class.forName(DRIVER);
            System.out.println("✓ Driver SQL Server chargé avec succès");
            
            this.connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("✓ Connexion à SQL Server établie avec succès");
            System.out.println("  Instance : " + SERVER);
            System.out.println("  Base de données : " + DATABASE);
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Erreur : Driver SQL Server non trouvé");
            e.printStackTrace();
            throw new RuntimeException("Driver SQL Server non trouvé", e);
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à SQL Server");
            System.err.println("  URL: " + DB_URL);
            System.err.println("  User: " + USERNAME);
            System.err.println("  Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à SQL Server", e);
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("⚠ Reconnexion à SQL Server...");
                connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                System.out.println("✓ Reconnexion réussie");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la reconnexion");
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion", e);
        }
        return connection;
    }
    
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("✗ Test de connexion échoué");
            e.printStackTrace();
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée avec succès");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture");
            e.printStackTrace();
        }
    }
    
    public String getConnectionInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMATIONS DE CONNEXION SQL SERVER ===\n");
        info.append("Instance: ").append(SERVER).append("\n");
        info.append("Base de données: ").append(DATABASE).append("\n");
        info.append("Utilisateur: ").append(USERNAME).append("\n");
        info.append("Driver: ").append(DRIVER).append("\n");
        
        try {
            if (connection != null) {
                info.append("État: ").append(connection.isClosed() ? "FERMÉE" : "OUVERTE").append("\n");
                info.append("Catalogue: ").append(connection.getCatalog()).append("\n");
            } else {
                info.append("État: NON INITIALISÉE\n");
            }
        } catch (SQLException e) {
            info.append("État: ERREUR - ").append(e.getMessage()).append("\n");
        }
        
        return info.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION SQL SERVER ===\n");
        
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            
            if (dbConn.testConnection()) {
                System.out.println("\n✓✓✓ CONNEXION RÉUSSIE ✓✓✓\n");
                System.out.println(dbConn.getConnectionInfo());
                
                Connection conn = dbConn.getConnection();
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) as total FROM users");
                
                if (rs.next()) {
                    System.out.println("\n=== TEST DE REQUÊTE ===");
                    System.out.println("Nombre d'utilisateurs : " + rs.getInt("total"));
                }
                
                rs.close();
                stmt.close();
                
            } else {
                System.err.println("\n✗✗✗ CONNEXION ÉCHOUÉE ✗✗✗\n");
            }
            
        } catch (Exception e) {
            System.err.println("\n✗✗✗ ERREUR LORS DU TEST ✗✗✗");
            e.printStackTrace();
        }
    }
}