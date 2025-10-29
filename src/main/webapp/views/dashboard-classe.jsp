<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cahiertexte.model.User" %>
<%@ page import="com.cahiertexte.model.Cours" %>
<%@ page import="com.cahiertexte.model.Matiere" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Matiere> matieresList = (List<Matiere>) request.getAttribute("matieresList");
    List<User> professeursList = (List<User>) request.getAttribute("professeursList");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Responsable Classe - Cahier de Texte TDSI</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', sans-serif; background: #f5f7fa; color: #2c3e50; }
        .navbar { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .navbar-brand { font-size: 20px; font-weight: bold; }
        .navbar-user { display: flex; align-items: center; gap: 20px; }
        .user-info { text-align: right; }
        .user-name { font-weight: 600; }
        .btn-logout { background: rgba(255,255,255,0.2); color: white; border: none; padding: 8px 20px; border-radius: 5px; text-decoration: none; display: inline-block; transition: 0.3s; }
        .btn-logout:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; margin-bottom: 20px; }
        .welcome-card h1 { color: #2c3e50; margin-bottom: 10px; }
        .controls { display:flex; justify-content:space-between; align-items:center; gap:12px; margin-bottom:18px; flex-wrap: wrap; }
        .primary-btn { background: #3498db; color: white; padding: 10px 18px; border-radius: 8px; text-decoration: none; font-weight:600; border: none; cursor:pointer; transition: 0.3s; display: inline-block; }
        .primary-btn:hover { background: #2980b9; transform: translateY(-2px); }
        .primary-btn.positive { background: #27ae60; }
        .primary-btn.positive:hover { background: #229954; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 20px; }
        .stat-card { background: white; padding: 20px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); transition: 0.3s; }
        .stat-card:hover { transform: translateY(-5px); box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
        .stat-card h3 { color: #7f8c8d; font-size: 13px; margin-bottom: 10px; text-transform: uppercase; }
        .stat-number { font-size: 28px; font-weight: bold; color: #3498db; margin-bottom: 5px; }
        .card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); margin-bottom: 20px; }
        .card h2 { color: #2c3e50; margin-bottom: 20px; font-size: 20px; }
        table { width:100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ecf0f1; text-align:left; font-size:14px; }
        th { background: #f8f9fa; color: #34495e; text-transform: uppercase; font-size:12px; font-weight: 600; }
        tr:hover { background: #f8fbff; }
        .action-btn { padding:7px 12px; border-radius:6px; color:white; text-decoration:none; font-size:13px; display:inline-block; margin-right: 5px; transition: 0.3s; font-weight: 500; }
        .action-btn:hover { transform: translateY(-2px); box-shadow: 0 2px 8px rgba(0,0,0,0.2); }
        .presence { background:#3498db; }
        .cahier { background:#9b59b6; }
        .badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; background: #3498db; color: white; }
        .muted { color:#7f8c8d; font-size:13px; }
        .success-message { background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; border-left: 4px solid #28a745; }
        .error-message { background: #f8d7da; color: #721c24; padding: 15px 20px; border-radius: 10px; margin-bottom: 20px; border-left: 4px solid #dc3545; }
        
        /* Modal styles */
        .modal-backdrop { position: fixed; inset:0; background: rgba(0,0,0,0.5); display:none; align-items:center; justify-content:center; z-index:999; animation: fadeIn 0.3s; }
        .modal-backdrop.active { display: flex; }
        .modal { background:white; width: 700px; max-width:95%; border-radius:15px; padding:25px; box-shadow: 0 10px 40px rgba(0,0,0,0.3); animation: slideUp 0.3s; max-height: 90vh; overflow-y: auto; }
        .modal-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; border-bottom: 2px solid #ecf0f1; padding-bottom: 15px; }
        .modal-header h3 { margin:0; color: #2c3e50; font-size: 22px; }
        .close-btn { background: none; border: none; font-size: 28px; cursor: pointer; color: #95a5a6; line-height: 1; padding: 0; width: 30px; height: 30px; }
        .close-btn:hover { color: #e74c3c; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; margin-bottom: 6px; font-weight: 600; color: #34495e; font-size: 14px; }
        .form-group.required label:after { content: " *"; color: #e74c3c; }
        .form-grid { display:grid; grid-template-columns: repeat(2,1fr); gap:16px; }
        .form-grid .full { grid-column: 1 / -1; }
        input[type="date"], input[type="time"], select, textarea, input[type="text"] { 
            width:100%; 
            padding:10px 12px; 
            border-radius:8px; 
            border:1px solid #d0d7de; 
            font-size:14px; 
            font-family: inherit;
            transition: 0.3s;
        }
        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }
        textarea { resize:vertical; min-height:80px; }
        .modal-footer { display:flex; justify-content:flex-end; gap:10px; margin-top:20px; padding-top: 15px; border-top: 1px solid #ecf0f1; }
        .btn-secondary { background: #95a5a6; color: white; padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; transition: 0.3s; }
        .btn-secondary:hover { background: #7f8c8d; }
        .btn-primary { background: #27ae60; color: white; padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; transition: 0.3s; }
        .btn-primary:hover { background: #229954; }
        
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        @keyframes slideUp {
            from { transform: translateY(50px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        
        .no-data { text-align: center; color: #7f8c8d; padding: 40px 20px; }
        .status-badge { padding: 6px 12px; border-radius: 12px; color: white; font-weight: 600; font-size: 11px; text-transform: uppercase; }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="navbar-brand">🎓 Cahier de Texte - TDSI</div>
        <div class="navbar-user">
            <div class="user-info">
                <div class="user-name"><%= user.getNomComplet() %></div>
                <div><span class="badge">Responsable <%= user.getClasse() %></span></div>
            </div>
            <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Déconnexion</a>
        </div>
    </div>

    <div class="container">
        <!-- Messages de succès/erreur -->
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
            <p class="muted">Responsable de classe <strong><%= user.getClasse() %></strong></p>
        </div>

        <div class="controls">
            <div>
                <button class="primary-btn positive" onclick="openModal()">➕ Planifier un nouveau cours</button>
                <a href="<%= request.getContextPath() %>/rapports" class="primary-btn">📊 Rapports</a>
            </div>
        </div>

        <!-- Statistiques -->
        <div class="stats-grid">
            <% 
            Map<String, Object> stats = (Map<String, Object>) request.getAttribute("statsGlobales");
            if (stats != null) {
            %>
                <div class="stat-card">
                    <h3>Total Cours</h3>
                    <div class="stat-number"><%= stats.get("nbTotal") != null ? stats.get("nbTotal") : 0 %></div>
                    <div class="muted">Planifiés</div>
                </div>
                <div class="stat-card">
                    <h3>Réalisés</h3>
                    <div class="stat-number"><%= stats.get("nbRealises") != null ? stats.get("nbRealises") : 0 %></div>
                    <div class="muted">Effectués</div>
                </div>
                <div class="stat-card">
                    <h3>Validés</h3>
                    <div class="stat-number"><%= stats.get("nbValides") != null ? stats.get("nbValides") : 0 %></div>
                    <div class="muted">Confirmés</div>
                </div>
                <div class="stat-card">
                    <h3>En Attente</h3>
                    <div class="stat-number"><%= stats.get("nbEnAttenteValidation") != null ? stats.get("nbEnAttenteValidation") : 0 %></div>
                    <div class="muted">À valider</div>
                </div>
            <% } %>

            <% 
            Map<String, Object> statsP = (Map<String, Object>) request.getAttribute("statsPresence");
            if (statsP != null) {
            %>
                <div class="stat-card">
                    <h3>Taux Présence</h3>
                    <div class="stat-number"><%= statsP.get("tauxPresenceMoyen") != null ? statsP.get("tauxPresenceMoyen") : 0 %>%</div>
                    <div class="muted">Moyenne classe</div>
                </div>
            <% } %>
        </div>

        <!-- Liste des cours -->
        <div class="card">
            <h2>📚 Cours de la Classe <%= user.getClasse() %></h2>
            <% 
            List<Cours> coursList = (List<Cours>) request.getAttribute("coursList");
            if (coursList != null && !coursList.isEmpty()) { 
            %>
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Matière</th>
                            <th>Professeur</th>
                            <th>Horaire</th>
                            <th>Salle</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        for (Cours c : coursList) { 
                            String statut = c.getStatutCours();
                            String statusColor = "#2ecc71";
                            if ("ANNULE".equalsIgnoreCase(statut)) statusColor = "#e74c3c";
                            else if ("REALISE".equalsIgnoreCase(statut)) statusColor = "#f39c12";
                            else if ("PLANIFIE".equalsIgnoreCase(statut)) statusColor = "#3498db";
                            
                            // DEBUG: Afficher les heures dans la console serveur
                            System.out.println("Heure Début brute: " + c.getHeureDebutPrevue());
                            System.out.println("Heure Fin brute: " + c.getHeureFinPrevue());
                            
                            // Formater les heures en format 24h (HH:mm)
                            String heureDebut = "-";
                            String heureFin = "-";
                            
                            if (c.getHeureDebutPrevue() != null) {
                                heureDebut = c.getHeureDebutPrevue().toString().substring(0, 5);
                                System.out.println("Heure Début formatée: " + heureDebut);
                            }
                            
                            if (c.getHeureFinPrevue() != null) {
                                heureFin = c.getHeureFinPrevue().toString().substring(0, 5);
                                System.out.println("Heure Fin formatée: " + heureFin);
                            }
                        %>
                        <tr>
                            <td><%= c.getDateCours() %></td>
                            <td><strong><%= c.getNomMatiere() %></strong></td>
                            <td><%= c.getNomProfesseur() %> <%= c.getPrenomProfesseur() %></td>
                            <td><strong><%= heureDebut %></strong> - <strong><%= heureFin %></strong></td>
                            <td><%= c.getSalle() != null ? c.getSalle() : "-" %></td>
                            <td>
                                <span class="status-badge" style="background:<%= statusColor %>"><%= statut %></span>
                            </td>
                            <td>
                                <a class="action-btn presence" href="<%= request.getContextPath() %>/presences?coursId=<%= c.getCoursId() %>">📋 Présences</a>
                                <a class="action-btn cahier" href="<%= request.getContextPath() %>/cahier-texte?coursId=<%= c.getCoursId() %>">📝 Cahier</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                    </tbody>
                </table>
            <% } else { %>
                <p class="no-data">📭 Aucun cours planifié pour le moment.<br><small>Cliquez sur "Planifier un nouveau cours" pour commencer.</small></p>
            <% } %>
        </div>
    </div>

    <!-- Modal de planification -->
    <div class="modal-backdrop" id="modalBackdrop" onclick="closeModalOnBackdrop(event)">
        <div class="modal" onclick="event.stopPropagation()">
            <div class="modal-header">
                <h3>📅 Planifier un nouveau cours</h3>
                <button class="close-btn" onclick="closeModal()">&times;</button>
            </div>
            
            <form method="POST" action="<%= request.getContextPath() %>/dashboard-classe">
                <div class="form-grid">
                    <div class="form-group required full">
                        <label for="matiereId">Matière</label>
                        <select id="matiereId" name="matiereId" required onchange="filterProfesseurs()">
                            <option value="">-- Sélectionner une matière --</option>
                            <% if (matieresList != null) {
                                for (Matiere m : matieresList) { %>
                                    <option value="<%= m.getMatiereId() %>" data-prof-id="<%= m.getProfesseurId() %>">
                                        <%= m.getNomMatiere() %> (<%= m.getVolumeHoraireTotal() %>h)
                                    </option>
                                <% }
                            } %>
                        </select>
                    </div>
                    
                    <div class="form-group required full">
                        <label for="professeurId">Professeur</label>
                        <select id="professeurId" name="professeurId" required>
                            <option value="">-- Sélectionner d'abord une matière --</option>
                        </select>
                        <!-- Liste complète des professeurs (cachée, pour le filtrage) -->
                        <div id="professeursList" style="display:none;">
                            <% if (professeursList != null) {
                                for (User prof : professeursList) { %>
                                    <option value="<%= prof.getUserId() %>" data-prof-id="<%= prof.getUserId() %>">
                                        <%= prof.getNomComplet() %>
                                    </option>
                                <% }
                            } %>
                        </div>
                    </div>
                    
                    <div class="form-group required">
                        <label for="dateCours">Date du cours</label>
                        <input type="date" id="dateCours" name="dateCours" required>
                    </div>
                    
                    <div class="form-group required">
                        <label for="salle">Salle</label>
                        <input type="text" id="salle" name="salle" placeholder="Ex: Salle 101" required>
                    </div>
                    
                    <div class="form-group required">
                        <label for="heureDebut">Heure de début</label>
                        <input type="time" id="heureDebut" name="heureDebut" required>
                    </div>
                    
                    <div class="form-group required">
                        <label for="heureFin">Heure de fin</label>
                        <input type="time" id="heureFin" name="heureFin" required>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn-secondary" onclick="closeModal()">Annuler</button>
                    <button type="submit" class="btn-primary">✓ Planifier le cours</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function openModal() {
            document.getElementById('modalBackdrop').classList.add('active');
            // Définir la date minimale à aujourd'hui
            document.getElementById('dateCours').min = new Date().toISOString().split('T')[0];
            // Réinitialiser le filtre des professeurs
            document.getElementById('professeurId').innerHTML = '<option value="">-- Sélectionner d\'abord une matière --</option>';
        }
        
        function closeModal() {
            document.getElementById('modalBackdrop').classList.remove('active');
        }
        
        function closeModalOnBackdrop(event) {
            if (event.target === event.currentTarget) {
                closeModal();
            }
        }
        
        // Filtrer les professeurs selon la matière sélectionnée
        function filterProfesseurs() {
            const matiereSelect = document.getElementById('matiereId');
            const professeurSelect = document.getElementById('professeurId');
            const professeursList = document.getElementById('professeursList');
            
            const selectedOption = matiereSelect.options[matiereSelect.selectedIndex];
            const profIdMatiere = selectedOption.getAttribute('data-prof-id');
            
            console.log('Matière sélectionnée, Prof ID:', profIdMatiere);
            
            // Réinitialiser la liste
            professeurSelect.innerHTML = '<option value="">-- Sélectionner un professeur --</option>';
            
            if (profIdMatiere && profIdMatiere !== '0' && profIdMatiere !== 'null') {
                // Récupérer toutes les options de professeurs depuis le div caché
                const allProfOptions = professeursList.querySelectorAll('option');
                let profFound = false;
                
                allProfOptions.forEach(option => {
                    const optionProfId = option.getAttribute('data-prof-id');
                    console.log('Comparaison:', optionProfId, '===', profIdMatiere);
                    
                    if (optionProfId === profIdMatiere) {
                        const newOption = document.createElement('option');
                        newOption.value = option.value;
                        newOption.textContent = option.textContent;
                        professeurSelect.appendChild(newOption);
                        profFound = true;
                    }
                });
                
                // Si un professeur trouvé, le sélectionner automatiquement
                if (profFound && professeurSelect.options.length === 2) {
                    professeurSelect.selectedIndex = 1;
                    console.log('Professeur sélectionné automatiquement');
                } else if (!profFound) {
                    console.warn('Aucun professeur trouvé pour cette matière');
                    professeurSelect.innerHTML = '<option value="">Aucun professeur assigné à cette matière</option>';
                }
            } else {
                console.log('Aucune matière sélectionnée ou pas de professeur assigné');
            }
        }
        
        // Fermer avec la touche Escape
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape') {
                closeModal();
            }
        });
    </script>
</body>
</html>