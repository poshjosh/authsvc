package com.authsvc;

import com.authsvc.handlers.RequestuserpasswordTest;
import com.authsvc.web.ContextListener;
import com.bc.webapptest.WebappSetup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @(#)LooseboxesTestSuite.java   24-May-2015 22:39:50
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
@RunWith(Suite.class)
//@Suite.SuiteClasses({ActivateuserTest.class})
@Suite.SuiteClasses({RequestuserpasswordTest.class})
public class AuthSvcTestSuite {
    
    private static WebappSetup setup;
    
    public AuthSvcTestSuite() { }

    @BeforeClass
    public static void setUpClass() throws Exception { 
        setup = new WebappSetup(
                new ContextListener(), 
                "http://localhost:8080",
                "C:/Users/Josh/Documents/NetBeansProjects/authsvc/web",
                "/authsvc");
        setup.contextInitialized();
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception { 
        setup.contextDestroyed();
    }
    
    @Before
    public void setUp() throws Exception {  }
    
    @After
    public void tearDown() throws Exception { }

    public static WebappSetup getSetup() {
        return setup;
    }
}
