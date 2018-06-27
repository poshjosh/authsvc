package com.authsvc.servlets.request;

import com.authsvc.auth.SecretToken;
import com.authsvc.web.WebApp;
import java.security.GeneralSecurityException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.authsvc.ConfigNames;
import com.bc.security.SecurityTool;


/**
 * @(#)CookieHandler.java   17-Jan-2015 06:53:04
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
public class CookieManager {

    public Cookie getCookie(HttpServletRequest request) {
        
        Cookie [] cookies = request.getCookies();
        
        if(cookies == null) return null;
        
        for(Cookie cookie:cookies) {
            
            final String cookieName = cookie.getName();
            
            if(!cookieName.equals(getCookieName())) continue;
            
            return cookie;
        }
        
        return null;
    }
    
    public void addCookie(HttpServletResponse response, CookieToken secretToken) {
        
        Cookie cookie = secretToken.getCookie();
        
        this.addCookie(response, cookie);
    }
    
    public void addCookie(HttpServletResponse response, Cookie cookie) {
        
        if(cookie != null) {
            
            response.addCookie(cookie);
            
            String cookieResponseType = getCookieResponseType();
            
            if(cookieResponseType != null && !cookieResponseType.trim().isEmpty()) {
                response.setContentType(cookieResponseType);
            }
        }
    }
    
    public Cookie getCookie(SecretToken secretToken, boolean create) {
        Cookie cookie;
        if(secretToken instanceof CookieToken) {
            cookie = ((CookieToken)secretToken).getCookie();
        }else{
            if(create) {
                cookie = createCookie(secretToken.getSeriesId(), secretToken.getToken());
            }else{
                throw new IllegalArgumentException("Type expected: "+CookieToken.class.getName()+", found: "+secretToken.getClass().getName());
            }
        }
        return cookie;
    }
    
    private Cookie createCookie(String seriesId, String token) {
        
        String encryptedData = null;
        try{
            encryptedData = new SecurityTool().encryptCookieValues(
                    WebApp.getInstance().getEncryption(), seriesId, token);
        }catch(GeneralSecurityException e){
            throw new RuntimeException(e);
        }
        
//Logger.getLogger(RememberMe.class.getName()).log(Level.INFO, "{0}. Cookie value: {1}", 
//new Object[]{RememberMe.class.getName(), encryptedData});      
        Cookie remembermeCookie = new Cookie(getCookieName(), encryptedData);
        
        remembermeCookie.setMaxAge(getCookieMaxAge());
        
        remembermeCookie.setPath("/");
        
        return remembermeCookie;
    }
    
    public String getCookieName() {
        return WebApp.getInstance().getConfiguration().getString(ConfigNames.NAME);
    }
    
    public String getCookieAttributeName() {
        return WebApp.getInstance().getConfiguration().getString(ConfigNames.ATTRIBUTE_NAME);
    }
    
    public int getCookieMaxAge() {
        return WebApp.getInstance().getConfiguration().getInt(ConfigNames.MAX_AGE);
    }
    
    public String getCookieResponseType() {
        return WebApp.getInstance().getConfiguration().getString(ConfigNames.RESPONSE_TYPE);
    }
}
