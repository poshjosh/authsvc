package com.authsvc.handlers;

import com.authsvc.pu.Columns.App;


/**
 * @(#)Requestapppassword.java   27-Mar-2015 18:03:12
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
public class Requestapppassword extends Requestpassword<App> {
    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }
}
