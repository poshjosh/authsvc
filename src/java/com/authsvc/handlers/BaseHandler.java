package com.authsvc.handlers;

import com.authsvc.AuthException;
import com.authsvc.auth.Authenticator;
import com.authsvc.auth.SecretToken;
import com.authsvc.pu.Columns;
import com.authsvc.pu.AuthSvcJpaContext.userstatus;
import com.authsvc.pu.entities.App;
import com.authsvc.pu.entities.Apptoken;
import com.authsvc.pu.entities.Appuser;
import com.authsvc.pu.entities.Userstatus;
import com.authsvc.pu.entities.Usertoken;
import com.authsvc.web.WebApp;
import com.bc.util.XLogger;
import com.bc.validators.MapValidator;
import com.bc.validators.ValidationException;
import com.bc.validators.Validator;
import com.bc.validators.ValidatorFactory;
import com.bc.jpa.controller.EntityController;
import com.bc.jpa.fk.EnumReferences;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import com.authsvc.ConfigNames;
import com.authsvc.pu.AuthSvcJpaContext;
import com.bc.jpa.context.JpaContext;
import java.util.Objects;


/**
 * @(#)AbstractHandler.java   25-Nov-2014 02:55:04
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <U> The type of the user entity
 * @param <R> The type of the return value of the {@link #execute(com.authsvc.handlers.AuthSettings)} method
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class BaseHandler<U, R> implements AuthHandler<U, R>{

    private U user;
    
    private SecretToken secretToken;
    
    private Map<String, Object> parameters;
    
    protected abstract R execute(AuthSettings<U> settings)
            throws AuthException, ValidationException, IOException;

    @Override
    public R process(AuthSettings<U> settings) 
            throws AuthException, ValidationException, IOException{

        this.validate(settings.getParameters());
            
        return this.execute(settings);
    } 
    
    public void updateWith(BaseHandler update) {
        this.setParameters(update.getParameters());
        this.setSecretToken(update.getSecretToken());
    }

    public void update(BaseHandler toupdate) {
        toupdate.setParameters(this.getParameters());
        toupdate.setSecretToken(this.getSecretToken());
    }
    
    /**
     * These are the column names that will be used for the 
     * <tt>find user operation</tt> via {@link #findUser()}
     * @return The column names to be used by method {@link #findUser()}
     */
    @Override
    public String [] getSearchColumnNames() {
        return this.getInputNames();
    }
    
    @Override
    public boolean isToBeEncrypted(String columnName) {
        // @related encrypt/decrypt password by default
        //
        return columnName.equals(Columns.App.password.name());
    }
    
    @Override
    public boolean isToBeDecrypted(String columnName) {
        return false;
    }
    
    @Override
    public String [] getAlternateColumnNames(String columnName) {
        return null;
    }
    
    @Override
    public boolean isColumnNullable(String columnName) {
        boolean nullable;
        if(columnName.equals(Columns.App.username.name())) {
            nullable = true;
        }else{
            nullable = false;
        }    
        return nullable;
    }
    
    @Override
    public int getValidatorType(String columnName) {
        int validatorType;
        if(columnName.equals(Columns.App.emailaddress.name())) {
            validatorType = ValidatorFactory.EMAIL_CHECK;
        }else if(columnName.equals(Columns.App.username.name())) {
            validatorType = ValidatorFactory.TEXT_CHECK;
        }else if(columnName.equals(Columns.App.password.name())) {
            validatorType = ValidatorFactory.TEXT_CHECK;
        }else{
            validatorType = -1;
        }
        return validatorType;
    }
    
    @Override
    public void validate(Map<String, Object> parameters) 
            throws ValidationException { 

        getValidator().validate(parameters);
    }    

    
    protected U findUser() {

        Class<U> entityClass = this.getEntityClass();
        
        String [] searchColumns = this.getSearchColumnNames();
        
        Map<String, Object> toFind = this.getDatabaseInput(searchColumns);
        
XLogger.getInstance().log(Level.FINER, "Entity class: {0}, Search columns: {1}\nDatabase params: {2}", 
this.getClass(), entityClass.getName(), searchColumns==null?null:Arrays.toString(searchColumns), toFind);
        
        this.user = this.find(entityClass, toFind);
        
        return this.user;
    }
    
    /**
     * @param targetStatus The status of the user to validate. 
     * All userstatuses will be considered if this value is null
     * @throws ValidationException If the request does not contain a valid user
     */
    protected U validateUser(
            userstatus targetStatus) 
            throws ValidationException {

        String key = this.getUserIdentifier();
        
        this.user = this.findUser();
        
        if(user == null) {

            throw new ValidationException("Incorrect details provided by "+key);
            
        }else{
            
            if(targetStatus != null) {
                
                EnumReferences refs = WebApp.getInstance().getJpaContext().getEnumReferences();
                
                String col = Columns.App.userstatus.name();
                
                Userstatus foundEntity = (Userstatus)this.getEntityController().getValue(user, col);
                
XLogger.getInstance().log(Level.FINER, "Fetching enum for: {0}={1}", this.getClass(), col, foundEntity);

                Enum foundStatus = refs.getEnum(Columns.App.userstatus.name(), foundEntity.getUserstatusid());
                
XLogger.getInstance().log(Level.FINER, "Expected: {0}, Found: {1}", this.getClass(), targetStatus, foundStatus);

                if(!foundStatus.equals(targetStatus)) {
                    
                    // NOTE This
                    //
                    user = null;
                    
                    StringBuilder builder = new StringBuilder();
                    builder.append(key).append(" status: ").append(foundStatus);
                    builder.append(". Only ").append(targetStatus).append(' ');
                    builder.append(key).append("s may perform the request operation");
                    throw new ValidationException(builder.toString());
                }
            }
        }
        
        return user;
    }
    
    /**
     * @param secretToken
     * @param entityClass The entity class of the token entity 
     * @param idColumnValue
     * @throws ValidationException If the request does not contain a valid token
     */
    protected void validateToken(
            SecretToken secretToken,
            Class entityClass,
            Object idColumnValue) 
            throws ValidationException {

        String idColumnName;
        String key;
        if(entityClass == Apptoken.class) {
            idColumnName = Columns.Apptoken.appid.name();
            key = "app";
        }else if (entityClass == Usertoken.class){
            idColumnName = Columns.Usertoken.appuserid.name();
            key = "user";
        }else{
            throw new UnsupportedOperationException("Unexpected entity class: "+entityClass.getName());
        }
        
        Map<String, Object> tokenWhere = this.getTokenData(secretToken, idColumnName, idColumnValue);
        
        Object token = this.find(entityClass, this.getDatabaseFormat(tokenWhere));

        if(token == null) {
            throw new ValidationException("Unauthorized " + key);
        }
    }
    
    /**
     * @param secretToken
     * @param idColumnName
     * @param idColumnValue
     * @return A map containing the token data and the input id column and value
     * @throws ValidationException If the request does not contain a token parameter
     */
    private Map<String, Object> getTokenData(
            SecretToken secretToken, String idColumnName, Object idColumnValue) 
            throws ValidationException {

        if(idColumnName == null) {
            throw new NullPointerException();
        }
        
        if(idColumnValue == null) {
            throw new ValidationException("Required: "+idColumnName);
        }
        
        if(secretToken == null) {
            throw new ValidationException("Required: "+Columns.Apptoken.token.name());
        }
        
        Map<String, Object> data = new HashMap<>(3, 1.0f);
        
        data.put(idColumnName, idColumnValue);
        
        // This is the decrypted token
        //
        data.put(Columns.Apptoken.seriesid.name(), secretToken.getSeriesId());
        data.put(Columns.Apptoken.token.name(), secretToken.getToken());
        
        // No need to use DatabaseRecord here as it will be converted
        // in the find method when called
        //
        return data;
    }

    /**
     * Searches for an entity in the database using the column names 
     * specified in the <tt>requiredColumns</tt> input parameter and 
     * their corresponding values in the <tt>data</tt> input parameter.
     * @param <X> The type of the entity to find
     * @param entityClass
     * @param whereParameters The data containing the values of the required columns
     * @return The entity if found, otherwise null.
     */
    protected <X> X find(
            Class<X> entityClass,
            Map<String, Object> whereParameters) {

        if(whereParameters.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        
        List<X> found = this.getEntityController(entityClass).select(whereParameters, null, -1, -1);
        
        X output;
        if(found == null || found.isEmpty()) {
            output = null;
        }else if(found.size() == 1) {
            output = found.get(0);
        }else{
            throw new UnsupportedOperationException("Found > 1 record where only 1 was expected with values: "+whereParameters);
        }
        
        return output;
    }
    
    protected <X> X find(Class<X> entityClass, String columnName, Object columnValue) throws ValidationException {

        Map<String, Object> where = Collections.singletonMap(
                columnName, columnValue);
        
        List<X> found = this.getEntityController(entityClass).select(where, null, -1, -1);
        
        X output;
        if(found == null || found.isEmpty()) {
            output = null;
        }else if(found.size() == 1) {
            output = found.get(0);
        }else{
            throw new UnsupportedOperationException("Found > 1 record where only 1 was expected with values: "+where);
        }
        
        return output;
    }

    protected Validator<Map<String, Object>> getValidator() {
        final int minPassLen = WebApp.getInstance().getConfiguration().getInt(ConfigNames.MIN_PASSWORD_LENGTH, 6);
        MapValidator validator = new MapValidator(){
            @Override
            protected void validate(String col, Object val) throws ValidationException {
                super.validate(col, val);
                if(col.equals("password") && val.toString().length() < minPassLen) {
                    throw new ValidationException("Password length must be greater or equals to "+minPassLen);
                }
            }
            @Override
            public String [] getColumnNames() {
                return BaseHandler.this.getInputNames();
            }
            @Override
            public String [] getAlternateColumnNames(String columnName) {
                return BaseHandler.this.getAlternateColumnNames(columnName);
            }
            @Override
            public int getValidatorType(String columnName) {
                return BaseHandler.this.getValidatorType(columnName);
            }
            @Override
            public boolean isColumnNullable(String columnName) {
                return BaseHandler.this.isColumnNullable(columnName);
            }
        };
        return validator;
    }

    protected Map<String, Object> getDatabaseInput() {
        return this.getDatabaseInput(null);
    }
    
    protected Map<String, Object> getDatabaseInput(
            String [] requiredColumns) {
        return this.getDatabaseFormat(this.getParameters(), requiredColumns);
    }
    
    protected Map<String, Object> getDatabaseFormat(Map<String, Object> data) {
        
        return this.getDatabaseFormat(data, null);
    }
    
    protected Map<String, Object> getDatabaseFormat(
            Map<String, Object> data, String [] requiredColumns) {
        Map<String, Object> output = new DatabaseRecord(data, requiredColumns) {
            @Override
            public Class getEntityClass() {
                return BaseHandler.this.getEntityClass();
            }
            @Override
            public boolean isToBeDecrypted(String columnName) {
                return BaseHandler.this.isToBeDecrypted(columnName);
            }
            @Override
            public boolean isToBeEncrypted(String columnName) {
                return BaseHandler.this.isToBeEncrypted(columnName);
            }
            @Override
            public String[] getAlternateColumnNames(String columnName) {
                return BaseHandler.this.getAlternateColumnNames(columnName);
            }
        };
        
        return output;
    }

    protected DatabaseRecord getDatabaseFormat() {
        DatabaseRecord output = new DatabaseRecord() {
            @Override
            public Class getEntityClass() {
                return BaseHandler.this.getEntityClass();
            }
            @Override
            public boolean isToBeDecrypted(String columnName) {
                return BaseHandler.this.isToBeDecrypted(columnName);
            }
            @Override
            public boolean isToBeEncrypted(String columnName) {
                return BaseHandler.this.isToBeEncrypted(columnName);
            }
            @Override
            public String[] getAlternateColumnNames(String columnName) {
                return BaseHandler.this.getAlternateColumnNames(columnName);
            }
        };
        return output;
    }
    
    public String getUserIdentifier() {
        String key;
        if(this.getEntityClass() == App.class) {
            key = "App";
        }else if (this.getEntityClass() == Appuser.class){
            key = "User";
        }else{
            throw new UnsupportedOperationException("Unexpected entity class: "+this.getEntityClass().getName());
        }
        return key;
    }

    protected Object checkNull(Map data, String key) throws ValidationException{
        Object val = data.get(key);
        if(val == null) {
            throw new ValidationException("Required: "+key);
        }
        return val;
    }

    private EntityController<U, Integer> ec_accessViaGetter;
    public EntityController<U, Integer> getEntityController() {
        if(ec_accessViaGetter == null) {
            ec_accessViaGetter = this.getEntityController(this.getEntityClass());
        }
        return ec_accessViaGetter;
    }
    
    public <X> EntityController<X, Integer> getEntityController(Class<X> entityClass) {
        return WebApp.getInstance().getJpaContext().getEntityController(entityClass, Integer.class);
    }

    private transient Authenticator<U, ?> a_accessViaGetter;
    protected Authenticator<U, ?> getAuthenticator() {
        if(a_accessViaGetter == null) {
            final Class tokenEntityClass;
            if(this.getEntityClass() == App.class) {
                tokenEntityClass = Apptoken.class;
            }else if(this.getEntityClass() == Appuser.class) {
                tokenEntityClass = Usertoken.class;
            }else{
                throw new UnsupportedOperationException("Unexpected entity class: "+this.getEntityClass().getName());
            }
            a_accessViaGetter = new Authenticator() {
                @Override
                public Class getUserEntityClass() {
                    return BaseHandler.this.getEntityClass();
                }
                @Override
                public Class getTokenEntityClass() {
                    return tokenEntityClass;
                }
            };
        }
        return a_accessViaGetter;
    }
    
    
    public Userstatus getUserstatus(AuthSettings<U> settings, AuthSvcJpaContext.userstatus userstatusEnum) {
        
        final JpaContext factory = WebApp.getInstance().getJpaContext();
        
        final EnumReferences refs = factory.getEnumReferences();

        final Userstatus userstatus = (Userstatus)refs.getEntity(userstatusEnum);
        Objects.requireNonNull(userstatus);
        
        return userstatus;
    }
    
    @Override
    public U getUser() {
        return user;
    }

    @Override
    public SecretToken getSecretToken() {
        return secretToken;
    }

    @Override
    public void setSecretToken(SecretToken secretToken) {
        this.secretToken = secretToken;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
        
    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
