package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.SecretToken;
import com.authsvc.pu.Columns;
import com.bc.util.XLogger;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;


/**
 * @(#)AbstractAuthenticationHandler.java   27-Dec-2014 22:41:24
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
public abstract class AbstractAuthenticationHandler<U, T> 
        extends BaseHandler<U, Map<String, String>> {

    protected abstract SecretToken doExecute(AuthSettings<U> settings) 
            throws AuthException;

    @Override
    protected Map<String, String> execute(
            AuthSettings<U> settings) 
            throws AuthException {

        SecretToken secretToken = this.doExecute(settings);
        
XLogger.getInstance().log(Level.FINER, "Secret token: {0}", this.getClass(), secretToken);
        
        if(secretToken == null) {
            
            throw new AuthException("Invalid request");
        }
        
        Map<String, String> response = Collections.singletonMap(
                                     Columns.Apptoken.token.name(), 
                                     secretToken.getSecret());
XLogger.getInstance().log(Level.FINER, "Success message: {0}", this.getClass(), response);
        return response;
    }
}



