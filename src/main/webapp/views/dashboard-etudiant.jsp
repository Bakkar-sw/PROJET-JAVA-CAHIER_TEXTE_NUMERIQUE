<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Presence" %>
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
    <title>Dashboard Étudiant - Cahier de Texte TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .user-info { text-align: right; }
        .user-name { font-weight: 600; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; display: inline-block; }
        .btn-logout:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; margin-bottom: 30px; }
        .welcome-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .stat-card h3 { color: #7f8c8d; font-size: 13px; margin-bottom: 10px; text-transform: uppercase; }
        .stat-number { font-size: 36px; font-weight: bold; color: #2ecc71; margin-bottom: 5px; }
        .stat-label { color: #95a5a6; font-size: 14px; }
        .success-message { background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        .badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: #2ecc71; color: white; }
        .card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-size: 13px; color: #7f8c8d; }
        td { padding: 12px; border-bottom: 1px solid #ecf0f1; }
        .alert-warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; border-radius: 8px; margin-bottom: 20px; color: #856404; }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div class="user-info">
                <div class="user-name"><%= user.getNomComplet() %></div>
                <div><span class="badge">Étudiant <%= user.getClasse() %></span></div>
            </div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>
    
    <div class="container">
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="success-message">✓ <%= session.getAttribute("successMessage") %></div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        
        <div class="welcome-card">
            <h1>Bienvenue, <%= user.getPrenom() %> !</h1>
            <p>Classe <%= user.getClasse() %> - Suivi de vos présences</p>
        </div>
        
        <div class="stats-grid">
            <% 
            Map<String, Object> statsP = (Map<String, Object>) request.getAttribute("statsPresence");
            if (statsP != null) {
            %>
                <div class="stat-card">
                    <h3>Cours Suivis</h3>
                    <div class="stat-number"><%= statsP.get("nbTotal") %></div>
                    <div class="stat-label">Total</div>
                </div>
                <div class="stat-card">
                    <h3>Présences</h3>
                    <div class="stat-number"><%= statsP.get("nbPresents") %></div>
                    <div class="stat-label">Cours présent</div>
                </div>
                <div class="stat-card">
                    <h3>Absences</h3>
                    <div class="stat-number"><%= statsP.get("nbAbsents") %></div>
                    <div class="stat-label">Total absences</div>
                </div>
                <div class="stat-card">
                    <h3>Taux Présence</h3>
                    <div class="stat-number"><%= statsP.get("tauxPresence") %>%</div>
                    <div class="stat-label">Performance</div>
                </div>
            <% } %>
        </div>
        
        <% 
        List<Presence> mesAbsences = (List<Presence>) request.getAttribute("mesAbsences");
        if (mesAbsences != null && !mesAbsences.isEmpty()) {
        %>
        <div class="alert-warning">
            ⚠️ Vous avez <%= mesAbsences.size() %> absence(s) enregistrée(s)
        </div>
        
        <div class="card">
            <h2>📝 Mes Absences</h2>
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Matière</th>
                        <th>Commentaire</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Presence abs : mesAbsences) { %>
                    <tr>
                        <td><%= abs.getDateCours() %></td>
                        <td><%= abs.getNomMatiere() %></td>
                        <td><%= abs.getCommentaire() != null ? abs.getCommentaire() : "-" %></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/soumettre-justificatif?presenceId=<%= abs.getPresenceId() %>" 
                               style="background: #3498db; color: white; padding: 6px 12px; border-radius: 5px; text-decoration: none; font-size: 13px;">
                                📄 Justifier
                            </a>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } else { %>
        <div class="card">
            <h2>✅ Aucune Absence</h2>
            <p style="text-align: center; color: #27ae60; padding: 20px;">
                Excellent ! Vous êtes assidu à tous vos cours.
            </p>
        </div>
        <% } %>
    </div>
</body>
</html>