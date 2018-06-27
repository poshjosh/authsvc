package com.authsvc.servlets.records;

import com.authsvc.handlers.RequestParameters;
import com.authsvc.pu.Columns;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)AppRecord.java   26-Nov-2014 01:14:26
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
public class AppRecord extends RequestRecord {
    private transient static final Logger LOG = Logger.getLogger(AppRecord.class.getName());
    
    public AppRecord() { }
    
    public AppRecord(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    protected String getColumnName(String paramValue) {
        final String lc = paramValue.toLowerCase();
        String columnName;
        if(lc.startsWith("email")) {
            return Columns.App.emailaddress.name();
        }else if(lc.equals("password") || lc.equals("pass")) {
            return Columns.App.password.name();
        }else if(lc.equals("user") || lc.equals("username")) {
            return Columns.App.username.name();
        }else if(lc.equals("appid")) {
            return Columns.App.appid.name();
        }else if(lc.equals("userstatus") || lc.contains("status")) {
            return Columns.App.userstatus.name();
        }else if(lc.equals("token")) {
            return Columns.Apptoken.token.name();
        }else if(lc.contains("registration")) {
            return RequestParameters.SEND_REGISTRATION_MAIL;
        }else if(lc.equals("create")) {
            return RequestParameters.CREATE;
        }else{
            columnName = paramValue;
        }
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "param=columnName {0}={1}",new Object[]{ paramValue,  columnName});
}
        return columnName;
    }
}

