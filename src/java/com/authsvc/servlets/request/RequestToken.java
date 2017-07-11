package com.authsvc.servlets.request;

import com.authsvc.auth.SimpleToken;
import com.authsvc.pu.Columns;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)AbstractRequestToken.java   25-Nov-2014 12:52:05
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * Use this class when the secret token is present as a parameter in the 
 * request, otherwise if the secret token is present in a cookie use 
 * {@link com.authsvc.servlets.request.CookieToken}
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public final class RequestToken extends SimpleToken {

    public RequestToken() { }

    public RequestToken(HttpServletRequest request) {
        RequestToken.this.setRequest(request);
    }
    
    public void setRequest(HttpServletRequest request) {
    
        String secret = request.getParameter(Columns.Apptoken.token.name());
        
        this.setSecret(secret);
    }
}
