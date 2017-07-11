package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.pu.Columns;
import com.authsvc.web.WebApp;
import com.bc.util.XLogger;
import com.bc.validators.ValidationException;
import com.bc.jpa.EntityController;
import com.bc.mail.EmailBuilder;
import com.bc.mail.EmailBuilderImpl;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import com.authsvc.ConfigNames;


/**
 * @(#)Requestpassword.java   27-Mar-2015 17:25:31
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class Requestpassword<U> extends BaseHandler<U, Boolean> {

    @Override
    protected Boolean execute(AuthSettings<U> settings) throws AuthException, ValidationException, IOException {
        
        Map<String, Object> parameters = settings.getParameters();
XLogger.getInstance().log(Level.FINER, "Parameters: {0}", this.getClass(), parameters);

        Map<String, Object> where = this.getDatabaseFormat(parameters);

XLogger.getInstance().log(Level.FINE, "Where: {0}", this.getClass(), where);
        
        EntityController<U, Integer> ec = this.getEntityController();
        
        List<U> found = ec.select(where, "AND"); 
        
        int count = found == null ? 0 : found.size();
        
        if(count < 1) {
            
            throw new RuntimeException("Not found. Record with values: "+where);
            
        }else if(count == 1) {
            
            U user = found.get(0);
            
            Map map = ec.toMap(user);
            
            String recipient =(String)map.get(Columns.Appuser.emailaddress.name());
            String password = (String)map.get(Columns.Appuser.password.name());
            Configuration config = WebApp.getInstance().getConfiguration();
            try{

                SimpleEmail email = new SimpleEmail();
                email.setSubject("Here is the password you forgot and requested for");
                email.setMsg("Your password: "+ new String(WebApp.getInstance().getEncryption().decrypt(password)));
                
                String senderEmail = (String)parameters.get("sender_"+Columns.Appuser.emailaddress.name());
                if(senderEmail == null) {
                    senderEmail = config.getString(ConfigNames.EMAIL_ADDRESS);
                }
                String senderPass = (String)parameters.get("sender_"+Columns.Appuser.password.name()); 
                if(senderPass == null) {
                    senderPass = config.getString(ConfigNames.EMAIL_PASSWORD);
                }
                
                EmailBuilder emailBuilder = new EmailBuilderImpl(WebApp.getInstance().getMailConfig());
                emailBuilder.build(email, senderEmail, senderPass, true);
                
                email.addTo(recipient);
                
                email.send();
                
                return Boolean.TRUE;
                
            }catch(GeneralSecurityException | EmailException e) {
                throw new AuthException("Error sending password email", e);
            }
        }else {
            throw new RuntimeException("Trying to update more than one record, when only one should be updated. Parameters: "+where);
        }
    }

    @Override
    public String[] getInputNames() {
        return new String[]{Columns.App.emailaddress.name()};
    }

    @Override
    public String[] getOutputNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
