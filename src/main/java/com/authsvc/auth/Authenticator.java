package com.authsvc.auth;

import com.authsvc.AuthException;
import com.authsvc.pu.Columns;
import com.authsvc.web.WebApp;
import com.bc.jpa.controller.EntityController;
import com.bc.security.SecurityTool;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import com.bc.jpa.EntityMemberAccess;

/**
 * @(#)Authenticator.java   25-Nov-2014 09:46:52
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <U> The entity type (typically user record) to be authenticated
 * @param <T> The entity type (typically token record) for which the type
 * <tt>&lt;U&gt;</tt> will be authenticated.
 */
public abstract class Authenticator<U, T> {

    private transient static final Logger logger = Logger.getLogger(Authenticator.class.getName());

    public Authenticator() { }

    public abstract Class<U> getUserEntityClass();
    
    public abstract Class<T> getTokenEntityClass();

    protected void postUpdate(SecretToken secretToken, boolean enable) {  }

    protected boolean update(
            T tokenEntity, SecretToken tokenInput, String newTokenStr) 
            throws AuthException {
        
        if(tokenEntity != null) {
            
            final EntityMemberAccess updater = this.getTokenController();
            
            final String tokenCol = Columns.Apptoken.token.name();
            final String seriesCol = Columns.Apptoken.seriesid.name();
            
            final boolean validToken = tokenInput.getToken().equals(updater.getValue(tokenEntity, tokenCol));
            
            final boolean validSeriesId = tokenInput.getSeriesId().equals(updater.getValue(tokenEntity, seriesCol));
            
            logger.finer(() -> "Valid token: "+validToken+", seriesId: "+validSeriesId);                

            if(validToken && validSeriesId){
                
                final Function<EntityManager, Optional<T>> editToken = (em) -> {
                    T found = null;
                    try{
                        final Object tokenEntityId = updater.getId(tokenEntity);
                        found = em.find(this.getTokenEntityClass(), tokenEntityId);
                        updater.update(tokenEntity, found, false);
                        updater.setValue(found, tokenCol, newTokenStr);
                        
                        found = em.merge(found);
                        
                    }catch(RuntimeException e) {
                        logger.log(Level.WARNING, "Unexpected exception", e);
                    }
                    return Optional.ofNullable(found);
                };

                final EntityManager em = WebApp.getInstance().getJpaContext()
                        .getEntityManager(this.getTokenEntityClass());
                WebApp.getInstance().getJpaContext().executeTransaction(
                        em, editToken).orElseThrow(() -> 
                        new AuthException("Database error while trying to authenticate. Please try again later"));

                return true;

            }else{
                throw new AuthException("Access violation");
            }
        }else{
            
            return false;
        }
    }

    /**
     * Tries to login the user using the <tt>Remember me</tt> cookie if available. If the
     * operation is successful adds the cookie to the response.
     * @param secretToken
     * @return SecretToken. if the user was successfully authenticated or null if not.
     * @throws com.authsvc.AuthException
     */
    public SecretToken authenticate(SecretToken secretToken) 
            throws AuthException {

        SecurityTool securityTool = new SecurityTool();
        
        if(secretToken == null) {
            return null;
        }

        final String newToken = securityTool.getRandomUUID(16);
        
        boolean success = authenticate(secretToken, newToken);
        
        this.postUpdate(secretToken, success);
        
        return secretToken;
    }
    
    private boolean authenticate(
            SecretToken tokenInput, 
            String newTokenStr) 
            throws AuthException {
        
        // Get the seriesId and token from the database 
        // If none is found return false
        // If both are found postUpdate the token with the newToken
        // Extract the user's details using the emailAddress 
        // Login the user with the emailAddress
        //
        EntityController<T, ?> ec = this.getTokenController();
        
        Map<String, Object> where = new HashMap<>(2, 1.0f);
        where.put(Columns.Usertoken.seriesid.name(), tokenInput.getSeriesId());
        where.put(Columns.Usertoken.token.name(), tokenInput.getToken());
        
        List<T> found = ec.select(where, null, -1, -1);
        
        T tokenEntity;
        
        if(found == null || found.isEmpty()) {
            tokenEntity = null;
        }else if(found.size() == 1){
            tokenEntity = found.get(0);
        }else{
            throw new UnsupportedOperationException("Found > 1 record where 1 or less was expected for values: "+where);
        }

        boolean success;
        
        if(tokenEntity != null) {
            
            success = this.update(tokenEntity, tokenInput, newTokenStr);
            
        }else{
            
            success = false;
        }
        
        if(success) {
            
            tokenInput.setToken(newTokenStr);
        }
        
        return success;
    }

    /**
     * Creates a new secret token for the user, to be used by the
     * user for authentication
     * @param mUser
     * @return
     * @throws AuthException 
     */
    public SecretToken rememberUser(U mUser) 
            throws AuthException {
        
        if(mUser == null) {
            throw new NullPointerException();
        }
        
        String seriesId = UUID.randomUUID().toString();
        String tokenStr = UUID.randomUUID().toString();
        
        // Before we insert the user's 'remember me' record, we attempt to delete 
        // the record incase it exists, even though it's not supposed to.
        deleteAuthUserToken(mUser);
        
        if(insertRecord(tokenStr, seriesId, mUser)) {

            SimpleToken secretToken = new SimpleToken(seriesId, tokenStr);
            
            this.postUpdate(secretToken, true);
            
            return secretToken;
            
        }else{
            
            return null;
        }
    }
    
    public void forgetUser(U mUser, SecretToken secretToken) 
            throws AuthException {
        
        if(secretToken != null) {
           
            this.postUpdate(secretToken, false);
            
            if(mUser != null) {
                
                this.deleteAuthUserToken(mUser);
                
            }else{

                deleteSeries(secretToken.getSeriesId());
            }     
        }
    }

    private boolean insertRecord(
            String tokenStr, 
            String seriesId, 
            U authuser) {
        
        EntityController<T, ?> ec = this.getTokenController();
        
        String referenceColumn = WebApp.getInstance().getJpaContext().getMetaData()
                .getReferenceColumn(this.getUserEntityClass(), this.getTokenEntityClass());
        
        LinkedHashMap parameters = new LinkedHashMap(4, 1.0f);
        parameters.put(Columns.Usertoken.token.name(), tokenStr);
        parameters.put(Columns.Usertoken.seriesid.name(), seriesId);
        parameters.put(referenceColumn, authuser);
        parameters.put(Columns.Usertoken.datecreated.name(), new Date());
        
        try{
            
            return ec.insert(parameters) == 1;
            
        }catch(Exception e) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Failed to create record for table: "+ec.getTableName()+" using parameters: "+parameters, e);
            
            return false;
        }
    }

    private int deleteAuthUserToken(U authuser) {
        
        String idColumnName = this.getUserController().getIdColumnName();
        
        EntityController<T, ?> ec = this.getTokenController();
        
        int updated = ec.delete(idColumnName, authuser);
            
        return updated;
    }

    private int deleteSeries(String seriesId) {
        
        EntityController<T, ?> ec = this.getTokenController();
        
        int updated = ec.delete(Columns.Usertoken.seriesid.name(), seriesId);
            
        return updated;
    }
    
    protected EntityController<U, ?> getUserController() {
        return WebApp.getInstance().getJpaContext().getEntityController(this.getUserEntityClass());
    }
    
    protected EntityController<T, ?> getTokenController() {
        return WebApp.getInstance().getJpaContext().getEntityController(this.getTokenEntityClass());
    }
}
