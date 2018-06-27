package com.authsvc.handlers;

import com.authsvc.AuthException;


/**
 * @(#)LoginHandler.java   27-Dec-2014 22:52:20
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
public abstract class LoginHandler<U> extends BaseHandler<U, Object> {

    @Override
    protected Object execute(AuthSettings<U> settings) 
            throws AuthException {
        
        if(this.getUser() == null) {
            
            throw new AuthException("Invalid login credentials");
        }
        
        return Boolean.TRUE;
    }
}
