package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.AuthSvcJpaContext.userstatus;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Apptoken;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)Authenticateapp.java   26-Nov-2014 01:13:31
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
public class Authenticateapp extends AuthenticationHandler<App, Apptoken> {

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
    public String [] getSearchColumnNames() {
        String [] names = new String[]{
            Columns.App.emailaddress.name()
        };
        return names;
    }
    
    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException {
        
        super.validate(parameters); 
        
        App app = this.validateUser(userstatus.Activated);
        
        validateToken(this.getSecretToken(), Apptoken.class, app.getAppid());
    }
    
    @Override
    public String[] getInputNames() {
        String [] names = new String[]{
            Columns.App.emailaddress.name(),
            Columns.Apptoken.token.name()
        };
        return names;
    }

    @Override
    public String[] getOutputNames() {
        String [] names = new String[]{
            Columns.Apptoken.token.name()
        };
        return names;
    }

    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }
}

