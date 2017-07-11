package com.authsvc.handlers;

import com.authsvc.pu.entities.Appuser;


/**
 * @(#)Activateuser.java   26-Dec-2014 11:48:24
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
public class Activateuser extends ActivationHandler<Appuser> {
    @Override
    public Class<Appuser> getEntityClass() {
        return Appuser.class;
    }
}
