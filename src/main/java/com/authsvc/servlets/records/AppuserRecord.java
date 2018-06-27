package com.authsvc.servlets.records;

import com.authsvc.handlers.RequestParameters;
import com.authsvc.pu.Columns;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)NewuserRecord.java   25-Nov-2014 00:13:34
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
public class AppuserRecord extends RequestRecord {
    private transient static final Logger LOG = Logger.getLogger(AppuserRecord.class.getName());
    
    public AppuserRecord() { }
    
    public AppuserRecord(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    protected String getColumnName(String paramValue) {
        final String lc = paramValue.toLowerCase();
        String columnName;
        if(lc.startsWith("email")) {
            return Columns.Appuser.emailaddress.name();
        }else if(lc.equals("password") || lc.equals("pass")) {
            return Columns.Appuser.password.name();
        }else if(lc.equals("user") || lc.equals("username")) {
            return Columns.Appuser.username.name();
        }else if(lc.equals("appid")) {
            return Columns.Appuser.appid.name();
        }else if(lc.equals("appuserid")) {
            return Columns.Appuser.appuserid.name();
        }else if(lc.equals("userstatus") || lc.contains("status")) {
            return Columns.Appuser.userstatus.name();
        }else if(lc.equals("token")) {
            return Columns.Usertoken.token.name();
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
