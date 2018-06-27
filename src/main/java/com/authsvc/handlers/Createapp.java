package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;
import com.bc.validators.ValidationException;
import java.util.Map;


/**
 * @(#)Createapp.java   26-Nov-2014 12:42:51
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
public class Createapp extends CreationHandler<App> {

    @Override
    public String [] getInputNames() {
        String [] columnNames = {
            Columns.App.emailaddress.name(),
            Columns.App.username.name(),
            Columns.App.password.name()
        };
        return columnNames;
    }
    
    @Override
    public String [] getOutputNames() {
        String [] columnNames = {
            Columns.App.appid.name(),
            Columns.App.emailaddress.name(),
            Columns.App.username.name(),
            Columns.App.password.name()
        };
        return columnNames;
    }
    
    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException { 

        super.validate(parameters);

        DatabaseRecord dbfmt = this.getDatabaseFormat();
        
        // Return an entry with the appropriate column name and value
        // for database operations
        Map.Entry<String, Object> entry = dbfmt.getDatabaseValue(
                parameters, Columns.App.emailaddress.name());
        
        App app = this.find(App.class, entry.getKey(), entry.getValue());
        
        if(app != null) {
            throw new ValidationException("Specified "+entry.getKey()+" already taken");
        }

        // Return an entry with the appropriate column name and value
        // for database operations
        entry = dbfmt.getDatabaseValue(parameters, Columns.App.username.name());
        
        if(entry.getValue() != null) {
            
            app = this.find(App.class, entry.getKey(), entry.getValue());

            if(app != null) {
                throw new ValidationException("Specified "+entry.getKey()+" already taken");
            }
        }
    }    

    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }
}

