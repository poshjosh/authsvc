package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)Editappstatus.java   26-Dec-2014 12:06:50
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
public class Editappstatus extends EditStatusHandler<App> {

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
            Columns.App.password.name(),
            Columns.App.userstatus.name()
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
    public String [] getSearchColumnNames() {
        String [] columnNames = {
            Columns.App.emailaddress.name(),
            Columns.App.password.name()
        };
        return columnNames;
    }
    
    /**
     * @param parameters
     * @throws ValidationException 
     */
    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException {
        
        super.validate(parameters);
        
        this.validateUser(null);
    }
    
    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }

    @Override
    protected String getStatusColumnName() {
        return Columns.App.userstatus.name();
    }
}


