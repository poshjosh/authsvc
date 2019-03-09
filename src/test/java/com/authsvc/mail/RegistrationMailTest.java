package com.authsvc.mail;

import com.authsvc.AuthSvcWebappSetup;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Appuser;
import com.bc.mail.EmailBuilder;
import com.bc.mail.EmailBuilderImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.junit.Test;
import com.authsvc.ConfigNames;
import com.authsvc.web.WebApp;
import com.bc.jpa.dao.JpaObjectFactory;
import com.bc.mail.config.MailConfig;
import com.bc.webapptest.WebappSetup;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author poshjosh
 */
public class RegistrationMailTest {
    
    private static final WebappSetup webappSetup = new AuthSvcWebappSetup();
    
    public RegistrationMailTest() { }
    
    @BeforeClass
    public static void setUpClass() { 
        webappSetup.contextInitialized();
    }
    
    @AfterClass
    public static void tearDownClass() { 
        webappSetup.contextDestroyed();
    }
    
    @Test
    public void testAll() throws Exception {
        
        System.out.println(this.getClass().getName()+"#testAll");
        
        final String fname = "META-INF/properties/authsvcdefaults.properties";
        
        File file;
        file = new File(webappSetup.getServletContext().getClassLoader().getResource(fname).getFile());        
                
        file = new File(Thread.currentThread().getContextClassLoader().getResource(fname).getFile());

        final Properties props = new Properties();

        try (Reader reader = new InputStreamReader(new FileInputStream(file))) {

            props.load(reader);
        }
        
        System.out.println("Loaded properties: "+props);
        
        final MailConfig mailConfig = WebApp.getInstance().getMailConfig();
        
        final EmailBuilder emailBuilder = new EmailBuilderImpl(mailConfig);
// database = all9janews_newsdb        
// installationid=1, installationkey=abdb33ee-a09e-4d7d-b861-311ee7061325,
// feeduserid=1, screenname=user_2, emailAddress=posh.bc@gmail.com

// database = loosebox_authsvc
// username=poshjosh, appid=2, appuserid=39, emailaddress=posh.bc@gmail.com, password=1kjvdul-

        final Date date = this.getDate();
        
        Appuser appuser = new Appuser(39, "posh.bc@gmail.com", "1kjvdul-", "poshjosh", date, date);
        
        final String app_email = props.getProperty(ConfigNames.EMAIL_ADDRESS);
        final String app_pass = props.getProperty(ConfigNames.EMAIL_PASSWORD);
        
        final String app_name = "Dummy-App-Name";
        
        App app = new App(2, app_email, app_pass, app_name, date, date);
        
        appuser.setAppid(app);
        
        final JpaObjectFactory jpaContext = WebApp.getInstance().getJpaObjectFactory();
        
        final EmailActivationSettings settings = new EmailActivationSettings(jpaContext, appuser); 
        
        RegistrationMail<Appuser> email = new RegistrationMail(
                emailBuilder, 
                app_name,  
                settings,
                "/dummyResponseServletPath"){
            @Override
            public String getSenderPassword() {
                return app_pass;
            }
            @Override
            public String getSenderEmail() {
                return app_email;
            }
        };
        
        email.setDebug(true);

        try{
            email.send();
        }catch(org.apache.commons.mail.EmailException e) {
            System.err.println(e.toString());
        }
    }

    private Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 5, 27, 9, 49, 35); // 27 June 2015 09:49:35
        return cal.getTime();
    }
}
