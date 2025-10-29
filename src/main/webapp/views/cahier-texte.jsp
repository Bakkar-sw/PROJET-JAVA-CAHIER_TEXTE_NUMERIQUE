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
    <title>Saisie Cahier de Texte - TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
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
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 25px; }
        .form-group label { display: block; color: #2c3e50; font-weight: 600; margin-bottom: 8px; font-size: 14px; }
        .form-group label .required { color: #e74c3c; }
        .form-group input[type="time"],
        .form-group input[type="text"],
        .form-group textarea { width: 100%; padding: 12px; border: 2px solid #ecf0f1; border-radius: 8px; font-size: 14px; font-family: 'Segoe UI', sans-serif; }
        .form-group input:focus,
        .form-group textarea:focus { outline: none; border-color: #667eea; }
        .form-group textarea { min-height: 100px; resize: vertical; }
        .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .btn-save { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 15px 40px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; width: 100%; margin-top: 10px; }
        .btn-save:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4); }
        .success-message { background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        .error-message { background: #f8d7da; color: #721c24; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        .help-text { font-size: 12px; color: #7f8c8d; margin-top: 5px; }
        .badge-realise { background: #fff3cd; color: #856404; padding: 6px 12px; border-radius: 15px; font-size: 13px; font-weight: 600; }
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
        
        <% if (session.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                ✗ <%= session.getAttribute("errorMessage") %>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>
        
        <div class="header-card">
            <h1>📝 Saisie du Cahier de Texte</h1>
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
                    <div class="info-label">Horaire Prévu</div>
                    <div class="info-value"><%= cours.getHeureDebutPrevue() %> - <%= cours.getHeureFinPrevue() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Professeur</div>
                    <div class="info-value"><%= cours.getProfesseurComplet() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Statut</div>
                    <div class="info-value">
                        <% if (cours.isRealise()) { %>
                            <span class="badge-realise">Déjà saisi</span>
                        <% } else { %>
                            À saisir
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card">
            <h2 style="margin-bottom: 25px; color: #2c3e50;">Informations du Cours</h2>
            
            <form method="post" action="<%= request.getContextPath() %>/cahier-texte">
                <input type="hidden" name="coursId" value="<%= cours.getCoursId() %>">
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Heure de début réelle <span class="required">*</span></label>
                        <input type="time" name="heureDebut" 
                               value="<%= cours.getHeureDebutReelle() != null ? cours.getHeureDebutReelle().toString().substring(0,5) : "" %>" 
                               required>
                        <div class="help-text">Heure effective de début du cours</div>
                    </div>
                    
                    <div class="form-group">
                        <label>Heure de fin réelle <span class="required">*</span></label>
                        <input type="time" name="heureFin" 
                               value="<%= cours.getHeureFinReelle() != null ? cours.getHeureFinReelle().toString().substring(0,5) : "" %>" 
                               required>
                        <div class="help-text">Heure effective de fin du cours</div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label>Contenu du cours / Notions abordées <span class="required">*</span></label>
                    <textarea name="contenu" required placeholder="Décrivez les notions et chapitres abordés pendant ce cours..."><%= cours.getContenuCours() != null ? cours.getContenuCours() : "" %></textarea>
                    <div class="help-text">Exemple : Chapitre 3 - Les servlets Java : cycle de vie, méthodes doGet/doPost</div>
                </div>
                
                <div class="form-group">
                    <label>Objectifs pédagogiques atteints</label>
                    <textarea name="objectifs" placeholder="Quels sont les objectifs atteints pendant ce cours ?"><%= cours.getObjectifs() != null ? cours.getObjectifs() : "" %></textarea>
                    <div class="help-text">Exemple : Comprendre le fonctionnement des servlets, créer sa première servlet</div>
                </div>
                
                <div class="form-group">
                    <label>Travaux / Devoirs donnés</label>
                    <textarea name="travaux" placeholder="Décrivez les travaux ou devoirs donnés aux étudiants..."><%= cours.getTravauxDonnes() != null ? cours.getTravauxDonnes() : "" %></textarea>
                    <div class="help-text">Exemple : TP à rendre pour la semaine prochaine, exercices pages 45-50</div>
                </div>
                
                <div class="form-group">
                    <label>Ressources utilisées</label>
                    <input type="text" name="ressources" 
                           value="<%= cours.getRessources() != null ? cours.getRessources() : "" %>"
                           placeholder="Supports de cours, documents, liens...">
                    <div class="help-text">Exemple : Slides du cours, vidéo YouTube, documentation officielle</div>
                </div>
                
                <div class="form-group">
                    <label>Commentaires / Remarques</label>
                    <textarea name="commentaire" placeholder="Remarques générales sur le déroulement du cours..."><%= cours.getCommentaireProfesseur() != null ? cours.getCommentaireProfesseur() : "" %></textarea>
                    <div class="help-text">Observations particulières, difficultés rencontrées, points à revoir...</div>
                </div>
                
                <button type="submit" class="btn-save">💾 Enregistrer le Cahier de Texte</button>
            </form>
        </div>
    </div>
</body>
</html>