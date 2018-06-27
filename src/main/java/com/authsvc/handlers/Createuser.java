package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.Apptoken;
import com.authsvc.pu.entities.Appuser;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)NewUser.java   24-Nov-2014 22:24:08
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
public class Createuser extends CreationHandler<Appuser> {

    @Override
    public String [] getInputNames() {
        String [] columnNames = {
            Columns.Appuser.appid.name(),
            Columns.Apptoken.token.name(),
            Columns.Appuser.emailaddress.name(),
            Columns.Appuser.username.name(),
            Columns.Appuser.password.name()
        };
        return columnNames;
    }
    
    @Override
    public String [] getOutputNames() {
        String [] columnNames = {
            Columns.Appuser.appuserid.name(),
            Columns.Appuser.emailaddress.name(),
            Columns.Appuser.username.name(),
            Columns.Appuser.password.name()
        };
        return columnNames;
    }

    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException { 

        super.validate(parameters);
        
        Map<String, Object> toFind = this.getDatabaseFormat(parameters, new String[]{
            Columns.Appuser.appid.name(),
            Columns.Appuser.emailaddress.name()
        });
        
        Appuser authuser = this.find(Appuser.class, toFind);
                
        if(authuser != null) {
            throw new ValidationException(Columns.Appuser.emailaddress.name()+" already taken");
        }
        
        Object username = parameters.get(Columns.Appuser.username.name());
        
        if(username != null) {
            
            toFind = this.getDatabaseFormat(parameters, new String[]{
                Columns.Appuser.appid.name(),
                Columns.Appuser.username.name()
            });
            
            authuser = this.find(Appuser.class, toFind);

            if(authuser != null) {
                throw new ValidationException(Columns.Appuser.username.name()+" already taken");
            }
        }
        
        // We are validating app token not user token
        //
        this.validateToken(this.getSecretToken(), Apptoken.class, parameters.get(Columns.Apptoken.appid.name()));
    }    
    
    @Override
    public Class<Appuser> getEntityClass() {
        return Appuser.class;
    }
}
