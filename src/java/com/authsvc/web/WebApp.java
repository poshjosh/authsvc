package com.authsvc.web;

import com.authsvc.AuthSvcApp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.configuration.ConfigurationException;
import com.authsvc.ConfigNames;
import com.authsvc.handlers.Activateapp;
import com.authsvc.handlers.Activateuser;
import com.authsvc.handlers.AuthHandler;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Appuser;

/**
 * @(#)WebApp.java   16-Oct-2014 10:08:15
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class WebApp extends AuthSvcApp {
    
    private boolean productionMode;
    
    private ServletContext servletContext;
    
    private static WebApp instance;
    
    private WebApp() { }
    
    public static WebApp getInstance() {
        if(instance == null) {
            instance = new WebApp();
        }
        return instance;
    }
    
    public void init(ServletContext context) 
            throws ServletException, IOException, ConfigurationException {
        
        try{
            
            this.servletContext = context;
            
            String sval = context.getInitParameter("productionMode");
            this.productionMode = Boolean.parseBoolean(sval);
            
            final String defaultPropertiesFilename = 
                    "META-INF/properties/authsvcdefaults.properties";            
            final String propertiesFilename = this.productionMode ? 
                    "META-INF/properties/authsvc.properties" :
                    "META-INF/properties/authsvcdevmode.properties";            
            
            this.init(defaultPropertiesFilename, propertiesFilename);
            
            final String baseURL = this.getConfiguration().getString(ConfigNames.BASE_URL);
            this.servletContext.setAttribute(ConfigNames.BASE_URL, Objects.requireNonNull(baseURL));
            
        }catch(MalformedURLException e) {
            
            throw new ServletException(e);
        }
    }
    
    public String getAppName() {
        return this.getConfiguration().getString(ConfigNames.APP_NAME);
    }
    
    public String getRegistrationMailResponseServletPath(AuthHandler handler) {
        final String baseURL = this.getConfiguration().getString(ConfigNames.BASE_URL);
        StringBuilder builder = new StringBuilder();
        builder.append(baseURL).append(this.getServletContext().getContextPath());
        builder.append("/").append(this.getRegistrationMailResponseHandlerClass(handler).getSimpleName().toLowerCase());
        return builder.toString();
    }
    
    private Class getRegistrationMailResponseHandlerClass(AuthHandler handler) {
        Class entityClass = handler.getEntityClass();
        if(entityClass == App.class) {
            return Activateapp.class;
        }else if(entityClass == Appuser.class) {
            return Activateuser.class;
        }else{
            throw new UnsupportedOperationException("Unexpected entity class: "+entityClass.getName());            
        }
    }
    
    public ServletContext getServletContext() {
        return servletContext;
    }
}
