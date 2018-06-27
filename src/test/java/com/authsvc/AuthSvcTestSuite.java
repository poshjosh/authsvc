package com.authsvc;

import com.authsvc.handlers.RequestuserpasswordTest;
import com.authsvc.mail.RegistrationMailTest;
import com.authsvc.servlets.ActivateuserTest;
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
@Suite.SuiteClasses({
    RequestuserpasswordTest.class, 
    RegistrationMailTest.class, 
    ActivateuserTest.class
})
public class AuthSvcTestSuite {
    
    public AuthSvcTestSuite() { }

    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() {  }
    
    @After
    public void tearDown() { }
}
