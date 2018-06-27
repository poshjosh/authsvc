package com.authsvc.servlets;

/**
 * @(#)AuthSvc.java   24-Nov-2014 22:19:05
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

import com.authsvc.AuthException;
import com.authsvc.servlets.request.CookieManager;
import com.authsvc.servlets.request.CookieToken;
import com.authsvc.auth.SecretToken;
import com.authsvc.servlets.request.RequestToken;
import com.authsvc.pu.entities.Appuser;
import com.authsvc.servlets.records.AppRecord;
import com.authsvc.servlets.records.AppuserRecord;
import com.bc.validators.ValidationException;
import com.authsvc.handlers.AuthHandler;
import com.authsvc.pu.entities.App;
import com.authsvc.servlets.request.RequestHandlerFactory;
import com.authsvc.servlets.request.Responder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class AuthSvc extends HttpServlet {

    private transient static final Logger LOG = Logger.getLogger(AuthSvc.class.getName());
    
    private transient static RequestHandlerFactory handlers;
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(
            HttpServletRequest request, 
            HttpServletResponse response) {

        try{
            
            if(handlers == null) {
                handlers = new RequestHandlerFactory();
            }

            AuthHandler handler = handlers.get(request);
        
            LOG.log(Level.FINER, "Request handler: {0}", handler);

            if(handler == null) {

                throw new ValidationException("Invalid request: "+request.getServletPath());

            }else{

                SecretToken secretToken = this.getSecretToken(request);
                
                handler.setSecretToken(secretToken);
                
                Map<String, Object> parameters = this.getParameters(handler, request);
                
                handler.setParameters(parameters);
                
                Object responseMsg = handler.process(handler);
                
                new Responder().respond(response, responseMsg);
            }
        }catch(ValidationException | IOException | AuthException | RuntimeException e) {
            
            this.handleException(request, response, e);
        }
    } 
    
    protected void handleException(            
            HttpServletRequest request, 
            HttpServletResponse response, 
            Exception e) {

        Responder responder = new Responder();

        responder.respond(response, e);
    }

    public final SecretToken getSecretToken(HttpServletRequest request) {
        
        SecretToken secretToken = new RequestToken(request);
        
        if(!isValid(secretToken)) { // May be a cookie 

            Cookie cookie = new CookieManager().getCookie(request);
            
            if(cookie != null) {
                
                secretToken = new CookieToken(cookie);
            }
        }
        
//LOG.log(Level.INFO, 
//"Series id: {0}, token: {1}",  
//secretToken==null?null:secretToken.getSeriesId(),
//secretToken==null?null:secretToken.getToken());

        return isValid(secretToken) ? secretToken : null;
    }
    
    
    public final boolean isValid(SecretToken secretToken) {
        if(secretToken == null || 
                secretToken.getSeriesId() == null ||
                secretToken.getToken() == null) {
            return false;
        }else{
            return true;
        }
    }

    public Map<String, Object> getParameters(AuthHandler handler, HttpServletRequest request) {
        Map<String, Object> parameters;
        Class entityClass = handler.getEntityClass();
        if(entityClass == App.class) {
            parameters = new AppRecord(request);
        }else if(entityClass == Appuser.class) {
            parameters = new AppuserRecord(request);
        }else{
            throw new UnsupportedOperationException("Unexpected entity class: "+entityClass.getName());
        }
        if(LOG.isLoggable(Level.FINER)) {
            final Map view = new HashMap(parameters);
            view.replace("password", "[PASSWORD]");
            LOG.log(Level.FINER, "Parameters: {0}", view);
        }
        return parameters;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "The Controller (main) servlet for the Authentication Service app";
    }// </editor-fold>
}
