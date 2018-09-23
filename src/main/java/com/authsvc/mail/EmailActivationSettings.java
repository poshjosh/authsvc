package com.authsvc.mail;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;
import com.bc.jpa.dao.JpaObjectFactory;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;


/**
 * @(#)EmailActivationToken.java   28-Nov-2014 00:05:21
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U> The type of the user
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class EmailActivationSettings<U> extends EmailActivationNames<U> {
    
    private transient static final Logger LOG = Logger.getLogger(EmailActivationSettings.class.getName());
    
    private final U user;
    
    private final JpaObjectFactory jpaContext;
    
    public EmailActivationSettings(JpaObjectFactory jpaContext, U user) { 
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.user = Objects.requireNonNull(user);
    }
    
    public Map<String, Object> getMap() {
        
        Map<String, Object> map = new HashMap(this.getColumnNames().length, 1.0f);
        
        for(String column:this.getColumnNames()) {
            
            Object value = this.getValue(column);
            
            if(value == null) {
                throw new NullPointerException();
            }
            
            if(this.isEncrypted(column)) {
                value = this.encrypt(value);
            }
            
            // Authuser.getAppid returns an App
            // However we want to send the appid of the app (i.e an integer in our mail)
            //
            if(column.equals(Columns.Appuser.appid.name())) {
                App app = (App)value;
                value = app.getAppid();
                if(LOG.isLoggable(Level.FINER)){
                    LOG.log(Level.FINER, "For column: {0} converted {1} to {2}",
                            new Object[]{ column,  app,  value});
                }
            }
            
            map.put(column, value);
        }
        
        return map;
    }
    
    public Object getIdValue() {
        return this.getValue(this.getIdColumnName());
    }
    
    public String getEmailAddressValue() {
        return (String)this.getValue(this.getEmailAddressColumnName());
    }

    public Object getUserIdentifierValue() {
        return this.getValue(this.getUserIdentifierColumnName());
    }
    
    public Object getRequestParameterValue() {
        return this.getValue(this.getRequestParameterName());
    }
    
    public Object getValue(String columnName) {
        return jpaContext
                .getEntityMemberAccess(this.getEntityClass())
                .getValue(user, columnName);
    }
    
    @Override
    public Class<U> getEntityClass() {
        return (Class<U>)this.user.getClass();
    }

    public final U getUser() {
        return user;
    }
}
