package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.Appuser;
import com.bc.validators.ValidationException;
import java.util.Map;

/**
 * @(#)Edituserstatus.java   25-Nov-2014 02:33:18
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
public class Edituserstatus extends EditStatusHandler<Appuser> {

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
            Columns.Appuser.appid.name(), 
            Columns.Appuser.emailaddress.name(),
            Columns.Appuser.password.name(),
            Columns.Appuser.userstatus.name()
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
    public String [] getSearchColumnNames() {
        String [] columnNames = {
            Columns.Appuser.appid.name(), 
            Columns.Appuser.emailaddress.name(),
            Columns.Appuser.password.name()
        };
        return columnNames;
    }
    
    
    @Override
    public void validate(Map<String, Object> parameters) throws ValidationException {

        super.validate(parameters);
        
        this.validateUser(null);
    }

    @Override
    public Class<Appuser> getEntityClass() {
        return Appuser.class;
    }

    @Override
    protected String getStatusColumnName() {
        return Columns.Appuser.userstatus.name();
    }
}

