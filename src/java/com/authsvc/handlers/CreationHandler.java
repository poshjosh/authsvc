package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.mail.RegistrationMail;
import com.authsvc.pu.Columns;
import com.authsvc.pu.AuthSvcJpaContext.userstatus;
import com.authsvc.web.WebApp;
import com.bc.util.XLogger;
import com.bc.jpa.EntityController;
import com.bc.jpa.fk.EnumReferences;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.mail.internet.InternetAddress;
import org.apache.commons.mail.EmailException;
import com.authsvc.ConfigNames;
import com.authsvc.mail.EmailActivationSettings;
import com.bc.mail.EmailBuilder;
import com.bc.mail.EmailBuilderImpl;
import com.bc.security.SecurityTool;

/**
 * @(#)CreationHandler.java   26-Nov-2014 12:45:26
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U> The entity type of the user to create
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class CreationHandler<U> extends BaseHandler<U, Map> {

    private U createdEntity;

    @Override
    public U getUser() {
        return createdEntity;
    }
    
    @Override
    public String[] getSearchColumnNames() {
        // Since the user is just being created, we can't actually search for the user
        throw new UnsupportedOperationException();
    }
    
    protected Map<String, Object> formatInput(Map<String, Object> input) {
        
        SecurityTool sytool = new SecurityTool();
        
        // Add an auto-generated user name if none was provided
        //
        Object username = input.get(Columns.App.username.name());
        if(username == null) {
            input.put(Columns.App.username.name(), sytool.generateUsername());
        }
        
        // Add an auto-generated password if none was provided
        //
        String password = (String)input.get(Columns.App.password.name());
        if(password == null) {
            int len = WebApp.getInstance().getConfiguration().getInt(ConfigNames.MIN_PASSWORD_LENGTH, 6);
            input.put(Columns.App.password.name(), sytool.getRandomUUID(len));
        }
        
        return input;
    }
    
    protected Map<String, Object> formatOutput(Map<String, Object> output) {
        
        String password = (String)output.get(Columns.App.password.name());
        if(password != null) {
            try{
                char [] decrypted = WebApp.getInstance().getEncryption().decrypt(password);
                output.put(Columns.App.password.name(), new String(decrypted));
            }catch(GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
        
        // Remove all keys that are not contained in the output names
        //
        List<String> names = java.util.Arrays.asList(this.getOutputNames());
        output.keySet().retainAll(names);
        
        return output;
    }
    
    @Override
    protected Map execute(AuthSettings<U> settings) throws AuthException {
        
        Map<String, Object> formattedInput = this.formatInput(this.getParameters());
        
        EntityController<U, Integer> ec = this.getEntityController();

        EnumReferences refs = WebApp.getInstance().getJpaContext().getEnumReferences();
        formattedInput.put(Columns.App.datecreated.name(), new Date());
        Object oval = refs.getId(userstatus.Unactivated);
        Short shval;
        try{
            shval = (Short)oval;
        }catch(ClassCastException e) {
            shval = Short.valueOf(oval.toString());
        }
        if(shval == null) {
            throw new NullPointerException();
        }
        formattedInput.put(Columns.App.userstatus.name(), shval);
        
        Map toCreate = this.getDatabaseFormat(formattedInput);
        
        this.createdEntity = ec.persist(toCreate);

        if(this.createdEntity == null) {

            throw new AuthException("Failed to create user");
        }

XLogger.getInstance().log(Level.FINE, "Successfully created entity: {0}", this.getClass(), createdEntity);

        String paramValue = (String)this.getParameters().get(RequestParameters.SEND_REGISTRATION_MAIL);
        
        boolean sendMail = paramValue == null ? false : Boolean.parseBoolean(paramValue.trim());
        
        if(sendMail) {

            this.sendRegistrationMail();
        }
        
        Map map = ec.toMap(this.createdEntity);
        
        return this.formatOutput(map);
    }
    
    protected void sendRegistrationMail() throws AuthException{
        
        if(createdEntity == null) {
            throw new NullPointerException();
        }
        
        RegistrationMail<U> email = null;
        
        try{
            
            EmailBuilder emailBuilder;
//            emailBuilder = new EmailBuilderImpl(WebApp.getInstance().getMailConfig());
            emailBuilder = new EmailBuilderImpl();
            EmailActivationSettings settings = new EmailActivationSettings(
                    WebApp.getInstance().getJpaContext(), this.getUser());
            final String responsePath = WebApp.getInstance().getRegistrationMailResponseServletPath(this);
            
            email = new RegistrationMail<>(
                    emailBuilder, WebApp.getInstance().getAppName(), 
                    settings, responsePath);
            
            email.setDebug(true);
            
            email.send();
            
        }catch(EmailException e) {
            
            if(email != null) {
                
                List<InternetAddress> to = email.getToAddresses();
                String subject = email.getSubject();
                String htmlMsg = email.getHtmlMsg();
XLogger.getInstance().log(Level.INFO, "================= THE FOLLOWING EMAIL FAILED TO SEND ====================\nTo: {0}\nSubject: {1}\n{2}", 
this.getClass(), to, subject, htmlMsg);
            }
            
            throw new AuthException("Error sending resistration mail", e);
        }
    }
    
    public U getCreatedEntity() {
        return createdEntity;
    }
}
