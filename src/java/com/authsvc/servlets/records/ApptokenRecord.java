package com.authsvc.servlets.records;

import com.authsvc.pu.Columns;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)Tokenrecord.java   26-Nov-2014 00:51:42
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
public class ApptokenRecord extends RequestRecord {
    
    public ApptokenRecord() { }
    
    public ApptokenRecord(HttpServletRequest request) {
        super(request);
    }

    @Override
    protected String getColumnName(String paramValue) {
        Columns.Apptoken [] columns = this.getColumns();
        for(Columns.Apptoken column:columns) {
            if(column.name().equals(paramValue)) {
                return paramValue;
            }
        }
        return null;
    }
    
    private Columns.Apptoken [] c_acessViaGetter;
    private Columns.Apptoken [] getColumns() {
        if(c_acessViaGetter == null) {
            c_acessViaGetter = Columns.Apptoken.values();
        }
        return c_acessViaGetter;
    }
}
