package com.authsvc.servlets.request;

import com.authsvc.auth.SimpleToken;
import javax.servlet.http.Cookie;


/**
 * @(#)CookieToken.java   25-Nov-2014 13:31:39
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * Use this class when the secret token is present in a cookie in the 
 * request, otherwise use {@link com.authsvc.servlets.request.RequestToken}
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public final class CookieToken extends SimpleToken {

    private Cookie cookie;
    
    public CookieToken(Cookie cookie) {
        CookieToken.this.setCookie(cookie);
    }

    @Override
    public void setSecret(String secret) {
        super.setSecret(secret);
        this.cookie.setValue(secret);
    }

    @Override
    public String getSecret() {
        return super.getSecret(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setToken(String tokenStr) {
        
        super.setToken(tokenStr);
        
        this.cookie.setValue(this.getSecret());
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        
        this.cookie = cookie;
        
        final String cookieValue = cookie.getValue();
        
        this.setSecret(cookieValue);
    }
}
