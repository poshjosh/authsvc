package com.authsvc.handlers;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.Appuser;


/**
 * @(#)Requestuserpassword.java   27-Mar-2015 18:02:16
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
public class Requestuserpassword extends Requestpassword<Appuser> {
    @Override
    public Class<Appuser> getEntityClass() {
        return Appuser.class;
    }
    @Override
    public String[] getInputNames() {
        return new String[]{Columns.Appuser.appid.name(), Columns.Appuser.emailaddress.name()};
    }
}
