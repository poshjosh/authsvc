package com.authsvc.mail;

import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Appuser;
import com.authsvc.web.WebApp;
import com.bc.security.Encryption;
import java.security.GeneralSecurityException;


/**
 * @(#)EAS.java   24-May-2015 21:20:03
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class EmailActivationNames<T> {

    public EmailActivationNames() { }
    
    public abstract Class<T> getEntityClass();
    
    public boolean isEncrypted(String columnName) {
        return columnName.equals(this.getRequestParameterName());
    }
    
    public String [] getColumnNames() {
        Class type = this.getEntityClass();
        String [] names;
        if(type == App.class) {
            names = new String[] {
                this.getIdColumnName(),
                this.getRequestParameterName()
            };
        }else if(type == Appuser.class) {
            names = new String[] {
                Columns.Appuser.appid.name(),
                this.getIdColumnName(),
                this.getRequestParameterName()
            };
        }else{
            throw new IllegalArgumentException("Unexpected entity: "+type);
        }
        return names;
    }
    
    public String getIdColumnName() {
        Class type = this.getEntityClass();
        if(type == App.class) {
            return Columns.App.appid.name();
        }else if(type == Appuser.class) {
            return Columns.Appuser.appuserid.name();
        }else{
            throw new IllegalArgumentException("Unexpected entity: "+type);
        }
    }
    
    public String getEmailAddressColumnName() {
        Class type = this.getEntityClass();
        if(type == App.class) {
            return Columns.App.emailaddress.name();
        }else if(type == Appuser.class) {
            return Columns.Appuser.emailaddress.name();
        }else{
            throw new IllegalArgumentException("Unexpected entity: "+type);
        }
    }

    public String getUserIdentifierColumnName() {
        Class type = this.getEntityClass();
        if(type  == App.class) {
            return Columns.App.username.name();
        }else if(type == Appuser.class) {
            return Columns.Appuser.username.name();
        }else{
            throw new IllegalArgumentException("Unexpected entity: "+type);
        }
    }
    
    public String getRequestParameterName() {
        return this.getEmailAddressColumnName();
    }
    
    public String encrypt(Object value) {
        try{
            Encryption sy = WebApp.getInstance().getEncryption();
            return sy.encrypt(value.toString().toCharArray());
        }catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String decrypt(Object value) {
        try{
            Encryption sy = WebApp.getInstance().getEncryption();
            return new String(sy.decrypt(value.toString()));
        }catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
