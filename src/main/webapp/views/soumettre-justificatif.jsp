<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Presence" %>
<%
    User user = (User) request.getAttribute("user");
    Presence presence = (Presence) request.getAttribute("presence");
    
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
    <title>Soumettre un Justificatif - TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .container { max-width: 800px; margin: 40px auto; padding: 0 20px; }
        .btn-back { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; text-decoration: none; display: inline-block; margin-bottom: 20px; }
        .btn-back:hover { background: #7f8c8d; }
        .header-card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .header-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .absence-info { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 20px; background: #ffe6e6; padding: 20px; border-radius: 10px; border-left: 4px solid #e74c3c; }
        .info-item { }
        .info-label { font-size: 12px; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; color: #2c3e50; font-weight: 600; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 25px; }
        .form-group label { display: block; color: #2c3e50; font-weight: 600; margin-bottom: 8px; font-size: 14px; }
        .form-group label .required { color: #e74c3c; }
        .form-group select,
        .form-group textarea { width: 100%; padding: 12px; border: 2px solid #ecf0f1; border-radius: 8px; font-size: 14px; font-family: 'Segoe UI', sans-serif; }
        .form-group select:focus,
        .form-group textarea:focus { outline: none; border-color: #2ecc71; }
        .form-group textarea { min-height: 120px; resize: vertical; }
        .help-text { font-size: 12px; color: #7f8c8d; margin-top: 5px; }
        .btn-submit { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; border: none; padding: 15px 40px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; width: 100%; margin-top: 10px; }
        .btn-submit:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(46, 204, 113, 0.4); }
        .error-message { background: #f8d7da; color: #721c24; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        .alert-info { background: #d1ecf1; color: #0c5460; padding: 15px; border-radius: 8px; border-left: 4px solid #17a2b8; margin-bottom: 20px; }
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
        <a href="<%= request.getContextPath() %>/dashboard-etudiant" class="btn-back">← Retour au Dashboard</a>
        
        <% if (session.getAttribute("errorMessage") != null) { %>
            <div class="error-message">
                ✗ <%= session.getAttribute("errorMessage") %>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>
        
        <div class="header-card">
            <h1>📄 Soumettre un Justificatif d'Absence</h1>
            
            <div class="absence-info">
                <div class="info-item">
                    <div class="info-label">Date du Cours</div>
                    <div class="info-value"><%= presence.getDateCours() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Matière</div>
                    <div class="info-value"><%= presence.getNomMatiere() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Classe</div>
                    <div class="info-value"><%= presence.getClasseEtudiant() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Statut</div>
                    <div class="info-value" style="color: #e74c3c;">Absent(e)</div>
                </div>
            </div>
        </div>
        
        <div class="alert-info">
            ℹ️ <strong>Information importante :</strong> Votre justificatif sera examiné par le responsable de formation. 
            Assurez-vous de fournir un motif clair et détaillé.
        </div>
        
        <div class="card">
            <h2 style="margin-bottom: 25px; color: #2c3e50;">Informations du Justificatif</h2>
            
            <form method="post" action="<%= request.getContextPath() %>/soumettre-justificatif">
                <input type="hidden" name="presenceId" value="<%= presence.getPresenceId() %>">
                
                <div class="form-group">
                    <label>Type de justificatif <span class="required">*</span></label>
                    <select name="typeJustificatif" required>
                        <option value="">-- Sélectionnez un type --</option>
                        <option value="MEDICAL">Médical (maladie, consultation médicale)</option>
                        <option value="ADMINISTRATIF">Administratif (convocation, démarches)</option>
                        <option value="FAMILIAL">Familial (événement familial)</option>
                        <option value="AUTRE">Autre</option>
                    </select>
                    <div class="help-text">Choisissez le type qui correspond à votre situation</div>
                </div>
                
                <div class="form-group">
                    <label>Motif de l'absence <span class="required">*</span></label>
                    <textarea name="motif" required placeholder="Expliquez en détail la raison de votre absence..."></textarea>
                    <div class="help-text">Exemple : Consultation médicale chez le médecin pour une infection respiratoire</div>
                </div>
                
                <div style="background: #fff3cd; padding: 15px; border-radius: 8px; margin-bottom: 20px; color: #856404;">
                    ⚠️ <strong>Note :</strong> Si vous disposez d'un document justificatif (certificat médical, convocation, etc.), 
                    veuillez le conserver. Il pourra vous être demandé ultérieurement.
                </div>
                
                <button type="submit" class="btn-submit">📤 Soumettre le Justificatif</button>
            </form>
        </div>
    </div>
</body>
</html>