<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Cours" %>
<%@ page import="java.util.List" %>
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
    <title>Dashboard Professeur - Cahier de Texte TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .user-info { text-align: right; }
        .user-name { font-weight: 600; }
        .user-role { font-size: 12px; opacity: 0.9; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn-logout:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; margin-bottom: 30px; }
        .welcome-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .welcome-card p { color: #7f8c8d; font-size: 16px; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .stat-card h3 { color: #7f8c8d; font-size: 14px; margin-bottom: 10px; text-transform: uppercase; }
        .stat-number { font-size: 36px; font-weight: bold; color: #9b59b6; margin-bottom: 5px; }
        .stat-label { color: #95a5a6; font-size: 14px; }
        .success-message { background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; border-left: 4px solid #28a745; }
        .badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: #9b59b6; color: white; }
        .card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-size: 13px; color: #7f8c8d; border-bottom: 2px solid #ecf0f1; }
        td { padding: 12px; border-bottom: 1px solid #ecf0f1; }
        tr:hover { background: #f8f9fa; }
        .badge-status { padding: 4px 10px; border-radius: 15px; font-size: 11px; font-weight: 600; }
        .badge-planifie { background: #e3f2fd; color: #1976d2; }
        .badge-realise { background: #fff3e0; color: #f57c00; }
        .badge-valide { background: #e8f5e9; color: #388e3c; }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div class="user-info">
                <div class="user-name"><%= user.getNomComplet() %></div>
                <div class="user-role"><span class="badge">Professeur</span></div>
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
            <h1>Bienvenue, Professeur <%= user.getPrenom() %> !</h1>
            <p>Gestion de vos cours et validation des cahiers de texte</p>
        </div>
        
        <div class="stats-grid">
            <% 
			@SuppressWarnings("unchecked")
			List<Cours> mesCours = (List<Cours>) request.getAttribute("mesCours");
			int total = mesCours != null ? mesCours.size() : 0;
			int nbAValider = request.getAttribute("nbCoursAValider") != null ? (Integer) request.getAttribute("nbCoursAValider") : 0;
			%>
            <div class="stat-card">
                <h3>Mes Cours</h3>
                <div class="stat-number"><%= total %></div>
                <div class="stat-label">Total cours</div>
            </div>
            <div class="stat-card">
                <h3>À Valider</h3>
                <div class="stat-number"><%= nbAValider %></div>
                <div class="stat-label">Cours en attente</div>
            </div>
        </div>
        <a href="<%= request.getContextPath() %>/rapports" 
		   style="background: #3498db; color: white; padding: 12px 30px; border-radius: 10px; text-decoration: none; display: inline-block; margin-bottom: 20px; font-weight: 600;">
		    📊 Voir les Rapports Détaillés
		</a>
        <div class="card">
            <h2>⚠️ Cours à Valider (<%= nbAValider %>)</h2>
            <% 
            if (mesCours != null && !mesCours.isEmpty()) { 
                boolean hasCoursAValider = false;
            %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Matière</th>
                            <th>Classe</th>
                            <th>Horaire</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Cours c : mesCours) { 
                            if (c.isRealise() && !c.isEstValide()) {
                                hasCoursAValider = true;
                        %>
                        <tr style="background: #fff3cd;">
                            <td><%= c.getDateCours() %></td>
                            <td><%= c.getNomMatiere() %></td>
                            <td><%= c.getClasse() %></td>
                            <td><%= c.getHeureDebutReelle() %> - <%= c.getHeureFinReelle() %></td>
                            <td><span style="background: #ffc107; color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px;">À valider</span></td>
                            <td>
                                <a href="<%= request.getContextPath() %>/validation-cours?coursId=<%= c.getCoursId() %>" 
                                   style="background: #2ecc71; color: white; padding: 6px 12px; border-radius: 5px; text-decoration: none; font-size: 13px;">
                                    ✓ Valider
                                </a>
                            </td>
                        </tr>
                        <% 
                            } 
                        } 
                        if (!hasCoursAValider) {
                        %>
                        <tr>
                            <td colspan="6" style="text-align: center; color: #27ae60; padding: 20px;">
                                ✓ Aucun cours en attente de validation
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p style="text-align: center; color: #7f8c8d; padding: 20px;">Aucun cours trouvé</p>
            <% } %>
        </div>
          
        <div class="card">
            <h2>📚 Tous Mes Cours</h2>
            <% if (mesCours != null && !mesCours.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Matière</th>
                            <th>Classe</th>
                            <th>Horaire</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Cours c : mesCours) { 
                            String badgeClass = c.isEstValide() ? "badge-valide" : (c.isRealise() ? "badge-realise" : "badge-planifie");
                            String statutText = c.isEstValide() ? "Validé" : (c.isRealise() ? "À valider" : "Planifié");
                        %>
                        <tr>
                            <td><%= c.getDateCours() %></td>
                            <td><%= c.getNomMatiere() %></td>
                            <td><%= c.getClasse() %></td>
                            <td><%= c.getHeureDebutPrevue() %> - <%= c.getHeureFinPrevue() %></td>
                            <td><span class="badge-status <%= badgeClass %>"><%= statutText %></span></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
</body>
</html>