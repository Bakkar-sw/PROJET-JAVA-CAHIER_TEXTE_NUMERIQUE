<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rapports et Statistiques - TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .btn-back { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; text-decoration: none; display: inline-block; margin-bottom: 20px; }
        .header-card { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; margin-bottom: 30px; }
        .header-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .stat-card h3 { color: #7f8c8d; font-size: 13px; margin-bottom: 10px; text-transform: uppercase; }
        .stat-number { font-size: 36px; font-weight: bold; color: #667eea; margin-bottom: 5px; }
        .stat-label { color: #95a5a6; font-size: 14px; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-size: 13px; color: #7f8c8d; }
        td { padding: 12px; border-bottom: 1px solid #ecf0f1; }
        .progress-bar { height: 20px; background: #ecf0f1; border-radius: 10px; overflow: hidden; margin-top: 5px; }
        .progress-fill { height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); transition: width 0.3s; }
        .badge-critique { background: #e74c3c; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; }
        .badge-ok { background: #2ecc71; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; }
        .classe-section { background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 20px; }
        .classe-title { font-size: 18px; font-weight: bold; color: #2c3e50; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div><%= user.getNomComplet() %></div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>
    
    <div class="container">
        <a href="<%= request.getContextPath() %>/dashboard" class="btn-back">← Retour au Dashboard</a>
        
        <div class="header-card">
            <h1>📊 Rapports et Statistiques</h1>
            <p style="color: #7f8c8d;">Vue d'ensemble et analyses détaillées</p>
        </div>
        
        <% if (user.isResponsableFormation()) { 
            List<Map<String, Object>> statsParClasse = (List<Map<String, Object>>) request.getAttribute("statsParClasse");
            if (statsParClasse != null) {
                for (Map<String, Object> classeData : statsParClasse) {
                    String classe = (String) classeData.get("classe");
                    Map<String, Object> statsCours = (Map<String, Object>) classeData.get("statsCours");
                    Map<String, Object> statsPresence = (Map<String, Object>) classeData.get("statsPresence");
        %>
        <div class="classe-section">
            <div class="classe-title">📚 Classe <%= classe %></div>
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>Cours Totaux</h3>
                    <div class="stat-number"><%= statsCours.get("nbTotal") %></div>
                    <div class="stat-label">Planifiés</div>
                </div>
                <div class="stat-card">
                    <h3>Cours Validés</h3>
                    <div class="stat-number"><%= statsCours.get("nbValides") %></div>
                    <div class="stat-label">Confirmés</div>
                </div>
                <div class="stat-card">
                    <h3>Taux Présence</h3>
                    <div class="stat-number"><%= statsPresence.get("tauxPresenceMoyen") %>%</div>
                    <div class="stat-label">Moyenne classe</div>
                </div>
                <div class="stat-card">
                    <h3>Étudiants Critiques</h3>
                    <div class="stat-number"><%= statsPresence.get("nbEtudiantsCritiques") %></div>
                    <div class="stat-label">Absences critiques</div>
                </div>
            </div>
        </div>
        <% 
                }
            }
            
            // Alertes matières
            List<Map<String, Object>> alertes = (List<Map<String, Object>>) request.getAttribute("alertesMatiere");
            if (alertes != null && !alertes.isEmpty()) {
        %>
        <div class="card">
            <h2>⚠️ Matières nécessitant une attention (< 12h restantes)</h2>
            <table>
                <thead>
                    <tr>
                        <th>Matière</th>
                        <th>Classe</th>
                        <th>Volume Total</th>
                        <th>Heures Restantes</th>
                        <th>Progression</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Map<String, Object> alerte : alertes) { 
                        com.cahiertexte.model.Matiere mat = (com.cahiertexte.model.Matiere) alerte.get("matiere");
                        double pourcent = (Double) alerte.get("pourcentageRealise");
                    %>
                    <tr>
                        <td><%= mat.getNomMatiere() %></td>
                        <td><%= mat.getClasse() %></td>
                        <td><%= alerte.get("volumeTotal") %>h</td>
                        <td style="color: #e74c3c; font-weight: bold;"><%= alerte.get("heuresRestantes") %>h</td>
                        <td>
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: <%= pourcent %>%;"></div>
                            </div>
                            <%= String.format("%.1f", pourcent) %>%
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% 
            }
        } else if (user.isEtudiant()) { 
            Map<String, Object> statsPerso = (Map<String, Object>) request.getAttribute("statsPersonnelles");
            if (statsPerso != null) {
        %>
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Cours Suivis</h3>
                <div class="stat-number"><%= statsPerso.get("nbTotal") %></div>
                <div class="stat-label">Total</div>
            </div>
            <div class="stat-card">
                <h3>Présences</h3>
                <div class="stat-number"><%= statsPerso.get("nbPresents") %></div>
                <div class="stat-label">Cours présent</div>
            </div>
            <div class="stat-card">
                <h3>Absences</h3>
                <div class="stat-number"><%= statsPerso.get("nbAbsents") %></div>
                <div class="stat-label">Total absences</div>
            </div>
            <div class="stat-card">
                <h3>Taux Présence</h3>
                <div class="stat-number"><%= statsPerso.get("tauxPresence") %>%</div>
                <div class="stat-label">Performance</div>
            </div>
        </div>
        <% 
            }
        }
        %>
        
        <div class="card">
            <h2>ℹ️ Informations</h2>
            <p style="color: #7f8c8d; line-height: 1.6;">
                Ces statistiques sont mises à jour en temps réel et reflètent l'état actuel du système. 
                Pour des analyses plus approfondies, contactez l'administration.
            </p>
        </div>
    </div>
</body>
</html>