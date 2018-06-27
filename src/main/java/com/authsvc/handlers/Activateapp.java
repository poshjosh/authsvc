package com.authsvc.handlers;

import com.authsvc.pu.entities.App;


/**
 * @(#)Activateapp.java   27-Nov-2014 23:53:27
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
public class Activateapp extends ActivationHandler<App> {
    @Override
    public Class<App> getEntityClass() {
        return App.class;
    }
}
