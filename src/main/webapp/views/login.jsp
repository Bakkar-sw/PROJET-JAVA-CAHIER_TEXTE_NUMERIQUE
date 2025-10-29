<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Cahier de Texte TDSI</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap');

        :root {
            --primary: #667eea;
            --secondary: #764ba2;
            --text-dark: #2c3e50;
            --text-light: #ecf0f1;
            --card-bg: rgba(255, 255, 255, 0.2);
            --blur: 25px;
        }

        body.light {
            --primary: #667eea;
            --secondary: #764ba2;
            --text-dark: #2c3e50;
            --text-light: #34495e;
            --card-bg: rgba(255, 255, 255, 0.35);
            background: linear-gradient(-45deg, #e0e7ff, #fff, #c7d2fe);
            color: var(--text-dark);
        }

        body.dark {
            --primary: #8b9fff;
            --secondary: #b57aff;
            --text-dark: #ecf0f1;
            --text-light: #bdc3c7;
            --card-bg: rgba(30, 30, 45, 0.6);
            background: linear-gradient(-45deg, #0f172a, #1e293b, #312e81);
            color: var(--text-dark);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html, body {
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            transition: background 0.5s ease, color 0.5s ease;
            overflow-y: auto;
            padding: 20px;
        }

        /* ======= Bouton de bascule thème ======= */
        .theme-toggle {
            position: fixed;
            top: 20px;
            right: 25px;
            background: rgba(255,255,255,0.2);
            border: none;
            border-radius: 50%;
            width: 45px;
            height: 45px;
            font-size: 22px;
            cursor: pointer;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            z-index: 50;
        }

        .theme-toggle:hover {
            transform: scale(1.1);
        }

        /* ====== Effets lumineux ====== */
        .glow {
            position: absolute;
            width: 250px;
            height: 250px;
            background: radial-gradient(circle, rgba(118,75,162,0.35) 0%, transparent 70%);
            filter: blur(80px);
            border-radius: 50%;
            z-index: 0;
            animation: float 8s ease-in-out infinite;
        }

        .glow:nth-child(1) {
            top: -60px;
            left: -60px;
            animation-delay: 0s;
        }

        .glow:nth-child(2) {
            bottom: -60px;
            right: -60px;
            animation-delay: 3s;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0) translateX(0); }
            50% { transform: translateY(-20px) translateX(15px); }
        }

        /* ======= Conteneur ======= */
        .login-container {
            position: relative;
            z-index: 2;
            backdrop-filter: blur(var(--blur));
            background: var(--card-bg);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: 25px;
            padding: 60px 50px;
            width: 420px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.35);
            animation: fadeInUp 0.8s ease-out;
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .login-header {
            text-align: center;
            margin-bottom: 35px;
        }

        .login-header h1 {
            font-size: 30px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .login-header p {
            font-size: 14px;
            color: var(--text-light);
        }

        /* ===== Formulaire ===== */
        .form-group {
            position: relative;
            margin-bottom: 25px;
        }

        .form-group input {
            width: 100%;
            padding: 14px 50px 14px 15px;
            border: 1.8px solid rgba(255,255,255,0.3);
            border-radius: 12px;
            background: rgba(255,255,255,0.1);
            color: var(--text-dark);
            font-size: 15px;
            transition: all 0.3s ease;
        }

        .form-group input:focus {
            border-color: var(--primary);
            box-shadow: 0 0 10px rgba(102,126,234,0.4);
            outline: none;
            background: rgba(255,255,255,0.15);
        }

        .form-group label {
            display: block;
            font-weight: 600;
            font-size: 14px;
            margin-bottom: 8px;
            color: var(--text-light);
        }

        .input-icon {
            position: absolute;
            right: 15px;
            top: 42px;
            font-size: 18px;
            color: #aaa;
        }

        /* ===== Bouton ===== */
        .btn-login {
            width: 100%;
            padding: 14px;
            margin-top: 10px;
            border: none;
            border-radius: 12px;
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            color: #fff;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: all 0.3s ease;
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102,126,234,0.4);
        }

        /* ====== Alertes ====== */
        .alert {
            padding: 12px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-error {
            background: rgba(255, 50, 50, 0.1);
            color: #ff5c5c;
            border: 1px solid rgba(255, 50, 50, 0.2);
        }

        .alert-success {
            background: rgba(50, 255, 100, 0.1);
            color: #3c3;
            border: 1px solid rgba(50, 255, 100, 0.2);
        }

        .login-footer {
            text-align: center;
            margin-top: 25px;
            font-size: 13px;
            color: var(--text-light);
            opacity: 0.8;
        }

        /* ====== Comptes de test ====== */
        .test-accounts {
            margin-top: 25px;
            padding: 15px;
            border-radius: 10px;
            border: 1px solid rgba(255,255,255,0.1);
            background: rgba(255,255,255,0.08);
            transition: all 0.3s ease;
        }

        .test-accounts h4 {
            font-size: 13px;
            margin-bottom: 10px;
            color: var(--text-dark);
        }

        .test-accounts p {
            font-size: 12px;
            color: var(--text-light);
            margin: 4px 0;
        }

        .test-accounts code {
            color: var(--primary);
            background: rgba(255,255,255,0.15);
            padding: 2px 6px;
            border-radius: 4px;
        }

        .accounts-content {
            display: none;
            opacity: 0;
            max-height: 0;
            overflow: hidden;
            transition: all 0.4s ease;
        }

        .accounts-content.show {
            display: block;
            opacity: 1;
            max-height: 500px;
        }

        .toggle-btn {
            background: none;
            border: none;
            color: var(--primary);
            font-weight: 600;
            cursor: pointer;
            margin-bottom: 10px;
        }

    </style>
</head>
<body class="light">
    <!-- Effets lumineux -->
    <div class="glow"></div>
    <div class="glow"></div>

    <!-- Bouton bascule thème -->
    <button class="theme-toggle" id="themeToggle">🌙</button>

    <div class="login-container">
        <div class="login-header">
            <h1>🎓 Cahier de Texte Numérique</h1>
            <p>Institut TDSI - Université Cheikh Anta Diop de Dakar</p>
        </div>

        <% if (request.getParameter("logout") != null) { %>
            <div class="alert alert-success">
                ✓ Déconnexion réussie. À bientôt !
            </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                ✗ <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/login">
            <div class="form-group">
                <label for="username">Nom d'utilisateur</label>
                <input type="text" id="username" name="username"
                    placeholder="Entrez votre nom d'utilisateur"
                    value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                    required autofocus>
                <span class="input-icon">👤</span>
            </div>

            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password"
                    placeholder="Entrez votre mot de passe" required>
                <span class="input-icon">🔒</span>
            </div>

            <button type="submit" class="btn-login">Se connecter</button>
        </form>

        <div class="test-accounts">
            <button class="toggle-btn" type="button" id="toggleAccounts">Afficher les comptes de test ⬇️</button>
            <div class="accounts-content" id="accountsContent">
                <h4>💡 Comptes de test :</h4>
                <p>👤 Responsable Formation : <code>resp.formation</code></p>
                <p>👤 Responsable Classe : <code>resp.ci.m1</code></p>
                <p>👤 Professeur : <code>prof.samb</code></p>
                <p>👤 Étudiant : <code>etud.seck</code></p>
                <p>🔑 Mot de passe : <code>password123</code></p>
            </div>
        </div>

        <div class="login-footer">
            © 2024-2025 Institut TDSI - Tous droits réservés
        </div>
    </div>

    <script>
        // Basculer entre Light et Dark Mode
        const themeToggle = document.getElementById("themeToggle");
        const body = document.body;
        themeToggle.addEventListener("click", () => {
            body.classList.toggle("dark");
            body.classList.toggle("light");
            themeToggle.textContent = body.classList.contains("dark") ? "☀️" : "🌙";
        });

        // Repliable : Comptes de test
        const toggleAccounts = document.getElementById("toggleAccounts");
        const accountsContent = document.getElementById("accountsContent");
        toggleAccounts.addEventListener("click", () => {
            accountsContent.classList.toggle("show");
            toggleAccounts.textContent = accountsContent.classList.contains("show")
                ? "Masquer les comptes de test ⬆️"
                : "Afficher les comptes de test ⬇️";
        });
    </script>
</body>
</html>
