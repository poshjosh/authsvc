package com.authsvc.auth;


/**
 * @(#)RequesToken.java   25-Nov-2014 12:51:22
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
public interface SecretToken {
    
    String getSeriesId();
    
    String getToken();
    
    void setToken(String s);
    
    String getSecret();
}
