package com.authsvc.handlers;

import com.authsvc.web.WebApp;
import com.bc.jpa.JpaUtil;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;


/**
 * @(#)DatabaseRecord.java   17-Jan-2015 08:30:56
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
public abstract class DatabaseRecord extends HashMap<String, Object> {

    public DatabaseRecord() {}
    
    public DatabaseRecord(Map<String, Object> source) {
        super(Math.max((int) (source.size() / 0.75f) + 1, 10), 0.75f);
        super.putAll(DatabaseRecord.this.getDatabaseView(source));
    }

    public DatabaseRecord(Map<String, Object> source, String [] requiredColumns) {
        super(Math.max((int) (  (requiredColumns == null ? source.size() : requiredColumns.length)   / 0.75f) + 1, 10), 0.75f);
        super.putAll(DatabaseRecord.this.getDatabaseView(source, requiredColumns));
    }
    
    public abstract Class getEntityClass();
    /**
     * @return The value in the map for the key represented by the input
     * <tt>col</tt> as required for database operations
     */
    public Map<String, Object> getDatabaseView(Map<String, Object> data) {
        return this.getDatabaseView(data, null);
    }
    
    /**
     * @return The value in the map for the key represented by the input
     * <tt>col</tt> as required for database operations
     */
    public Map<String, Object> getDatabaseView(
            Map<String, Object> data, String [] requiredColumns) {
        
        int size = requiredColumns == null ? data.size() : requiredColumns.length;
        
        Map<String, Object> output = new HashMap<String, Object>(size, 1.0f);
        
        if(requiredColumns == null) {
            for(String col:data.keySet()) {
                Map.Entry<String, Object> entry = this.getDatabaseValue(data, col);
                if(entry == null) {
                    continue;
                }
                output.put(entry.getKey(), entry.getValue());
            }
        }else{
            for(String col:requiredColumns) {
                Map.Entry<String, Object> entry = this.getDatabaseValue(data, col);
                if(entry == null) {
                    continue;
                }
                output.put(entry.getKey(), entry.getValue());
            }
        }
        return output;
    }
    
    
    /**
     * @return The value in the map for the key represented by the input
     * <tt>col</tt> as required for database operations
     */
    public Map.Entry<String, Object> getDatabaseValue(
            Map<String, Object> data, String col) {

        if(!this.isDatabaseField(col)) {
            return null;
        }
        
        Object val = data.get(col);
        
        if(val == null) {

            String [] altcols = this.getAlternateColumnNames(col);

            if(altcols != null && altcols.length != 0) {
                for(String altcol:altcols) {
                    Object altval = data.get(altcol);
                    if(altval != null) {
                        col = altcol;
                        val = altval;
                        break;
                    }
                }
            }
        }
        
        if(val != null) {

            if(this.isToBeEncrypted(col)) {
                try{
                    val = WebApp.getInstance().getEncryption().encrypt(val.toString().toCharArray());
                }catch(GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
            }else if(this.isToBeDecrypted(col)) {
                try{
                    val = new String(WebApp.getInstance().getEncryption().decrypt(val.toString()));
                }catch(GeneralSecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        return new HashMap.SimpleEntry<>(col, val);
    }
    
    private Field [] fields;
    public boolean isDatabaseField(String columnName) {
        if(fields == null) {
            fields = this.getEntityClass().getDeclaredFields();
        }
        return JpaUtil.getMethodName(false, fields, columnName) != null;
    }
    
    public boolean isToBeEncrypted(String columnName) {
        return false;
    }
    
    public boolean isToBeDecrypted(String columnName) {
        return false;
    }
    
    public String [] getAlternateColumnNames(String columnName) {
        return null;
    }
}
