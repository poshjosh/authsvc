package com.authsvc.servlets;

import com.authsvc.AuthSvcWebappSetup;
import com.bc.webapptest.HttpServletRequestImpl;
import com.bc.webapptest.HttpServletResponseImpl;
import com.bc.webapptest.WebappSetup;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh
 */
public abstract class HandlerTest {
    
    private static final WebappSetup webappSetup = new AuthSvcWebappSetup();
    
    public HandlerTest() {  }
    
    public abstract String getUrlPattern();
    
    public abstract Map<String, String> getParameterValues();
    
    @BeforeClass
    public static void setUpClass() { 
        webappSetup.contextInitialized();
    }
    
    @AfterClass
    public static void tearDownClass() { 
        webappSetup.contextDestroyed();
    }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    /**
     * Test of processRequest method, of class AuthSvc.
     */
    @Test
    public void testProcessRequest() {
        
System.out.println(this.getClass().getName()+"#processRequest");

        HttpServletRequest request = this.getRequest();
        
        HttpServletResponse response = new HttpServletResponseImpl();
        
        AuthSvc instance = new AuthSvc();
        
        instance.processRequest(request, response);
    }

    public HttpServletRequest getRequest() {

        HttpServletRequestImpl request = webappSetup.createRequest("/index.jsp", this.getParameterValues(), this.getUrlPattern());
        
        return request;
    }
}
