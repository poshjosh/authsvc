package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.mail.EmailActivationNames;
import com.authsvc.pu.Columns;
import com.authsvc.pu.Columns.App;
import com.authsvc.pu.AuthSvcJpaContext;
import com.authsvc.pu.entities.Appuser;
import com.authsvc.pu.entities.Userstatus;
import com.authsvc.web.WebApp;
import com.bc.util.XLogger;
import com.bc.validators.ValidationException;
import com.bc.jpa.controller.EntityController;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import com.bc.jpa.context.JpaContext;


/**
 * @(#)ActivationHandler.java   26-Dec-2014 12:18:14
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U> The entity type of the user to activate
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class ActivationHandler<U> extends BaseHandler<U, Object> {

    @Override
    public boolean isToBeDecrypted(String columnName) {
        EmailActivationNames<U> settings = this.getEmailActivationNames();
        return settings.isEncrypted(columnName);
    }
    
    @Override
    public String[] getInputNames() {
        return this.getEmailActivationNames().getColumnNames();
    }

    @Override
    public String[] getOutputNames() {
        return null;
    }

    protected String getStatusColumnName() {
        if(getEntityClass() == App.class) { 
            return Columns.App.userstatus.name();
        }else if (getEntityClass() == Appuser.class) {
            return Columns.Appuser.userstatus.name();
        }else{
            throw new UnsupportedOperationException("Unexpected entity class: "+this.getEntityClass().getName());
        }
    }

    @Override
    public void validate(Map<String, Object> parameters) throws ValidationException {

        super.validate(parameters);

        this.validateUser(AuthSvcJpaContext.userstatus.Unactivated);
    }
    
    @Override
    protected Object execute(AuthSettings<U> settings) 
            throws AuthException {

        JpaContext factory = WebApp.getInstance().getJpaContext();
        
        EntityController<U, ?> ec = factory.getEntityController(this.getEntityClass());

        Map<String, Object> where = this.getDatabaseFormat(settings.getParameters());
        
        final Userstatus userstatus = this.getUserstatus(settings, AuthSvcJpaContext.userstatus.Activated);
        
        final String statusCol = this.getStatusColumnName();

        Map update = Collections.singletonMap(statusCol, userstatus.getUserstatusid());
            
        try{
            
XLogger.getInstance().log(Level.FINER, "Where: {0}\nUpdate: {1}", 
        this.getClass(), where, update);

            final long count = ec.count(where);
            
            if(count == 0) {
                throw new RuntimeException("Not found. Record with values: "+where);
            }else if(count == 1) {
                ec.update(where, update);
            }else {
                throw new RuntimeException("Trying to update more than one record, when only one should be updated. Parameters: "+where);
            }
        }catch(Exception e) {

            throw new AuthException("Database error", e);
        }
        
        return Boolean.TRUE;
    }

    private EmailActivationNames<U> eas;
    protected EmailActivationNames<U> getEmailActivationNames() { 
        if(eas == null) {
            eas = new EmailActivationNames(){
                @Override
                public Class<U> getEntityClass() {
                    return ActivationHandler.this.getEntityClass();
                }
            };
        }
        return eas;
    }
}

