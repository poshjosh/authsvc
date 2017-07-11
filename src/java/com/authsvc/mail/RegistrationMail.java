package com.authsvc.mail;

import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Appuser;
import com.authsvc.web.WebApp;
import com.bc.mail.EmailBuilder;
import com.bc.mail.EmailBuilderImpl;
import com.bc.util.QueryParametersConverter;
import com.bc.util.XLogger;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import com.authsvc.ConfigNames;

/**
 * @(#)RegistrationMail.java   15-Jan-2015 22:42:09
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @author   chinomso bassey ikwuagwu
 * @version  2.1
 * @since    2.0
 */
public class RegistrationMail<U> extends HtmlEmail {
    
    private final String htmlMsg;
    
    public RegistrationMail(String appName, U user, String responseServletPath) throws EmailException {
        
        this(new EmailBuilderImpl(WebApp.getInstance().getMailConfig()), appName, 
                new EmailActivationSettings(WebApp.getInstance().getJpaContext(), user), responseServletPath);
    }
        
    public RegistrationMail(
            EmailBuilder emailBuilder, String appName, 
            EmailActivationSettings settings, String responseServletPath) throws EmailException {
        
        Object user = settings.getUser();
        
        if(user instanceof Appuser) {
            appName = ((Appuser)user).getAppid().getUsername();
        }else if(user instanceof App) {
//@todo            
        }else{
            throw new UnsupportedOperationException("Unexpected user entity type: "+ user.getClass());
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append("Activate your ").append(appName);
        builder.append(" subscription");
        
        this.setSubject(builder.toString());
        
        builder.setLength(0);
        builder.append("You subscribed to ").append(appName);
        builder.append(". Click <a href=\"");
        builder.append(responseServletPath);
        builder.append('?');
        
        final Map activationLink_queryParameters = settings.getMap();
        QueryParametersConverter queryfmt = new QueryParametersConverter();
        String query = queryfmt.convert(activationLink_queryParameters);
        builder.append(query);
        
        builder.append("\">here</a> to activate your subscription");
        
        this.htmlMsg = builder.toString();
        
        this.setHtmlMsg(htmlMsg);
        
        final String senderEmail = this.getSenderEmail();
        final String senderPass = this.getSenderPassword();
        
        emailBuilder.build(this, senderEmail, senderPass, true);

        this.addTo(settings.getEmailAddressValue());
        
XLogger.getInstance().log(Level.FINER, "Successfully created: {0}", this.getClass(), this);
    }
    
    public String getSenderEmail() {
        final Configuration config = WebApp.getInstance().getConfiguration();
        final String senderEmail = config.getString(ConfigNames.EMAIL_ADDRESS);
        return senderEmail;
    }

    public String getSenderPassword() {
        final Configuration config = WebApp.getInstance().getConfiguration();
        final String senderPassword = config.getString(ConfigNames.EMAIL_PASSWORD);
        return senderPassword;
    }

    public String getHtmlMsg() {
        return htmlMsg;
    }
}

