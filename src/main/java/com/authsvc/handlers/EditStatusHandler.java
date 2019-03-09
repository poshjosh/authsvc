package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.pu.Enums.userstatus;
import com.authsvc.pu.Columns;
import com.authsvc.pu.entities.Userstatus;
import com.bc.jpa.dao.JpaObjectFactory;
import java.util.logging.Logger;
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
    
    private transient static final Logger LOG = Logger.getLogger(EditStatusHandler.class.getName());
    
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
        
        short statusId = -1;
        try{
            statusId = ((Integer)oval).shortValue();
        }catch(ClassCastException e) {
            try{
                statusId = Integer.valueOf(oval.toString()).shortValue();
            }catch(NumberFormatException nfe) { }
        }
        
        final Object statusVal;
        final Object output;
        
        if(statusId != -1) {
            
            statusVal = statusId;
            output = this.execute(where, statusId);
            
        }else{
            
            final userstatus statusEnum = userstatus.valueOf(oval.toString());
            
            statusVal = statusEnum;
            
            if(statusEnum != null) {
                
                output = this.execute(where, statusEnum);
                
            }else{
                
                throw new AuthException("Invalid value for: " + statusCol);
            }
        }

        if(LOG.isLoggable(Level.FINER)){
            LOG.log(Level.FINER, "Status. input: {0}, outptu: {1}",
                    new Object[]{oval, statusVal});
        }

        return output;
    }

    public Object execute(Map<String, Object> where, short statusId) throws AuthException {
        final JpaObjectFactory puContext = this.getJpaObjectFactory();
        final Userstatus userstatus = puContext.getDaoForSelect(Userstatus.class).findAndClose(statusId);
        return this.execute(where, userstatus);
    }
    
    public Object execute(Map<String, Object> where, com.authsvc.pu.Enums.userstatus statusEnum) throws AuthException {
        final JpaObjectFactory puContext = this.getJpaObjectFactory();
        final Userstatus userstatus = puContext.getDaoForSelect(Userstatus.class)
                .where(Columns.App.userstatus.name(), statusEnum.name())
                .getSingleResultAndClose();
        return this.execute(where, userstatus);
    }

    public Object execute(Map<String, Object> where, Userstatus userstatus) throws AuthException {
        
        final String statusCol = this.getStatusColumnName();
            
        try{
            
            if(LOG.isLoggable(Level.FINER)){
                LOG.log(Level.FINER, "Where: {0}\nUpdate: {1} = {1}", 
                        new Object[]{where, statusCol, userstatus});
            }

            final JpaObjectFactory puContext = this.getJpaObjectFactory();
            
            final Long count = puContext.getDaoForSelect(Long.class)
                    .from(this.getEntityClass())
                    .where(where)
                    .count()
                    .getSingleResultAndClose();
            
            if(count == 0) {
                throw new RuntimeException("Not found. Record with values: "+where);
            }else if(count == 1) {
                final int updateCount = puContext.getDaoForUpdate(this.getEntityClass())
                        .where(where)
                        .set(statusCol, userstatus)
                        .executeUpdateCommitAndClose();
                if(updateCount < 1) {
                    throw new AuthException("Update failed");
                }
                assert (updateCount == 1) : "Updated " + updateCount + 
                        " records, when only one should be updated. Parameters: " + where + 
                        ", update: " + statusCol + '=' + userstatus;
            }else{
                throw new RuntimeException("Found " + count + 
                        " records, when only one was expected. entity type: " + 
                        this.getEntityClass().getName() + ", parameters: " + where);
            }
            
        }catch(Exception e) {

            throw new AuthException("Database error", e);
        }
        
        return Boolean.TRUE;
    }
}


