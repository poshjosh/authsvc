package com.authsvc;

import com.authsvc.pu.AuthSvcJpaContext;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.bc.jpa.JpaContext;

/**
 * @author Josh
 */
public class AuthSvcAppTest {
    
    public AuthSvcAppTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testAll() throws IOException, URISyntaxException {
        
        JpaContext jpaContext = new AuthSvcJpaContext();
    }
}
