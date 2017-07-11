package com.authsvc.servlets.request;

import com.authsvc.auth.Authenticator;
import com.authsvc.auth.SecretToken;
import javax.servlet.http.Cookie;


/**
 * @(#)CookieAuthenticator.java   19-Jan-2015 17:25:15
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
public abstract class CookieAuthenticator<U, T> extends Authenticator<U, T> {
    
    public CookieAuthenticator() { }
    
    @Override
    protected void postUpdate(SecretToken secretToken, boolean enable) {
        if(!enable) {
            if(secretToken instanceof CookieToken) {
                Cookie cookie = ((CookieToken)secretToken).getCookie();
                cookie.setMaxAge(0); // A zero value causes the cookie to be deleted
            }
        }
    }
}
