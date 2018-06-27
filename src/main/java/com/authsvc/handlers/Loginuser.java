package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.AuthSvcJpaContext.userstatus;
import com.authsvc.pu.entities.Appuser;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)Loginuser.java   27-Dec-2014 22:50:14
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
public class Loginuser extends LoginHandler<Appuser> {
    
    @Override
    public String [] getInputNames() { 
        // email and password are enough to find an app
        // however we need appid, email and password to find 
        // a user of an app
        String [] columnNames = {
            Columns.Appuser.appid.name(), 
            Columns.Appuser.emailaddress.name(),
            Columns.Appuser.password.name()
        };
        return columnNames;
    }

    @Override
    public String [] getAlternateColumnNames(String columnName) {
        
        if(columnName.equals(Columns.App.emailaddress.name())) {
            
            return new String[]{
                Columns.Appuser.appuserid.name(), 
                Columns.Appuser.username.name()};
        }else{
            return null;
        }
    }
    
    @Override
    public String[] getOutputNames() {
        return null;
    }

    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException {
        
        super.validate(parameters); 

        this.validateUser(userstatus.Activated);
    }
    
    @Override
    public Class<Appuser> getEntityClass() {
        return Appuser.class;
    }
}
