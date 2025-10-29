<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Cours" %>
<%
    User user = (User) request.getAttribute("user");
    Cours cours = (Cours) request.getAttribute("cours");
    
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
    <title>Validation Cours - TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
        .btn-back { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; text-decoration: none; display: inline-block; margin-bottom: 20px; }
        .btn-back:hover { background: #7f8c8d; }
        .header-card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .header-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .course-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 20px; }
        .info-item { background: #f8f9fa; padding: 12px; border-radius: 8px; }
        .info-label { font-size: 12px; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; color: #2c3e50; font-weight: 600; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px; }
        .content-section { margin-bottom: 25px; }
        .content-label { font-size: 14px; color: #7f8c8d; font-weight: 600; margin-bottom: 8px; text-transform: uppercase; }
        .content-value { font-size: 15px; color: #2c3e50; line-height: 1.6; background: #f8f9fa; padding: 15px; border-radius: 8px; white-space: pre-wrap; }
        .content-empty { font-style: italic; color: #95a5a6; }
        .validation-card { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); padding: 30px; border-radius: 15px; border: 2px solid #9b59b6; }
        .validation-card h2 { color: #9b59b6; }
        .btn-validate { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; border: none; padding: 15px 40px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; width: 100%; margin-top: 20px; }
        .btn-validate:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(46, 204, 113, 0.4); }
        .alert-info { background: #d1ecf1; color: #0c5460; padding: 15px; border-radius: 8px; border-left: 4px solid #17a2b8; margin-bottom: 20px; }
        .duration-badge { background: #9b59b6; color: white; padding: 8px 15px; border-radius: 20px; font-weight: 600; display: inline-block; }
    </style>
    <script>
        function confirmValidation() {
            return confirm('Êtes-vous sûr de vouloir valider ce cours ?\n\nUne fois validé, le contenu ne pourra plus être modifié.');
        }
    </script>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div>Prof. <%= user.getNomComplet() %></div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>
    
    <div class="container">
        <a href="<%= request.getContextPath() %>/dashboard-professeur" class="btn-back">← Retour au Dashboard</a>
        
        <div class="header-card">
            <h1>✓ Validation du Cours</h1>
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
                    <div class="info-label">Horaire Réel</div>
                    <div class="info-value">
                        <%= cours.getHeureDebutReelle() %> - <%= cours.getHeureFinReelle() %>
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-label">Durée Effective</div>
                    <div class="info-value">
                        <span class="duration-badge"><%= cours.getDureeEffective() %> heures</span>
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-label">Saisi par</div>
                    <div class="info-value">
                        <%= cours.getResponsableSaisieId() > 0 ? "Responsable classe" : "Non renseigné" %>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="alert-info">
            ℹ️ <strong>Vérifiez attentivement</strong> le contenu du cahier de texte avant de valider. Une fois validé, le cours ne pourra plus être modifié.
        </div>
        
        <div class="card">
            <h2>📚 Contenu Pédagogique</h2>
            
            <div class="content-section">
                <div class="content-label">🔹 Contenu / Notions abordées</div>
                <div class="content-value">
                    <%= cours.getContenuCours() != null && !cours.getContenuCours().isEmpty() ? cours.getContenuCours() : "<span class='content-empty'>Non renseigné</span>" %>
                </div>
            </div>
            
            <div class="content-section">
                <div class="content-label">🎯 Objectifs pédagogiques</div>
                <div class="content-value">
                    <%= cours.getObjectifs() != null && !cours.getObjectifs().isEmpty() ? cours.getObjectifs() : "<span class='content-empty'>Non renseigné</span>" %>
                </div>
            </div>
            
            <div class="content-section">
                <div class="content-label">📝 Travaux / Devoirs donnés</div>
                <div class="content-value">
                    <%= cours.getTravauxDonnes() != null && !cours.getTravauxDonnes().isEmpty() ? cours.getTravauxDonnes() : "<span class='content-empty'>Non renseigné</span>" %>
                </div>
            </div>
            
            <div class="content-section">
                <div class="content-label">📖 Ressources utilisées</div>
                <div class="content-value">
                    <%= cours.getRessources() != null && !cours.getRessources().isEmpty() ? cours.getRessources() : "<span class='content-empty'>Non renseigné</span>" %>
                </div>
            </div>
            
            <div class="content-section">
                <div class="content-label">💬 Commentaires / Remarques</div>
                <div class="content-value">
                    <%= cours.getCommentaireProfesseur() != null && !cours.getCommentaireProfesseur().isEmpty() ? cours.getCommentaireProfesseur() : "<span class='content-empty'>Non renseigné</span>" %>
                </div>
            </div>
        </div>
        
        <div class="validation-card">
            <h2>✅ Validation du Cours</h2>
            <p style="color: #7f8c8d; margin: 15px 0;">
                En validant ce cours, vous confirmez que le contenu saisi correspond bien à ce qui a été enseigné. 
                Le cours sera marqué comme validé et ne pourra plus être modifié.
            </p>
            
            <form method="post" action="<%= request.getContextPath() %>/validation-cours" onsubmit="return confirmValidation()">
                <input type="hidden" name="coursId" value="<%= cours.getCoursId() %>">
                <button type="submit" class="btn-validate">✓ Valider ce Cours</button>
            </form>
        </div>
    </div>
</body>
</html>