package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;



/**
 * @(#)Getapp.java   24-Jan-2015 01:29:58
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
public class Getapp extends GetHandler<App> {

    @Override
    protected EditStatusHandler<App> getEditStatusHandler() {
        return new Editappstatus();
    }
    
    @Override
    public String[] getSearchColumnNames() {
// Search for the user with out using the password. 
// This is done because a new user may be created        
        String [] columnNames = {Columns.App.emailaddress.name()};
        return columnNames;
    }

    @Override
    protected LoginHandler<App> getLoginHandler() {
        return new Loginapp();
    }

    @Override
    protected CreationHandler<App> getCreationHandler() {
        return new Createapp();
    }
}
