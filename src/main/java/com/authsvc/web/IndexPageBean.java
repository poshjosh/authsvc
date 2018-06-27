package com.authsvc.web;

import com.authsvc.handlers.BaseHandler;
import java.io.Serializable;


/**
 * @(#)IndexPageBean.java   16-Jan-2015 01:52:55
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
public class IndexPageBean implements Serializable {

    private String columnName;
    
    private BaseHandler requestHandler;
    
    public IndexPageBean() { }
    
    public String getServletPath() {
        return requestHandler.getClass().getSimpleName().toLowerCase();
    }

    public String [] getInputNames() {
        return requestHandler.getInputNames();
    }
    
    public String [] getOutputNames() {
        return requestHandler.getOutputNames();
    }
    
    public String [] getAlternateColumnNames() {
        if(this.columnName == null) {
            throw new NullPointerException();
        }
        return requestHandler.getAlternateColumnNames(columnName);
    }
    
    public int getValidatorType() {
        if(this.columnName == null) {
            throw new NullPointerException();
        }
        return requestHandler.getValidatorType(columnName);
    }
    
    public boolean isColumnNullable() {
        if(this.columnName == null) {
            throw new NullPointerException();
        }
        return requestHandler.isColumnNullable(columnName);
    }
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public BaseHandler getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(BaseHandler requestHandler) {
        this.requestHandler = requestHandler;
    }
}
