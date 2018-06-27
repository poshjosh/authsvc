package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.web.WebApp;
import com.bc.validators.ValidationException;
import com.bc.jpa.controller.EntityController;
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
import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;
import com.bc.functions.GetSingle;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;


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

    private transient static final Logger LOG = Logger.getLogger(Requestpassword.class.getName());

    @Override
    protected Boolean execute(AuthSettings<U> settings) throws AuthException, ValidationException, IOException {
        
        final Map<String, Object> parameters = settings.getParameters();

        final Map<String, Object> where = this.getDatabaseFormat(parameters);

        if(LOG.isLoggable(Level.FINER)) {
            final Map view = new HashMap(where);
            view.replace("password", "[PASSWORD]");
            LOG.log(Level.FINER, "Parameters: {0}", view);
        }
        
        final EntityController<U, Integer> ec = this.getEntityController();
        
        final List<U> found = ec.select(where, "AND"); 
        
        final U user = new GetSingle<U>().apply(found);
        
        final Map map = ec.toMap(user);

        final String recipient =(String)map.get(Columns.Appuser.emailaddress.name());
        final String password = (String)map.get(Columns.Appuser.password.name());
        try{

            final SimpleEmail email = new SimpleEmail();
            email.setSubject("Here is the password you requested for");
            email.setMsg("Your password: "+ new String(WebApp.getInstance().getEncryption().decrypt(password)));

            final Entry<String, String> senderEmailAndPass = this.getSenderEmailAndPassword(parameters);

            EmailBuilder emailBuilder = new EmailBuilderImpl(WebApp.getInstance().getMailConfig());
            emailBuilder.build(email, senderEmailAndPass.getKey(), senderEmailAndPass.getValue(), true);

            email.addTo(recipient);

            email.send();

            return Boolean.TRUE;

        }catch(GeneralSecurityException | EmailException e) {
            throw new AuthException("Error sending password email", e);
        }
    }
    
    public Entry<String, String> getSenderEmailAndPassword(Map<String, Object> parameters) {
        String senderEmail = null;
        String senderPass = null;
        final App app;
//@todo allow for sending of email from apps email address by making the 
//email access immune to the 'less secure apps' complain from email servers
//
//        app = this.getAppOrDefault(parameters, null);
        app = null;
        if(app != null) {
            try{
                senderPass = app.getPassword() == null ? null : new String(WebApp.getInstance().getEncryption().decrypt(app.getPassword()));
                senderEmail = app.getEmailaddress();
                LOG.log(Level.FINE, "Sender email from app: {0}", senderEmail);
            }catch(GeneralSecurityException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
        if(senderEmail == null || senderPass == null) {
            final Configuration config = WebApp.getInstance().getConfiguration();
            senderEmail = (String)parameters.get("sender_"+Columns.Appuser.emailaddress.name());
            if(senderEmail == null) {
                senderEmail = config.getString(ConfigNames.EMAIL_ADDRESS);
            }
            senderPass = (String)parameters.get("sender_"+Columns.Appuser.password.name()); 
            if(senderPass == null) {
                senderPass = config.getString(ConfigNames.EMAIL_PASSWORD);
            }
        }
        final Entry<String, String> output = new HashMap.SimpleImmutableEntry<>(senderEmail, senderPass);
        LOG.fine(() -> "Sender email: " + output.getKey());
        return output;
    }
    
    public App getAppOrDefault(Map<String, Object> params, App outputIfNone) {
        final Integer id = (Integer)params.get(Columns.App.appid.name());
        final App app = id == null ? null : this.getEntityController(App.class).find(id);
        return app == null ? outputIfNone : app;
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
