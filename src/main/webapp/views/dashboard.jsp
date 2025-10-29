<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%
    // Vérifier si l'utilisateur est connecté
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
    <title>Dashboard - Cahier de Texte TDSI</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .navbar-brand {
            font-size: 20px;
            font-weight: bold;
        }
        
        .navbar-user {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .user-info {
            text-align: right;
        }
        
        .user-name {
            font-weight: 600;
        }
        
        .user-role {
            font-size: 12px;
            opacity: 0.9;
        }
        
        .btn-logout {
            background: rgba(255,255,255,0.2);
            color: white;
            border: none;
            padding: 8px 20px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-logout:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }
        
        .welcome-card {
            background: white;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
            margin-bottom: 30px;
        }
        
        .welcome-card h1 {
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .welcome-card p {
            color: #7f8c8d;
            font-size: 16px;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .stat-card h3 {
            color: #7f8c8d;
            font-size: 14px;
            margin-bottom: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .stat-number {
            font-size: 36px;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 5px;
        }
        
        .stat-label {
            color: #95a5a6;
            font-size: 14px;
        }
        
        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 4px solid #28a745;
        }
        
        .badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .badge-formation { background: #e74c3c; color: white; }
        .badge-classe { background: #3498db; color: white; }
        .badge-prof { background: #9b59b6; color: white; }
        .badge-etud { background: #2ecc71; color: white; }
    </style>
</head>
<body>
    <!-- Navbar -->
    <div class="navbar">
        <div class="navbar-brand">
            🎓 Cahier de Texte - TDSI
        </div>
        <div class="navbar-user">
            <div class="user-info">
                <div class="user-name"><%= user.getNomComplet() %></div>
                <div class="user-role">
                    <% 
                    String badgeClass = "";
                    String roleText = "";
                    switch(user.getRole()) {
                        case "RESPONSABLE_FORMATION":
                            badgeClass = "badge-formation";
                            roleText = "Responsable de Formation";
                            break;
                        case "RESPONSABLE_CLASSE":
                            badgeClass = "badge-classe";
                            roleText = "Responsable de Classe " + user.getClasse();
                            break;
                        case "PROFESSEUR":
                            badgeClass = "badge-prof";
                            roleText = "Professeur";
                            break;
                        case "ETUDIANT":
                            badgeClass = "badge-etud";
                            roleText = "Étudiant " + user.getClasse();
                            break;
                    }
                    %>
                    <span class="badge <%= badgeClass %>"><%= roleText %></span>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>
    
    <!-- Contenu principal -->
    <div class="container">
        <!-- Message de succès si présent -->
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="success-message">
                ✓ <%= session.getAttribute("successMessage") %>
            </div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        
        <!-- Carte de bienvenue -->
        <div class="welcome-card">
            <h1>Bienvenue, <%= user.getPrenom() %> !</h1>
            <p>Votre espace personnel pour le suivi pédagogique</p>
        </div>
        
        <!-- Statistiques selon le rôle -->
        <div class="stats-grid">
            <% if (request.getAttribute("statsGlobales") != null) { 
                java.util.Map<String, Object> stats = (java.util.Map<String, Object>) request.getAttribute("statsGlobales");
            %>
                <div class="stat-card">
                    <h3>Total Cours</h3>
                    <div class="stat-number"><%= stats.get("nbTotal") %></div>
                    <div class="stat-label">Planifiés et réalisés</div>
                </div>
                <div class="stat-card">
                    <h3>Cours Réalisés</h3>
                    <div class="stat-number"><%= stats.get("nbRealises") %></div>
                    <div class="stat-label">Déjà effectués</div>
                </div>
                <div class="stat-card">
                    <h3>Cours Validés</h3>
                    <div class="stat-number"><%= stats.get("nbValides") %></div>
                    <div class="stat-label">Confirmés par professeur</div>
                </div>
                <div class="stat-card">
                    <h3>En Attente</h3>
                    <div class="stat-number"><%= stats.get("nbEnAttenteValidation") %></div>
                    <div class="stat-label">À valider</div>
                </div>
            <% } %>
            
            <% if (request.getAttribute("statsPresence") != null) { 
                java.util.Map<String, Object> statsP = (java.util.Map<String, Object>) request.getAttribute("statsPresence");
            %>
                <div class="stat-card">
                    <h3>Taux de Présence</h3>
                    <div class="stat-number"><%= statsP.get("tauxPresence") %>%</div>
                    <div class="stat-label">Moyenne de la classe</div>
                </div>
            <% } %>
        </div>
        
        <!-- Section en construction -->
        <div class="welcome-card">
            <h2>🚧 Tableau de bord en construction</h2>
            <p style="margin-top: 15px;">
                Les fonctionnalités complètes seront bientôt disponibles pour votre rôle.
            </p>
            <p style="margin-top: 10px; color: #27ae60; font-weight: 600;">
                ✓ Connexion fonctionnelle<br>
                ✓ Gestion des sessions<br>
                ✓ Protection des pages
            </p>
        </div>
    </div>
</body>
</html>