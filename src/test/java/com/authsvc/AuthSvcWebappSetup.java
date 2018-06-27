/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.authsvc;

import com.authsvc.web.ContextListener;
import com.bc.webapptest.WebappSetup;
import javax.servlet.ServletContextListener;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 17, 2018 2:05:09 PM
 */
public class AuthSvcWebappSetup extends WebappSetup{
    
    public AuthSvcWebappSetup() {
        super(
            new ContextListener(), 
            "http://localhost:8080",
            "C:/Users/Josh/Documents/NetBeansProjects/authsvc/src/main/webapp",
            "/authsvc"
        );        
    }

    public AuthSvcWebappSetup(ServletContextListener servletContextListener, String baseUrl, String localDir, String contextPath) {
        super(servletContextListener, baseUrl, localDir, contextPath);
    }
}
