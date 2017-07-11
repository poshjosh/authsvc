package com.authsvc;


/**
 * @(#)AuthException.java   19-Jan-2015 13:55:44
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
public class AuthException extends Exception {

    /**
     * Creates a new instance of <code>AuthException</code> without detail message.
     */
    public AuthException() { }


    /**
     * Constructs an instance of <code>AuthException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}
