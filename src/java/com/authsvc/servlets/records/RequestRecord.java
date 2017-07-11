package com.authsvc.servlets.records;

import com.authsvc.pu.Columns;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)NewuserRecord.java   25-Nov-2014 00:11:54
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
public class RequestRecord extends HashMap<String, Object>{

    public RequestRecord() { }
    
    public RequestRecord(HttpServletRequest request) {
        RequestRecord.this.setRequest(request);
    }
    
    protected String getColumnName(String paramValue) {
        return paramValue;
    }

    public void setRequest(HttpServletRequest request) {
        
        Enumeration<String> en = request.getParameterNames();
        while(en.hasMoreElements()) {
            String name = en.nextElement();
            String [] values = request.getParameterValues(name);
            if(values == null || values.length == 0) {
                continue;
            }
            String columnName = this.getColumnName(name);
            if(columnName != null) {
                this.put(columnName, values[0]);
            }
        }
        
        en = request.getAttributeNames();
        while(en.hasMoreElements()) {
            String name = en.nextElement();
            Object value = request.getAttribute(name);
            if(value == null) {
                continue;
            }
            String columnName = this.getColumnName(name);
            if(columnName != null) {
                this.put(columnName, value);
            }
        }
    }
    
    @Override
    public Object put(String name, Object value) {
        if(name.equals(Columns.App.appid.name()) || 
                name.equals(Columns.Appuser.appuserid.name())) {
            try{
                value = Integer.parseInt(value.toString());
            }catch(Exception ignored) { }
        }else if(name.equals(Columns.App.userstatus.name())) {
            try{
                value = Short.parseShort(value.toString());
            }catch(Exception ignored) { }
        }
        return super.put(name, value);
    }

}
