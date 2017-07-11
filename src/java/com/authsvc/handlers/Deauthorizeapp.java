package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.Authenticator;
import com.authsvc.pu.Columns;
import com.authsvc.pu.AuthSvcJpaContext.userstatus;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Apptoken;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)Deauthorizeapp.java   26-Nov-2014 15:32:17
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
public class Deauthorizeapp extends BaseHandler<App, Object> {

    @Override
    public String[] getOutputNames() {
        return null;
    }

    @Override
    public String [] getInputNames() { 
        // email and password are enough to find an app
        // however we need appid, email and password to find 
        // a user of an app
        String [] columnNames = {
            Columns.App.emailaddress.name(),
            Columns.App.password.name()
        };
        return columnNames;
    }
    
    @Override
    public String [] getAlternateColumnNames(String columnName) {
        
        if(columnName.equals(Columns.App.emailaddress.name())) {
            
            return new String[]{
                Columns.App.appid.name(), 
                Columns.App.username.name()};
        }else{
            return null;
        }
    }
    
    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException {
        
        super.validate(parameters); 
        
        App app = this.validateUser(userstatus.Activated);
        
        this.validateToken(this.getSecretToken(), Apptoken.class, app.getAppid());
    }
    
    @Override
    protected Object execute(AuthSettings<App> settings) 
            throws AuthException {
        
        Authenticator<App, ?> authenticator = this.getAuthenticator();

        authenticator.forgetUser(this.getUser(), settings.getSecretToken());
        
        return Boolean.TRUE;
    }

    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }
}


