package com.authsvc.web;

import com.bc.jpa.fk.EnumReferences;
import java.io.Serializable;
import java.util.Map;


/**
 * @(#)UserStatus.java   26-Dec-2014 14:20:31
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
public class ReferenceLoader implements Serializable {
    
    private String enumclass;
    
    private Map mappings;
    
    private final EnumReferences enumReferences;
    
    public ReferenceLoader() {
        enumReferences = WebApp.getInstance().getJpaContext().getEnumReferences();
    }
    
    public Map getMappings() {
        
        if(mappings == null) {
            mappings = enumReferences.getMappings(enumclass);
        }
        
        return mappings;
    }

    public String getEnumclass() {
        return enumclass;
    }

    public void setEnumclass(String enumclass) {
        this.enumclass = enumclass;
        this.mappings = null;
    }
}
