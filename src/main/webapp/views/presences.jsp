<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Cours" %>
<%@ page import="com.cahiertexte.model.Presence" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) request.getAttribute("user");
    Cours cours = (Cours) request.getAttribute("cours");
    List<Presence> presences = (List<Presence>) request.getAttribute("presences");
    
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
    <title>Gestion des Présences - Cahier de Texte TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .header-card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .header-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .course-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 20px; }
        .info-item { background: #f8f9fa; padding: 12px; border-radius: 8px; }
        .info-label { font-size: 12px; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; color: #2c3e50; font-weight: 600; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .success-message { background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th { background: #f8f9fa; padding: 15px; text-align: left; font-size: 13px; color: #7f8c8d; border-bottom: 2px solid #ecf0f1; }
        td { padding: 15px; border-bottom: 1px solid #ecf0f1; }
        tr:hover { background: #f8f9fa; }
        .radio-group { display: flex; gap: 15px; }
        .radio-option { display: flex; align-items: center; gap: 5px; }
        .radio-option input[type="radio"] { cursor: pointer; }
        .radio-option label { cursor: pointer; font-size: 14px; }
        .comment-input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 5px; font-size: 13px; }
        .btn-save { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 15px 40px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; margin-top: 20px; }
        .btn-save:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4); }
        .btn-back { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; text-decoration: none; display: inline-block; margin-bottom: 20px; }
        .btn-back:hover { background: #7f8c8d; }
        .badge-present { background: #d4edda; color: #155724; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
        .badge-absent { background: #f8d7da; color: #721c24; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
        .badge-retard { background: #fff3cd; color: #856404; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
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
        
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="success-message">
                ✓ <%= session.getAttribute("successMessage") %>
            </div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        
        <div class="header-card">
            <h1>📋 Gestion des Présences</h1>
            <div class="course-info">
                <div class="info-item">
                    <div class="info-label">Matière</div>
                    <div class="info-value"><%= cours.getNomMatiere() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Classe</div>
                    <div class="info-value"><%= cours.getClasse() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Date</div>
                    <div class="info-value"><%= cours.getDateCours() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Horaire</div>
                    <div class="info-value"><%= cours.getHeureDebutPrevue() %> - <%= cours.getHeureFinPrevue() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Professeur</div>
                    <div class="info-value"><%= cours.getProfesseurComplet() %></div>
                </div>
            </div>
        </div>
        
        <div class="card">
            <h2 style="margin-bottom: 20px;">Liste des Étudiants</h2>
            
            <form method="post" action="<%= request.getContextPath() %>/presences">
                <input type="hidden" name="coursId" value="<%= cours.getCoursId() %>">
                
                <table>
                    <thead>
                        <tr>
                            <th style="width: 50px;">#</th>
                            <th>Nom</th>
                            <th>Prénom</th>
                            <th>Statut</th>
                            <th>Commentaire</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int index = 1;
                        for (Presence p : presences) { 
                        %>
                        <tr>
                            <td><%= index++ %></td>
                            <td><%= p.getNomEtudiant() %></td>
                            <td><%= p.getPrenomEtudiant() %></td>
                            <td>
                                <div class="radio-group">
                                    <div class="radio-option">
                                        <input type="radio" 
                                               id="present_<%= p.getPresenceId() %>" 
                                               name="statut_<%= p.getPresenceId() %>" 
                                               value="PRESENT"
                                               <%= "PRESENT".equals(p.getStatut()) ? "checked" : "" %>>
                                        <label for="present_<%= p.getPresenceId() %>">Présent</label>
                                    </div>
                                    <div class="radio-option">
                                        <input type="radio" 
                                               id="absent_<%= p.getPresenceId() %>" 
                                               name="statut_<%= p.getPresenceId() %>" 
                                               value="ABSENT"
                                               <%= "ABSENT".equals(p.getStatut()) ? "checked" : "" %>>
                                        <label for="absent_<%= p.getPresenceId() %>">Absent</label>
                                    </div>
                                    <div class="radio-option">
                                        <input type="radio" 
                                               id="retard_acc_<%= p.getPresenceId() %>" 
                                               name="statut_<%= p.getPresenceId() %>" 
                                               value="RETARD_ACCEPTE"
                                               <%= "RETARD_ACCEPTE".equals(p.getStatut()) ? "checked" : "" %>>
                                        <label for="retard_acc_<%= p.getPresenceId() %>">Retard Accepté</label>
                                    </div>
                                    <div class="radio-option">
                                        <input type="radio" 
                                               id="retard_ref_<%= p.getPresenceId() %>" 
                                               name="statut_<%= p.getPresenceId() %>" 
                                               value="RETARD_REFUSE"
                                               <%= "RETARD_REFUSE".equals(p.getStatut()) ? "checked" : "" %>>
                                        <label for="retard_ref_<%= p.getPresenceId() %>">Retard Refusé</label>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <input type="text" 
                                       name="commentaire_<%= p.getPresenceId() %>" 
                                       class="comment-input" 
                                       placeholder="Commentaire (optionnel)"
                                       value="<%= p.getCommentaire() != null ? p.getCommentaire() : "" %>">
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                
                <button type="submit" class="btn-save">💾 Enregistrer les Présences</button>
            </form>
        </div>
    </div>
</body>
</html>