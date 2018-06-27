package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.Appuser;


/**
 * @(#)Getuser.java   24-Jan-2015 02:39:56
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
public class Getuser extends GetHandler<Appuser> {

    @Override
    protected EditStatusHandler<Appuser> getEditStatusHandler() {
        return new Edituserstatus();
    }

    @Override
    public String[] getSearchColumnNames() {
// Search for the user with out using the password. 
// This is done because a new user may be created        
        String [] columnNames = {
            Columns.Appuser.appid.name(), 
            Columns.App.emailaddress.name()};
        return columnNames;
    }

    @Override
    protected LoginHandler<Appuser> getLoginHandler() {
        return new Loginuser();
    }

    @Override
    protected CreationHandler<Appuser> getCreationHandler() {
        return new Createuser();
    }
}
