package com.authsvc;


/**
 * @(#)AppProperties.java   24-Nov-2014 23:00:31
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
public interface ConfigNames {

    String APP_NAME = "appName";
    String BASE_URL = "baseURL";
    String PERSISTENCE_CONFIG_URI = "persistenceConfigURI";
    String EMAIL_ADDRESS = "emailaddress";
    String EMAIL_PASSWORD = "emailpassword";
    String MIN_PASSWORD_LENGTH = "minPasswordLength";
    
    // security
    String ALGORITH = "algorithm";
    String ENCRYPTION_KEY = "encryptionKey";
    
    // remembermeCookie
    String NAME = "name";
    String MAX_AGE = "maxAge";
    String ATTRIBUTE_NAME = "attributeName";
    String RESPONSE_TYPE = "responseType";
}
