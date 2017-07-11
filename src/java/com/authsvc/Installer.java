package com.authsvc;

import com.bc.io.CharFileIO;
import com.bc.util.XLogger;
import com.bc.jpa.EntityController;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;


/**
 * @(#)Installer.java   19-Jan-2015 23:01:01
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
public class Installer {

    public boolean isInstalled() {
        File file = this.getFile("META-INF/persistence.xml");
        return file.exists();
    }
    
    public boolean uninstall(JpaContext jpaContext) {
        
        JpaMetaData metaData = jpaContext.getMetaData();
        
        String [] puNames = metaData.getPersistenceUnitNames();
        
        Map<String, EntityController> dbs = new HashMap<>();
        for(String puName:puNames) {
            Class [] puClasses = metaData.getEntityClasses(puName);
            for(Class puClass:puClasses) {
                String dbName = metaData.getDatabaseName(puClass);
                if(!dbs.containsKey(dbName)) {
                    dbs.put(dbName, jpaContext.getEntityController(puClass));
                }
            }
        }
        
        for(String dbName:dbs.keySet()) {
            
            EntityController ec = dbs.get(dbName);

XLogger.getInstance().log(Level.INFO, "Dropping database: {0}", this.getClass(), dbName);
            ec.executeUpdate("drop database `"+dbName+"`");
        }

        File file = this.getFile("META-INF/persistence.xml");

XLogger.getInstance().log(Level.INFO, "Deleting file: {0}", this.getClass(), file);
        if(!file.delete()) {
            file.deleteOnExit();
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * The use of unique tableNames is advised to avoid conflicts in
     * a shared database. 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public boolean install(
            String databaseName, String userName, String password) 
            throws IOException, ClassNotFoundException, SQLException {
     
        if(this.isInstalled()) {
            throw new UnsupportedOperationException();
        }
        
        this.createDatabase(databaseName, userName, password);
        
        return this.updatePersistenceConfig(databaseName, userName, password);
    }   
    
    private boolean updatePersistenceConfig(
            String databaseName, String userName, String password) 
            throws IOException {
        
        File file = this.getFile("META-INF/persistence_xml.template");
        
        CharFileIO io = new CharFileIO();
        
        CharSequence contents = io.readChars(file);
        
        String sval = contents.toString();
        
        String url = this.getDatabaseURL(databaseName);
        
        sval = sval.replace("${url}", url);
        
        sval = sval.replace("${driver}", this.getDriverName());
        
        sval = sval.replace("${user}", userName);
        
        sval = sval.replace("${password}", password);
        
        File persistence = new File(file.getParent(), "persistence.xml");

XLogger.getInstance().log(Level.INFO, "Creating file: {0}", this.getClass(), file);
        
        io.write(false, sval, persistence);
        
        return file.exists();
    }
        
    private void createDatabase(String databaseName, 
            String userName, String password) 
            throws IOException, ClassNotFoundException, SQLException {
        
        File file = this.getFile("META-INF/dbinstall_sql.template");
        
        CharFileIO io = new CharFileIO();
        
        CharSequence contents = io.readChars(file);
        
        String sql = contents.toString().replace("${database}", databaseName);
        
        Connection conn = null;
        
        Statement stmt = null;
        
        try{

            conn = this.newConnection(databaseName, userName, password);
        
            stmt = conn.createStatement();

XLogger.getInstance().log(Level.INFO, "Creating database: {0}", this.getClass(), databaseName);
            
            stmt.execute(sql);
            
        }finally{
            
            this.close(stmt);
            
            this.close(conn);
        }
    }
    
    private File getFile(String fname) {
        
        try{
            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            URL url = loader.getResource(fname);

            return Paths.get(url.toURI()).toFile();
            
        }catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getDriverName() {
        return "com.mysql.jdbc.Driver";
    }
    
    public String getDatabaseURL(String databaseName) {
        return "jdbc:mysql://localhost:3306/"+databaseName+"?zeroDateTimeBehavior=convertToNull";
    }
    
    /**
     * Create a new connection using driver <tt>com.mysql.jdbc.Driver</tt>
     * and databaseURL <tt>jdbc:mysql://localhost:3306/[databaseName]?zeroDateTimeBehavior=convertToNull</tt>
     * where [databaseName] is the name of the database
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private Connection newConnection(
            String databaseName, String userName, String password) 
            throws ClassNotFoundException, SQLException {
    
        return this.newConnection(
                this.getDriverName(), 
                this.getDatabaseURL(databaseName), 
                userName, 
                password);
        
    }
    
    private Connection newConnection(
            String driverName, String databaseURL, 
            String userName, String password) 
            throws ClassNotFoundException, SQLException {

        Class.forName(driverName);
        
        Connection con = DriverManager.getConnection(databaseURL, userName, password);

XLogger.getInstance().log(Level.FINE, "New connection:: {0}", this.getClass(), con);
        return con;
    }
    
    private void close(Connection con) {
        try{
            if (con == null || con.isClosed()) return; 
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Error checking if connection is closed", this.getClass(), e);
        }
        
        try {
            if (!con.getAutoCommit()) con.setAutoCommit(true);
        }catch (SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Error re-setting connection property (auto-commit) to 'true'", this.getClass(), e);
        }finally{

            try {

                con.close();

XLogger.getInstance().log(Level.FINER, "Closed connection:: {0}", this.getClass(), con);
            }catch (SQLException e) {
                XLogger.getInstance().log(Level.WARNING, "", this.getClass(), e);
            }
        }
    }

    private void close(Statement stmt) {
        try{
            if (stmt == null || stmt.isClosed()) return; 
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Error checking if statement is closed", this.getClass(), e);
        }
        try {
            stmt.close();
        }catch (SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "", this.getClass(), e);
        }
    }
}
