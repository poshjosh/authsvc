package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.bc.validators.ValidationException;
import com.bc.jpa.controller.EntityController;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


/**
 * @(#)GetHandler.java   24-Jan-2015 01:30:44
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class GetHandler<U> extends BaseHandler<U, Map> {

    protected abstract CreationHandler<U> getCreationHandler();

    protected abstract EditStatusHandler<U> getEditStatusHandler();
    
    protected abstract LoginHandler<U> getLoginHandler();
    
    @Override
    public Map process(AuthSettings<U> settings) 
            throws AuthException, ValidationException, IOException{

// We don't validate here because the actual validation is
// done by delegates returned by getXXXHandler methods
//         
//        this.validate(settings.getParameters());
            
        return this.execute(settings);
    } 
    
    @Override
    protected Map execute(AuthSettings<U> settings) 
            throws AuthException, ValidationException, IOException {

        U found = this.findUser();
        
        CreationHandler<U> creationHandler = this.getCreationHandler();
        
        Map out;
        if(found == null) {
            
            if(this.isCreate()) {
                
                creationHandler.updateWith(this);
                
                out = creationHandler.process(settings);
                
                this.editStatus(settings);
                
            }else{
                
                throw new FileNotFoundException("User not found");
            }
        }else{
            
            LoginHandler<U> loginHandler = this.getLoginHandler();

            loginHandler.updateWith(this);
            
            loginHandler.process(settings); 
            
            U user = loginHandler.getUser();
            
            if(user == null) {
                throw new AuthException("Invalid login credentials");
            }
            
            EntityController<U, Integer> ec = this.getEntityController();
            
            Map map = ec.toMap(user, false);
            
            out = creationHandler.formatOutput(map);
            
            this.editStatus(settings);
        }
        
        return out;
    }
    
    private void editStatus(AuthSettings<U> settings) 
            throws AuthException {
        
        EditStatusHandler<U> editStatus = this.getEditStatusHandler();

        editStatus.updateWith(this);

        Map<String, Object> where = this.getDatabaseFormat(
                settings.getParameters(), editStatus.getSearchColumnNames());

        // This throws an exception on failure
        //
        editStatus.execute(where, (short)2); // 2 == Activated
    }
    
    public boolean isCreate() {
        Object oval = this.getParameters().get(RequestParameters.CREATE);
        return oval == null ? false : Boolean.parseBoolean(oval.toString());
    }

    @Override
    public Class<U> getEntityClass() {
        return this.getCreationHandler().getEntityClass();
    }

    @Override
    public String[] getInputNames() {
        // The actual operations are handled by delegate handlers
        // having the correct value for this
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getOutputNames() {
        // The actual operations are handled by delegate handlers
        // having the correct value for this
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
