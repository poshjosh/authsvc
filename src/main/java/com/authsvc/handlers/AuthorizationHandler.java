package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.SecretToken;


/**
 * @(#)AuthorizationHandler.java   26-Dec-2014 09:32:37
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class AuthorizationHandler<U, T> 
        extends AbstractAuthenticationHandler<U, T> {
    
    @Override
    protected SecretToken doExecute(AuthSettings<U> settings) 
            throws AuthException {
        
        return this.getAuthenticator().rememberUser(this.getUser());
    }
}



