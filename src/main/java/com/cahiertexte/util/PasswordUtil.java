package com.cahiertexte.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe utilitaire pour le hashage et la vérification des mots de passe
 * Utilise l'algorithme SHA-256
 * 
 * @author Projet TDSI
 * @version 1.0
 */
public class PasswordUtil {
    
    /**
     * Hasher un mot de passe avec SHA-256
     * 
     * @param password Le mot de passe en clair
     * @return Le hash SHA-256 du mot de passe
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            
            // Convertir les bytes en hexadécimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
    
    /**
     * Vérifier si un mot de passe correspond à son hash
     * 
     * @param password Le mot de passe en clair
     * @param hashedPassword Le hash stocké en base
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String hashToCheck = hashPassword(password);
        return hashToCheck.equals(hashedPassword);
    }
    
    /**
     * Méthode main pour tester le hashage
     */
    public static void main(String[] args) {
        System.out.println("=== TEST DE HASHAGE DE MOTS DE PASSE ===\n");
        
        String password = "password123";
        String hashed = hashPassword(password);
        
        System.out.println("Mot de passe en clair : " + password);
        System.out.println("Hash SHA-256 : " + hashed);
        System.out.println();
        
        // Test de vérification
        System.out.println("Test de vérification :");
        System.out.println("Vérification avec bon mot de passe : " + verifyPassword("password123", hashed));
        System.out.println("Vérification avec mauvais mot de passe : " + verifyPassword("wrongpassword", hashed));
        
        System.out.println("\n=== HASH POUR LES UTILISATEURS DE TEST ===");
        System.out.println("Le hash utilisé dans la base de données pour 'password123' est :");
        System.out.println(hashed);
    }
}