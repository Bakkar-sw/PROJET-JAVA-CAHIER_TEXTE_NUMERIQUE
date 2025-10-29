<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Justificatif" %>
<%
    User user = (User) request.getAttribute("user");
    Justificatif justif = (Justificatif) request.getAttribute("justificatif");
    
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
    <title>Validation Justificatif - TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; }
        .navbar { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
        .btn-back { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; text-decoration: none; display: inline-block; margin-bottom: 20px; }
        .btn-back:hover { background: #7f8c8d; }
        .header-card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 30px; }
        .header-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-top: 20px; }
        .info-item { background: #f8f9fa; padding: 12px; border-radius: 8px; }
        .info-label { font-size: 12px; color: #7f8c8d; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; color: #2c3e50; font-weight: 600; }
        .card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px; }
        .content-section { margin-bottom: 25px; }
        .content-label { font-size: 14px; color: #7f8c8d; font-weight: 600; margin-bottom: 8px; text-transform: uppercase; }
        .content-value { font-size: 15px; color: #2c3e50; line-height: 1.6; background: #f8f9fa; padding: 15px; border-radius: 8px; white-space: pre-wrap; }
        .validation-card { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); padding: 30px; border-radius: 15px; border: 2px solid #e74c3c; }
        .validation-card h2 { color: #e74c3c; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; color: #2c3e50; font-weight: 600; margin-bottom: 8px; font-size: 14px; }
        .form-group textarea { width: 100%; padding: 12px; border: 2px solid #ecf0f1; border-radius: 8px; font-size: 14px; font-family: 'Segoe UI', sans-serif; min-height: 80px; resize: vertical; }
        .form-group textarea:focus { outline: none; border-color: #e74c3c; }
        .action-buttons { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 20px; }
        .btn-accept { background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); color: white; border: none; padding: 15px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; }
        .btn-accept:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(46, 204, 113, 0.4); }
        .btn-refuse { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; border: none; padding: 15px; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; }
        .btn-refuse:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(231, 76, 60, 0.4); }
        .badge-type { padding: 8px 15px; border-radius: 20px; font-weight: 600; display: inline-block; }
        .badge-medical { background: #e8f5e9; color: #388e3c; }
        .badge-admin { background: #e3f2fd; color: #1976d2; }
        .badge-familial { background: #fff3e0; color: #f57c00; }
        .badge-autre { background: #f3e5f5; color: #7b1fa2; }
    </style>
    <script>
        function validateForm(action) {
            if (action === 'accepter') {
                return confirm('Êtes-vous sûr de vouloir ACCEPTER ce justificatif ?');
            } else {
                return confirm('Êtes-vous sûr de vouloir REFUSER ce justificatif ?');
            }
        }
    </script>
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
        <a href="<%= request.getContextPath() %>/dashboard-formation" class="btn-back">← Retour au Dashboard</a>
        
        <div class="header-card">
            <h1>📄 Examen du Justificatif d'Absence</h1>
            
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Étudiant</div>
                    <div class="info-value"><%= justif.getEtudiantComplet() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Classe</div>
                    <div class="info-value"><%= justif.getClasseEtudiant() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Date du Cours</div>
                    <div class="info-value"><%= justif.getDateCours() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Matière</div>
                    <div class="info-value"><%= justif.getNomMatiere() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Date Soumission</div>
                    <div class="info-value"><%= justif.getDateSoumission().toString().substring(0,16) %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Type</div>
                    <div class="info-value">
                        <% 
                        String badgeClass = "";
                        switch(justif.getTypeJustificatif()) {
                            case "MEDICAL": badgeClass = "badge-medical"; break;
                            case "ADMINISTRATIF": badgeClass = "badge-admin"; break;
                            case "FAMILIAL": badgeClass = "badge-familial"; break;
                            default: badgeClass = "badge-autre";
                        }
                        %>
                        <span class="badge-type <%= badgeClass %>"><%= justif.getTypeLibelle() %></span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card">
            <h2>📝 Détails du Justificatif</h2>
            
            <div class="content-section">
                <div class="content-label">Motif de l'absence</div>
                <div class="content-value"><%= justif.getMotif() %></div>
            </div>
        </div>
        
        <div class="validation-card">
            <h2>⚖️ Décision de Validation</h2>
            <p style="color: #7f8c8d; margin: 15px 0;">
                Après examen du justificatif, prenez votre décision. En cas d'acceptation, l'absence sera justifiée. 
                En cas de refus, l'absence restera non justifiée.
            </p>
            
            <form id="validationForm" method="post" action="<%= request.getContextPath() %>/valider-justificatif">
                <input type="hidden" name="justificatifId" value="<%= justif.getJustificatifId() %>">
                <input type="hidden" id="actionField" name="action" value="">
                
                <div class="form-group">
                    <label>Commentaire (optionnel)</label>
                    <textarea name="commentaire" placeholder="Ajoutez un commentaire sur votre décision..."></textarea>
                </div>
                
                <div class="action-buttons">
                    <button type="button" class="btn-accept" onclick="if(validateForm('accepter')) { document.getElementById('actionField').value='accepter'; document.getElementById('validationForm').submit(); }">
                        ✓ Accepter le Justificatif
                    </button>
                    <button type="button" class="btn-refuse" onclick="if(validateForm('refuser')) { document.getElementById('actionField').value='refuser'; document.getElementById('validationForm').submit(); }">
                        ✗ Refuser le Justificatif
                    </button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>