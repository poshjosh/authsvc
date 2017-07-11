package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.SecretToken;
import com.bc.validators.ValidationException;
import java.io.IOException;
import java.util.Map;


/**
 * @(#)RequestHandler.java   24-Nov-2014 22:24:22
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @param <R>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface AuthHandler<U, R> extends AuthSettings<U> {

    U getUser();

    R process(AuthSettings<U> settings) throws AuthException, ValidationException, IOException;

    void validate(Map<String, Object> parameters) throws ValidationException;
    
    void setSecretToken(SecretToken secretToken);

    void setParameters(Map<String, Object> parameters);
}
