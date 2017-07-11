package com.authsvc.servlets.records;

import com.authsvc.pu.Columns;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)TokenRecord.java   25-Nov-2014 00:46:44
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
public class UsertokenRecord extends RequestRecord {
    
    public UsertokenRecord() { }
    
    public UsertokenRecord(HttpServletRequest request) {
        super(request);
    }

    @Override
    protected String getColumnName(String paramValue) {
        Columns.Usertoken [] columns = this.getColumns();
        for(Columns.Usertoken column:columns) {
            if(column.name().equals(paramValue)) {
                return paramValue;
            }
        }
        return null;
    }
    
    private Columns.Usertoken [] c_acessViaGetter;
    private Columns.Usertoken [] getColumns() {
        if(c_acessViaGetter == null) {
            c_acessViaGetter = Columns.Usertoken.values();
        }
        return c_acessViaGetter;
    }
}
