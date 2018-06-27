package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.SecretToken;
import com.authsvc.pu.Columns;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


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

    private transient static final Logger LOG = Logger.getLogger(AbstractAuthenticationHandler.class.getName());

    protected abstract SecretToken doExecute(AuthSettings<U> settings) 
            throws AuthException;

    @Override
    protected Map<String, String> execute(
            AuthSettings<U> settings) 
            throws AuthException {

        SecretToken secretToken = this.doExecute(settings);
        
        LOG.log(Level.FINER, "Secret token is null: {0}", secretToken == null);
        
        if(secretToken == null) {
            
            throw new AuthException("Invalid request");
        }
        
        Map<String, String> response = Collections.singletonMap(
                                     Columns.Apptoken.token.name(), 
                                     secretToken.getSecret());
        
        LOG.log(Level.FINER, "Success message: {0}", response);
        
        return response;
    }
}



