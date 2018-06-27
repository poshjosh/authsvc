package com.authsvc.auth;

import com.authsvc.web.WebApp;
import com.bc.security.SecurityTool;
import java.security.GeneralSecurityException;


/**
 * @(#)SimpleToken.java   25-Nov-2014 23:01:51
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
public class SimpleToken implements SecretToken {

    private String seriesId;
    
    private String token;
    
    public SimpleToken() { }

    public SimpleToken(String secret) { 
        SimpleToken.this.setSecret(secret);
    }
    
    public SimpleToken(String seriesId, String token) { 
        this.seriesId = seriesId;
        this.token = token;
    }

    @Override
    public String getSecret() {
        try{
            return new SecurityTool().encryptCookieValues(
                    WebApp.getInstance().getEncryption(), seriesId, token);
        }catch(GeneralSecurityException e){
            throw new RuntimeException(e);
        }
    }
    
    public void setSecret(String secret) {
    
//        System.out.println("Secret: " + secret + ". @" + this.getClass());

        if(secret != null) {

            try{

                String [] pair = new SecurityTool().decryptCookieValues(
                        WebApp.getInstance().getEncryption(), secret);

                this.seriesId = pair[0];

                this.token = pair[1];
                
//XLogger.getInstance().log(Level.INFO, "Series id: {0}, token: {1}", this.getClass(), seriesId, token);
                
            }catch(GeneralSecurityException e){

                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public String getSeriesId() {
        return seriesId;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }
}
