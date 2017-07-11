package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.bc.util.XLogger;
import com.bc.jpa.EntityController;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;


/**
 * @(#)EditStatusHandler.java   26-Dec-2014 11:52:32
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U> The type of the user whose status is to be edited
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class EditStatusHandler<U> extends BaseHandler<U, Object> {
    
    protected abstract String getStatusColumnName();

    @Override
    protected Object execute(AuthSettings<U> settings) 
            throws AuthException {

        // Rather than use only search columns, we use all columns
        // This way the returned map contains status column which
        // we will remove and use for update
        //
        Map<String, Object> where = this.getDatabaseFormat(
                settings.getParameters());

        final String statusCol = this.getStatusColumnName();

        // Notice we remove status from here
        // We will add it to the update values later
        //
        final Object oval = where.remove(statusCol);
        if(oval == null) {
            throw new AuthException("Required: "+statusCol);
        }
        
        Integer ival;
        try{
            ival = (Integer)oval;
        }catch(ClassCastException e) {
            ival = Integer.valueOf(oval.toString());
        }

XLogger.getInstance().log(Level.FINER, 
"Input status before: {0}, after: {1}",
this.getClass(), oval, ival);

        return this.execute(where, ival.shortValue());
    }
    
    public Object execute(Map<String, Object> where, short status) 
            throws AuthException {
        
        
        final String statusCol = this.getStatusColumnName();
        
//        EnumReferences refs = WebApp.getInstance().getControllerFactory().getEnumReferences();

        // We do this to ensure the status exists
//        Userstatus userstatus = (Userstatus)refs.getEntity(statusCol, status);
//XLogger.getInstance().log(Level.FINER, "Int: {0}, entity: {1}",
//this.getClass(), status, userstatus);
        
//        if(userstatus == null) {
//            throw new NullPointerException("Invalid value '"+status+"' for required parameter "+statusCol);
//        }
        
//        Map update = Collections.singletonMap(statusCol, userstatus.getUserstatusid());
        Map update = Collections.singletonMap(statusCol, status);
            
        try{
            
XLogger.getInstance().log(Level.FINER, "Where: {0}\nUpdate: {1}", 
        this.getClass(), where, update);

            EntityController<U, Integer> ec = this.getEntityController();

            final long count = ec.count(where);
            
            if(count == 0) {
                throw new RuntimeException("Not found. Record with values: "+where);
            }else if(count == 1) {
                int updateCount = ec.update(where, update);
                if(updateCount < 1) {
                    throw new AuthException("Update failed");
                }
                assert (updateCount == 1) : "Updated "+updateCount+" records, when only one should be updated. Parameters: "+where+", update: "+update;
            }else{
                throw new RuntimeException("Trying to update more than one record, when only one should be updated. Parameters: "+where);
            }
            
        }catch(Exception e) {

            throw new AuthException("Database error", e);
        }
        
        return Boolean.TRUE;
    }
}


