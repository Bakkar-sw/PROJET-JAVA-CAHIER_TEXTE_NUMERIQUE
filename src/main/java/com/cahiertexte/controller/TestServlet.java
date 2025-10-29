package com.cahiertexte.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cahiertexte.dao.DatabaseConnection;

/**
 * Servlet de test pour vérifier la configuration complète
 * URL: http://localhost:8080/CahierTexteApp/test
 */
@WebServlet("/test")
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Test - Cahier de Texte TDSI</title>");
            out.println("<style>");
            out.println("* { margin: 0; padding: 0; box-sizing: border-box; }");
            out.println("body { font-family: 'Segoe UI', Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px; min-height: 100vh; }");
            out.println(".container { background: white; padding: 40px; border-radius: 20px; max-width: 1000px; margin: 0 auto; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }");
            out.println("h1 { color: #2c3e50; border-bottom: 4px solid #3498db; padding-bottom: 15px; margin-bottom: 30px; font-size: 32px; }");
            out.println("h2 { color: #34495e; margin: 30px 0 15px 0; font-size: 22px; }");
            out.println(".success { background: #d4edda; color: #155724; padding: 20px; border-radius: 10px; border-left: 5px solid #28a745; margin: 20px 0; font-size: 18px; font-weight: 500; }");
            out.println(".info-box { background: #e7f3ff; padding: 25px; border-radius: 10px; margin: 20px 0; border-left: 5px solid #2196F3; }");
            out.println(".info-box h3 { color: #1976D2; margin-bottom: 15px; }");
            out.println(".info-box p { margin: 8px 0; color: #424242; line-height: 1.6; }");
            out.println(".info-box strong { color: #1565C0; }");
            out.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 10px; overflow: hidden; }");
            out.println("th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px; text-align: left; font-weight: 600; font-size: 14px; text-transform: uppercase; }");
            out.println("td { padding: 15px; border-bottom: 1px solid #ecf0f1; color: #2c3e50; }");
            out.println("tr:last-child td { border-bottom: none; }");
            out.println("tr:hover { background: #f8f9fa; }");
            out.println(".badge { display: inline-block; padding: 6px 12px; border-radius: 20px; font-size: 11px; font-weight: bold; text-transform: uppercase; }");
            out.println(".badge-formation { background: #e74c3c; color: white; }");
            out.println(".badge-classe { background: #3498db; color: white; }");
            out.println(".badge-prof { background: #9b59b6; color: white; }");
            out.println(".badge-etud { background: #2ecc71; color: white; }");
            out.println(".stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 30px 0; }");
            out.println(".stat-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 15px; text-align: center; box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4); }");
            out.println(".stat-number { font-size: 42px; font-weight: bold; margin: 15px 0; }");
            out.println(".stat-label { font-size: 14px; opacity: 0.95; text-transform: uppercase; letter-spacing: 1px; }");
            out.println(".footer { text-align: center; margin-top: 40px; padding-top: 30px; border-top: 2px solid #ecf0f1; color: #7f8c8d; }");
            out.println(".footer strong { color: #2c3e50; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>🎓 Système de Cahier de Texte Numérique - TDSI</h1>");
            
            // Test de connexion
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            
            if (dbConn.testConnection()) {
                out.println("<div class='success'>✅ Environnement J2EE configuré avec succès !</div>");
                
                // Informations de connexion
                out.println("<div class='info-box'>");
                out.println("<h3>📊 Informations du Serveur</h3>");
                out.println("<p><strong>Instance SQL Server :</strong> PHOENIX\\SQLSERVER</p>");
                out.println("<p><strong>Base de données :</strong> cahier_texte_db</p>");
                out.println("<p><strong>Servlet :</strong> " + this.getClass().getSimpleName() + "</p>");
                out.println("<p><strong>Contexte :</strong> " + request.getContextPath() + "</p>");
                out.println("<p><strong>URL :</strong> " + request.getRequestURL() + "</p>");
                out.println("<p><strong>Serveur Web :</strong> " + getServletContext().getServerInfo() + "</p>");
                out.println("</div>");
                
                Connection conn = dbConn.getConnection();
                Statement stmt = conn.createStatement();
                
                // Statistiques
                out.println("<h2>📈 Statistiques de la Base de Données</h2>");
                out.println("<div class='stats'>");
                
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM users");
                if (rs.next()) {
                    out.println("<div class='stat-card'>");
                    out.println("<div class='stat-label'>Utilisateurs</div>");
                    out.println("<div class='stat-number'>" + rs.getInt("total") + "</div>");
                    out.println("</div>");
                }
                rs.close();
                
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM matieres");
                if (rs.next()) {
                    out.println("<div class='stat-card'>");
                    out.println("<div class='stat-label'>Matières</div>");
                    out.println("<div class='stat-number'>" + rs.getInt("total") + "</div>");
                    out.println("</div>");
                }
                rs.close();
                
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM cours");
                if (rs.next()) {
                    out.println("<div class='stat-card'>");
                    out.println("<div class='stat-label'>Cours</div>");
                    out.println("<div class='stat-number'>" + rs.getInt("total") + "</div>");
                    out.println("</div>");
                }
                rs.close();
                
                out.println("</div>");
                
                // Liste des utilisateurs
                out.println("<h2>👥 Liste des Utilisateurs</h2>");
                out.println("<table>");
                out.println("<thead><tr>");
                out.println("<th>ID</th><th>Nom</th><th>Prénom</th><th>Email</th><th>Rôle</th><th>Classe</th>");
                out.println("</tr></thead>");
                out.println("<tbody>");
                
                rs = stmt.executeQuery(
                    "SELECT user_id, nom, prenom, email, role, classe " +
                    "FROM users ORDER BY role, nom"
                );
                
                while (rs.next()) {
                    String role = rs.getString("role");
                    String badgeClass = "";
                    
                    if (role.equals("RESPONSABLE_FORMATION")) badgeClass = "badge-formation";
                    else if (role.equals("RESPONSABLE_CLASSE")) badgeClass = "badge-classe";
                    else if (role.equals("PROFESSEUR")) badgeClass = "badge-prof";
                    else if (role.equals("ETUDIANT")) badgeClass = "badge-etud";
                    
                    out.println("<tr>");
                    out.println("<td>" + rs.getInt("user_id") + "</td>");
                    out.println("<td>" + rs.getString("nom") + "</td>");
                    out.println("<td>" + rs.getString("prenom") + "</td>");
                    out.println("<td>" + rs.getString("email") + "</td>");
                    out.println("<td><span class='badge " + badgeClass + "'>" + role.replace("_", " ") + "</span></td>");
                    out.println("<td>" + (rs.getString("classe") != null ? rs.getString("classe") : "-") + "</td>");
                    out.println("</tr>");
                }
                
                out.println("</tbody></table>");
                
                rs.close();
                stmt.close();
                
                // Prochaines étapes
                out.println("<div class='info-box'>");
                out.println("<h3>✨ Prochaines Étapes</h3>");
                out.println("<p>✅ <strong>Configuration Tomcat 9 :</strong> OK</p>");
                out.println("<p>✅ <strong>Connexion SQL Server :</strong> OK</p>");
                out.println("<p>✅ <strong>Servlet fonctionnelle :</strong> OK</p>");
                out.println("<p>✅ <strong>Base de données :</strong> OK</p>");
                out.println("<p>⏳ <strong>Classes entités (Model) :</strong> À venir...</p>");
                out.println("<p>⏳ <strong>Classes DAO :</strong> À venir...</p>");
                out.println("<p>⏳ <strong>Services métier :</strong> À venir...</p>");
                out.println("</div>");
                
            } else {
                out.println("<div style='background: #f8d7da; color: #721c24; padding: 20px; border-radius: 10px; border-left: 5px solid #dc3545;'>");
                out.println("❌ Erreur de connexion à la base de données");
                out.println("</div>");
            }
            
            out.println("<div class='footer'>");
            out.println("<p>Projet : <strong>Système de Numérisation des Cahiers de Texte</strong></p>");
            out.println("<p>Institut TDSI - Université Cheikh Anta Diop de Dakar</p>");
            out.println("<p style='margin-top: 10px; font-size: 12px;'>Année Académique 2024-2025</p>");
            out.println("</div>");
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            out.println("<h2>Erreur</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}