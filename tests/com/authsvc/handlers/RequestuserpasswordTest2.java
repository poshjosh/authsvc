package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.bc.validators.ValidationException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh
 */
public class RequestuserpasswordTest2 {
    
    public RequestuserpasswordTest2() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testAll() {
        System.out.println("testAll");
        Requestuserpassword instance = new Requestuserpassword();
        try{
            instance.execute(instance);
        }catch(AuthException | ValidationException | IOException e) {
            e.printStackTrace();
        }
//        Class<Appuser> expResult = null;
//        Class<Appuser> result = instance.getEntityClass();
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
