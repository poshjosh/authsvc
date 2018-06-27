package com.authsvc.handlers;

import com.authsvc.auth.SecretToken;
import java.util.Map;


/**
 * @(#)Settings.java   19-Jan-2015 14:37:33
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface AuthSettings<U> {

    String[] getAlternateColumnNames(String columnName);

    Class<U> getEntityClass();

    String[] getInputNames();

    String[] getOutputNames();

    Map<String, Object> getParameters();
    
    String[] getSearchColumnNames();

    SecretToken getSecretToken();
    
    int getValidatorType(String columnName);

    boolean isColumnNullable(String columnName);

    boolean isToBeDecrypted(String columnName);

    boolean isToBeEncrypted(String columnName);
}
