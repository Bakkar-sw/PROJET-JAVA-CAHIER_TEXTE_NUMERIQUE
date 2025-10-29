package com.cahiertexte.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cahiertexte.service.AuthenticationService;

/**
 * Servlet de déconnexion
 * URL: /logout
 * 
 * @author Projet TDSI
 * @version 1.0
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AuthenticationService authService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Détruire la session
        HttpSession session = request.getSession(false);
        if (session != null) {
            authService.destroyUserSession(session);
        }
        
        // Rediriger vers la page de login avec un message
        response.sendRedirect(request.getContextPath() + "/login?logout=success");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}