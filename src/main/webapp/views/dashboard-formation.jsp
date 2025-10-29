<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Cours" %>
<%@ page import="com.cahiertexte.model.Matiere" %>
<%@ page import="com.cahiertexte.model.Justificatif" %>
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
    <title>Dashboard Responsable Formation - Cahier de Texte TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; color: #2c3e50; }
        .navbar { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .user-name { font-weight: 600; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; }
        .btn-logout:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 1400px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); text-align: center; margin-bottom: 20px; }
        .badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: #e74c3c; color: white; }
        .success-message, .error-message { padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; }
        .success-message { background: #d4edda; color: #155724; border-left: 4px solid #28a745; }
        .error-message { background: #f8d7da; color: #721c24; border-left: 4px solid #dc3545; }
        
        /* Tabs */
        .tabs { display: flex; gap: 5px; margin-bottom: 20px; border-bottom: 2px solid #ecf0f1; }
        .tab { padding: 12px 25px; background: transparent; border: none; cursor: pointer; font-weight: 600; color: #7f8c8d; transition: 0.3s; border-bottom: 3px solid transparent; }
        .tab:hover { color: #e74c3c; }
        .tab.active { color: #e74c3c; border-bottom-color: #e74c3c; }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        
        /* Stats */
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 20px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); transition: 0.3s; }
        .stat-card:hover { transform: translateY(-5px); }
        .stat-card h3 { color: #7f8c8d; font-size: 13px; margin-bottom: 10px; text-transform: uppercase; }
        .stat-number { font-size: 32px; font-weight: bold; color: #e74c3c; }
        
        /* Cards */
        .card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; }
        .alert-card { background: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; border-radius: 10px; margin-bottom: 20px; }
        
        /* Tables */
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ecf0f1; text-align: left; font-size: 14px; }
        th { background: #f8f9fa; color: #34495e; text-transform: uppercase; font-size: 12px; font-weight: 600; }
        tr:hover { background: #f8fbff; }
        
        /* Buttons */
        .btn { padding: 8px 16px; border-radius: 6px; text-decoration: none; font-size: 13px; font-weight: 600; border: none; cursor: pointer; transition: 0.3s; display: inline-block; margin: 2px; }
        .btn-primary { background: #3498db; color: white; }
        .btn-success { background: #27ae60; color: white; }
        .btn-warning { background: #f39c12; color: white; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 2px 8px rgba(0,0,0,0.2); }
        
        /* Search & Filter */
        .controls { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 10px; }
        .search-box { padding: 8px 15px; border: 1px solid #d0d7de; border-radius: 8px; width: 250px; }
        
        /* Modal */
        .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 999; }
        .modal-backdrop.active { display: flex; }
        .modal { background: white; width: 700px; max-width: 95%; border-radius: 15px; padding: 25px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); max-height: 90vh; overflow-y: auto; }
        .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 15px; }
        .modal-header h3 { margin: 0; color: #2c3e50; }
        .close-btn { background: none; border: none; font-size: 28px; cursor: pointer; color: #95a5a6; }
        .close-btn:hover { color: #e74c3c; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #34495e; font-size: 14px; }
        .form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
        .form-grid .full { grid-column: 1 / -1; }
        input, select, textarea { width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #d0d7de; font-size: 14px; font-family: inherit; }
        input:focus, select:focus, textarea:focus { outline: none; border-color: #e74c3c; box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1); }
        .modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; padding-top: 15px; border-top: 1px solid #ecf0f1; }
        .no-data { text-align: center; color: #7f8c8d; padding: 40px 20px; }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div>
                <div class="user-name"><%= user.getNomComplet() %></div>
                <span class="badge">Responsable Formation</span>
            </div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>

    <div class="container">
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="success-message">✓ <%= session.getAttribute("successMessage") %></div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        <% if (session.getAttribute("errorMessage") != null) { %>
            <div class="error-message">✗ <%= session.getAttribute("errorMessage") %></div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>

        <div class="welcome-card">
            <h1>Bienvenue, <%= user.getPrenom() %> !</h1>
            <p style="color: #7f8c8d;">Supervision globale - Institut TDSI</p>
        </div>

        <!-- Tabs Navigation -->
        <div class="tabs">
            <button class="tab active" onclick="showTab('dashboard')">📊 Tableau de Bord</button>
            <button class="tab" onclick="showTab('users')">👥 Gestion Utilisateurs</button>
            <button class="tab" onclick="showTab('matieres')">📚 Gestion Matières</button>
            <button class="tab" onclick="showTab('justificatifs')">📄 Justificatifs</button>
        </div>

        <!-- TAB 1: DASHBOARD -->
        <div id="tab-dashboard" class="tab-content active">
            <%
            List<Cours> coursNonValides = (List<Cours>) request.getAttribute("coursNonValides");
            List<Map<String, Object>> alertes = (List<Map<String, Object>>) request.getAttribute("alertes");
            List<Justificatif> justificatifsEnAttente = (List<Justificatif>) request.getAttribute("justificatifsEnAttente");
            int nbNonValides = coursNonValides != null ? coursNonValides.size() : 0;
            int nbAlertes = alertes != null ? alertes.size() : 0;
            int nbJustifs = justificatifsEnAttente != null ? justificatifsEnAttente.size() : 0;
            %>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>Cours Non Validés</h3>
                    <div class="stat-number"><%= nbNonValides %></div>
                    <div style="color: #7f8c8d; font-size: 14px;">En attente</div>
                </div>
                <div class="stat-card">
                    <h3>Alertes Actives</h3>
                    <div class="stat-number"><%= nbAlertes %></div>
                    <div style="color: #7f8c8d; font-size: 14px;">Matières < 12h</div>
                </div>
                <div class="stat-card">
                    <h3>Justificatifs</h3>
                    <div class="stat-number"><%= nbJustifs %></div>
                    <div style="color: #7f8c8d; font-size: 14px;">En attente</div>
                </div>
            </div>

            <a href="<%= request.getContextPath() %>/rapports" class="btn btn-primary" style="margin-bottom: 20px;">📊 Voir les Rapports Détaillés</a>

            <% if (nbAlertes > 0) { %>
            <div class="alert-card">
                <h3 style="color: #856404; margin-bottom: 10px;">⚠️ Alertes : Matières avec moins de 12h restantes</h3>
                <p style="color: #856404;">Des matières nécessitent une attention particulière.</p>
            </div>
            <% } %>

            <div class="card">
                <h2>📋 Cours en Attente de Validation</h2>
                <% if (nbNonValides > 0) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Matière</th>
                                <th>Classe</th>
                                <th>Professeur</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Cours cours : coursNonValides) { %>
                            <tr>
                                <td><%= cours.getDateCours() %></td>
                                <td><%= cours.getNomMatiere() %></td>
                                <td><%= cours.getClasse() %></td>
                                <td><%= cours.getNomProfesseur() %> <%= cours.getPrenomProfesseur() %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">✓ Aucun cours en attente</p>
                <% } %>
            </div>

            <div class="card">
                <h2>📄 Justificatifs d'Absence en Attente</h2>
                <% if (nbJustifs > 0) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Étudiant</th>
                                <th>Classe</th>
                                <th>Type</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Justificatif j : justificatifsEnAttente) { %>
                            <tr>
                                <td><%= j.getDateSoumission() != null ? j.getDateSoumission().toString().substring(0,10) : "" %></td>
                                <td><%= j.getEtudiantComplet() %></td>
                                <td><%= j.getClasseEtudiant() %></td>
                                <td><%= j.getTypeLibelle() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/valider-justificatif?justificatifId=<%= j.getJustificatifId() %>" class="btn btn-primary">👁️ Examiner</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">✓ Aucun justificatif en attente</p>
                <% } %>
            </div>
        </div>

        <!-- TAB 2: GESTION UTILISATEURS -->
        <div id="tab-users" class="tab-content">
            <div class="card">
                <div class="controls">
                    <h2>👥 Gestion des Utilisateurs</h2>
                    <div>
                        <select id="roleFilter" onchange="filterUsers()" style="padding: 8px; border-radius: 6px; margin-right: 10px;">
                            <option value="">Tous les rôles</option>
                            <option value="PROFESSEUR">Professeur</option>
                            <option value="ETUDIANT">Étudiant</option>
                            <option value="RESPONSABLE_CLASSE">Responsable Classe</option>
                            <option value="RESPONSABLE_FORMATION">Responsable Formation</option>
                        </select>
                        <input type="text" id="userSearch" placeholder="🔍 Rechercher..." class="search-box" onkeyup="filterUsers()">
                        <button class="btn btn-success" onclick="openUserModal('create')">➕ Créer Utilisateur</button>
                    </div>
                </div>
                
                <%
                List<User> allUsers = (List<User>) request.getAttribute("allUsers");
                if (allUsers != null && !allUsers.isEmpty()) {
                %>
                    <table id="usersTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nom Complet</th>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Rôle</th>
                                <th>Classe</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (User u : allUsers) { %>
                            <tr data-role="<%= u.getRole() %>" 
                                data-search="<%= u.getNomComplet().toLowerCase() %> <%= u.getUsername().toLowerCase() %>"
                                data-user-id="<%= u.getUserId() %>"
                                data-username="<%= u.getUsername() %>"
                                data-nom="<%= u.getNom() %>"
                                data-prenom="<%= u.getPrenom() %>"
                                data-email="<%= u.getEmail() != null ? u.getEmail() : "" %>"
                                data-telephone="<%= u.getTelephone() != null ? u.getTelephone() : "" %>"
                                data-user-role="<%= u.getRole() %>"
                                data-classe="<%= u.getClasse() != null ? u.getClasse() : "" %>"
                                data-statut="<%= u.getStatut() %>"
                                data-nom-complet="<%= u.getNomComplet() %>">
                                <td><%= u.getUserId() %></td>
                                <td><strong><%= u.getNomComplet() %></strong></td>
                                <td><%= u.getUsername() %></td>
                                <td><%= u.getEmail() != null ? u.getEmail() : "-" %></td>
                                <td><span class="badge" style="background: #3498db;"><%= u.getRole() %></span></td>
                                <td><%= u.getClasse() != null ? u.getClasse() : "-" %></td>
                                <td><%= u.getStatut() %></td>
                                <td>
                                    <button class="btn btn-warning" onclick="editUserById(<%= u.getUserId() %>)">✏️ Modifier</button>
                                    <button class="btn btn-danger" onclick="deleteUserById(<%= u.getUserId() %>)">🗑️ Supprimer</button>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">Aucun utilisateur trouvé</p>
                <% } %>
            </div>
        </div>

        <!-- TAB 3: GESTION MATIÈRES -->
        <div id="tab-matieres" class="tab-content">
            <div class="card">
                <div class="controls">
                    <h2>📚 Gestion des Matières</h2>
                    <div>
                        <input type="text" id="matiereSearch" placeholder="🔍 Rechercher..." class="search-box" onkeyup="filterMatieres()">
                        <button class="btn btn-success" onclick="openMatiereModal('create')">➕ Créer Matière</button>
                    </div>
                </div>
                
                <%
                List<Matiere> allMatieres = (List<Matiere>) request.getAttribute("allMatieres");
                if (allMatieres != null && !allMatieres.isEmpty()) {
                %>
                    <table id="matieresTable">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Nom Matière</th>
                                <th>Classe</th>
                                <th>Volume (h)</th>
                                <th>Coef</th>
                                <th>Semestre</th>
                                <th>Professeur</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Matiere m : allMatieres) { %>
                            <tr data-search="<%= m.getNomMatiere().toLowerCase() %> <%= m.getCodeMatiere().toLowerCase() %>"
                                data-matiere-id="<%= m.getMatiereId() %>"
                                data-code="<%= m.getCodeMatiere() %>"
                                data-nom="<%= m.getNomMatiere() %>"
                                data-classe="<%= m.getClasse() %>"
                                data-volume="<%= m.getVolumeHoraireTotal() %>"
                                data-coef="<%= m.getCoefficient() %>"
                                data-semestre="<%= m.getSemestre() %>"
                                data-annee="<%= m.getAnneeAcademique() %>"
                                data-prof-id="<%= m.getProfesseurId() %>"
                                data-nom-matiere="<%= m.getNomMatiere() %>">
                                <td><strong><%= m.getCodeMatiere() %></strong></td>
                                <td><%= m.getNomMatiere() %></td>
                                <td><%= m.getClasse() %></td>
                                <td><%= m.getVolumeHoraireTotal() %></td>
                                <td><%= m.getCoefficient() %></td>
                                <td><%= m.getSemestre() %></td>
                                <td><%= m.getNomProfesseur() != null ? m.getNomProfesseur() : "-" %></td>
                                <td>
                                    <button class="btn btn-warning" onclick="editMatiereById(<%= m.getMatiereId() %>)">✏️ Modifier</button>
                                    <button class="btn btn-danger" onclick="deleteMatiereById(<%= m.getMatiereId() %>)">🗑️ Supprimer</button>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">Aucune matière trouvée</p>
                <% } %>
            </div>
        </div>

        <!-- TAB 4: TOUS LES JUSTIFICATIFS -->
        <div id="tab-justificatifs" class="tab-content">
            <div class="card">
                <div class="controls">
                    <h2>📄 Tous les Justificatifs</h2>
                    <select id="statutFilter" onchange="filterJustificatifs()" style="padding: 8px; border-radius: 6px;">
                        <option value="">Tous les statuts</option>
                        <option value="EN_ATTENTE">En attente</option>
                        <option value="ACCEPTE">Accepté</option>
                        <option value="REFUSE">Refusé</option>
                    </select>
                </div>
                
                <%
                List<Justificatif> allJustificatifs = (List<Justificatif>) request.getAttribute("allJustificatifs");
                if (allJustificatifs != null && !allJustificatifs.isEmpty()) {
                %>
                    <table id="justificatifsTable">
                        <thead>
                            <tr>
                                <th>Date Soumission</th>
                                <th>Étudiant</th>
                                <th>Classe</th>
                                <th>Type</th>
                                <th>Statut</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Justificatif j : allJustificatifs) { 
                                String statut = j.getStatut();
                                String statutColor = "#f39c12";
                                if ("ACCEPTE".equals(statut)) statutColor = "#27ae60";
                                else if ("REFUSE".equals(statut)) statutColor = "#e74c3c";
                            %>
                            <tr data-statut="<%= statut %>">
                                <td><%= j.getDateSoumission() != null ? j.getDateSoumission().toString().substring(0,10) : "" %></td>
                                <td><%= j.getEtudiantComplet() %></td>
                                <td><%= j.getClasseEtudiant() %></td>
                                <td><%= j.getTypeLibelle() %></td>
                                <td><span class="badge" style="background: <%= statutColor %>;"><%= statut %></span></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/valider-justificatif?justificatifId=<%= j.getJustificatifId() %>" class="btn btn-primary">👁️ Voir</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p class="no-data">Aucun justificatif trouvé</p>
                <% } %>
            </div>
        </div>
    </div>

    <!-- MODAL UTILISATEUR -->
    <div class="modal-backdrop" id="userModalBackdrop">
        <div class="modal">
            <div class="modal-header">
                <h3 id="userModalTitle">Créer un utilisateur</h3>
                <button class="close-btn" onclick="closeUserModal()">&times;</button>
            </div>
            <form method="POST" action="<%= request.getContextPath() %>/dashboard-formation">
                <input type="hidden" name="action" id="userAction" value="createUser">
                <input type="hidden" name="userId" id="userId">
                
                <div class="form-grid">
                    <div class="form-group">
                        <label>Username *</label>
                        <input type="text" name="username" id="username" required>
                    </div>
                    <div class="form-group">
                        <label>Mot de passe <span id="pwdNote" style="display:none;">(laisser vide pour ne pas changer)</span></label>
                        <input type="password" name="password" id="password">
                    </div>
                    <div class="form-group">
                        <label>Nom *</label>
                        <input type="text" name="nom" id="nom" required>
                    </div>
                    <div class="form-group">
                        <label>Prénom *</label>
                        <input type="text" name="prenom" id="prenom" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" id="email">
                    </div>
                    <div class="form-group">
                        <label>Téléphone</label>
                        <input type="text" name="telephone" id="telephone">
                    </div>
                    <div class="form-group">
                        <label>Rôle *</label>
                        <select name="role" id="role" required>
                            <option value="">-- Sélectionner --</option>
                            <option value="PROFESSEUR">Professeur</option>
                            <option value="ETUDIANT">Étudiant</option>
                            <option value="RESPONSABLE_CLASSE">Responsable Classe</option>
                            <option value="RESPONSABLE_FORMATION">Responsable Formation</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Classe</label>
                        <select name="classe" id="classe">
                            <option value="">-- Aucune --</option>
                            <option value="CI_M1">CI_M1</option>
                            <option value="CI_M2">CI_M2</option>
                            <option value="GL_M1">GL_M1</option>
                            <option value="GL_M2">GL_M2</option>
                        </select>
                    </div>
                    <div class="form-group" id="statutGroup" style="display:none;">
                        <label>Statut</label>
                        <select name="statut" id="statut">
                            <option value="ACTIF">ACTIF</option>
                            <option value="INACTIF">INACTIF</option>
                        </select>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" onclick="closeUserModal()">Annuler</button>
                    <button type="submit" class="btn btn-success">✓ Enregistrer</button>
                </div>
            </form>
        </div>
    </div>

    <!-- MODAL MATIÈRE -->
    <div class="modal-backdrop" id="matiereModalBackdrop">
        <div class="modal">
            <div class="modal-header">
                <h3 id="matiereModalTitle">Créer une matière</h3>
                <button class="close-btn" onclick="closeMatiereModal()">&times;</button>
            </div>
            <form method="POST" action="<%= request.getContextPath() %>/dashboard-formation">
                <input type="hidden" name="action" id="matiereAction" value="createMatiere">
                <input type="hidden" name="matiereId" id="matiereId">
                
                <div class="form-grid">
                    <div class="form-group">
                        <label>Code Matière *</label>
                        <input type="text" name="codeMatiere" id="codeMatiere" required>
                    </div>
                    <div class="form-group">
                        <label>Nom Matière *</label>
                        <input type="text" name="nomMatiere" id="nomMatiere" required>
                    </div>
                    <div class="form-group">
                        <label>Classe *</label>
                        <select name="classe" id="classeMatiere" required>
                            <option value="">-- Sélectionner --</option>
                            <option value="CI_M1">CI_M1</option>
                            <option value="CI_M2">CI_M2</option>
                            <option value="GL_M1">GL_M1</option>
                            <option value="GL_M2">GL_M2</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Volume Horaire (h) *</label>
                        <input type="number" name="volumeHoraire" id="volumeHoraire" step="0.5" required>
                    </div>
                    <div class="form-group">
                        <label>Coefficient *</label>
                        <input type="number" name="coefficient" id="coefficient" required>
                    </div>
                    <div class="form-group">
                        <label>Semestre *</label>
                        <select name="semestre" id="semestre" required>
                            <option value="">-- Sélectionner --</option>
                            <option value="SEMESTRE_1">Semestre 1</option>
                            <option value="SEMESTRE_2">Semestre 2</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Année Académique *</label>
                        <input type="text" name="anneeAcademique" id="anneeAcademique" value="2024-2025" required>
                    </div>
                    <div class="form-group">
                        <label>Professeur</label>
                        <select name="professeurId" id="professeurIdMatiere">
                            <option value="">-- Aucun --</option>
                            <% 
                            if (allUsers != null) {
                                for (User prof : allUsers) {
                                    if ("PROFESSEUR".equals(prof.getRole())) {
                            %>
                                <option value="<%= prof.getUserId() %>"><%= prof.getNomComplet() %></option>
                            <%      }
                                }
                            }
                            %>
                        </select>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" onclick="closeMatiereModal()">Annuler</button>
                    <button type="submit" class="btn btn-success">✓ Enregistrer</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Gestion des onglets
        function showTab(tabName) {
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            event.target.classList.add('active');
            document.getElementById('tab-' + tabName).classList.add('active');
        }

        // === GESTION UTILISATEURS ===
        function openUserModal(mode) {
            document.getElementById('userModalBackdrop').classList.add('active');
            if (mode === 'create') {
                document.getElementById('userModalTitle').textContent = 'Créer un utilisateur';
                document.getElementById('userAction').value = 'createUser';
                document.getElementById('password').required = true;
                document.getElementById('pwdNote').style.display = 'none';
                document.getElementById('statutGroup').style.display = 'none';
                document.querySelector('#userModalBackdrop form').reset();
            }
        }

        function editUserById(userId) {
            const row = document.querySelector('tr[data-user-id="' + userId + '"]');
            if (!row) return;
            
            document.getElementById('userModalBackdrop').classList.add('active');
            document.getElementById('userModalTitle').textContent = 'Modifier un utilisateur';
            document.getElementById('userAction').value = 'updateUser';
            document.getElementById('userId').value = row.getAttribute('data-user-id');
            document.getElementById('username').value = row.getAttribute('data-username');
            document.getElementById('nom').value = row.getAttribute('data-nom');
            document.getElementById('prenom').value = row.getAttribute('data-prenom');
            document.getElementById('email').value = row.getAttribute('data-email');
            document.getElementById('telephone').value = row.getAttribute('data-telephone');
            document.getElementById('role').value = row.getAttribute('data-user-role');
            document.getElementById('classe').value = row.getAttribute('data-classe');
            document.getElementById('statut').value = row.getAttribute('data-statut');
            document.getElementById('password').required = false;
            document.getElementById('password').value = '';
            document.getElementById('pwdNote').style.display = 'inline';
            document.getElementById('statutGroup').style.display = 'block';
        }

        function closeUserModal() {
            document.getElementById('userModalBackdrop').classList.remove('active');
        }

        function deleteUserById(userId) {
            const row = document.querySelector('tr[data-user-id="' + userId + '"]');
            const nom = row ? row.getAttribute('data-nom-complet') : 'cet utilisateur';
            
            if (confirm('Êtes-vous sûr de vouloir supprimer ' + nom + ' ?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '<%= request.getContextPath() %>/dashboard-formation';
                form.innerHTML = '<input type="hidden" name="action" value="deleteUser"><input type="hidden" name="userId" value="' + userId + '">';
                document.body.appendChild(form);
                form.submit();
            }
        }

        function filterUsers() {
            const roleFilter = document.getElementById('roleFilter').value.toLowerCase();
            const searchText = document.getElementById('userSearch').value.toLowerCase();
            const rows = document.querySelectorAll('#usersTable tbody tr');
            
            rows.forEach(row => {
                const role = row.getAttribute('data-role').toLowerCase();
                const searchData = row.getAttribute('data-search');
                const roleMatch = !roleFilter || role === roleFilter;
                const searchMatch = !searchText || searchData.includes(searchText);
                row.style.display = roleMatch && searchMatch ? '' : 'none';
            });
        }

        // === GESTION MATIÈRES ===
        function openMatiereModal(mode) {
            document.getElementById('matiereModalBackdrop').classList.add('active');
            document.getElementById('matiereModalTitle').textContent = 'Créer une matière';
            document.getElementById('matiereAction').value = 'createMatiere';
            document.querySelector('#matiereModalBackdrop form').reset();
            document.getElementById('anneeAcademique').value = '2024-2025';
        }

        function editMatiereById(matiereId) {
            const row = document.querySelector('tr[data-matiere-id="' + matiereId + '"]');
            if (!row) return;
            
            document.getElementById('matiereModalBackdrop').classList.add('active');
            document.getElementById('matiereModalTitle').textContent = 'Modifier une matière';
            document.getElementById('matiereAction').value = 'updateMatiere';
            document.getElementById('matiereId').value = row.getAttribute('data-matiere-id');
            document.getElementById('codeMatiere').value = row.getAttribute('data-code');
            document.getElementById('nomMatiere').value = row.getAttribute('data-nom');
            document.getElementById('classeMatiere').value = row.getAttribute('data-classe');
            document.getElementById('volumeHoraire').value = row.getAttribute('data-volume');
            document.getElementById('coefficient').value = row.getAttribute('data-coef');
            document.getElementById('semestre').value = row.getAttribute('data-semestre');
            document.getElementById('anneeAcademique').value = row.getAttribute('data-annee');
            document.getElementById('professeurIdMatiere').value = row.getAttribute('data-prof-id') || '';
        }

        function closeMatiereModal() {
            document.getElementById('matiereModalBackdrop').classList.remove('active');
        }

        function deleteMatiereById(matiereId) {
            const row = document.querySelector('tr[data-matiere-id="' + matiereId + '"]');
            const nom = row ? row.getAttribute('data-nom-matiere') : 'cette matière';
            
            if (confirm('Êtes-vous sûr de vouloir supprimer ' + nom + ' ?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '<%= request.getContextPath() %>/dashboard-formation';
                form.innerHTML = '<input type="hidden" name="action" value="deleteMatiere"><input type="hidden" name="matiereId" value="' + matiereId + '">';
                document.body.appendChild(form);
                form.submit();
            }
        }

        function filterMatieres() {
            const searchText = document.getElementById('matiereSearch').value.toLowerCase();
            const rows = document.querySelectorAll('#matieresTable tbody tr');
            
            rows.forEach(row => {
                const searchData = row.getAttribute('data-search');
                row.style.display = searchData.includes(searchText) ? '' : 'none';
            });
        }

        // === JUSTIFICATIFS ===
        function filterJustificatifs() {
            const statutFilter = document.getElementById('statutFilter').value;
            const rows = document.querySelectorAll('#justificatifsTable tbody tr');
            
            rows.forEach(row => {
                const statut = row.getAttribute('data-statut');
                row.style.display = !statutFilter || statut === statutFilter ? '' : 'none';
            });
        }
    </script>
</body>
</html>