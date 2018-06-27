package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.Authenticator;
import com.authsvc.auth.SecretToken;


/**
 * @(#)Authenticate.java   26-Nov-2014 01:28:03
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <U> The type of the user entity
 * @param <T> The type of the token entity
 */
public abstract class AuthenticationHandler<U, T> 
    extends AbstractAuthenticationHandler<U, T> {

    @Override
    protected SecretToken doExecute(AuthSettings<U> settings) 
            throws AuthException {
        
        Authenticator<U, ?> authenticator = this.getAuthenticator();
        
        return authenticator.authenticate(settings.getSecretToken());
    }
}


