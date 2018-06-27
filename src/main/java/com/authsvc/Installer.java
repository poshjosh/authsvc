package com.authsvc;

import com.bc.io.CharFileIO;
import com.bc.jpa.controller.EntityController;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.metadata.JpaMetaData;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.logging.Logger;


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
public class Installer implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(Installer.class.getName());
    
    private final String INSTALLATION_MARKER = "authsvc.installed";

    public boolean isInstalled() {
        return Files.exists(Paths.get(System.getProperty("user.home"), INSTALLATION_MARKER));
    }
    
    public void setInstalled() throws IOException {
        Files.write(Paths.get(System.getProperty("user.home"), INSTALLATION_MARKER), 
                (INSTALLATION_MARKER + "=true").getBytes(), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING, 
                StandardOpenOption.WRITE);
    }
    
    public boolean uninstall(JpaContext jpaContext) {
        
        JpaMetaData metaData = jpaContext.getMetaData();
        
        Set<String> puNames = metaData.getPersistenceUnitNames();
        
        Map<String, EntityController> dbs = new HashMap<>();
        for(String puName:puNames) {
            Set<Class> puClasses = metaData.getEntityClasses(puName);
            for(Class puClass:puClasses) {
                String dbName = metaData.getDatabaseName(puClass);
                if(!dbs.containsKey(dbName)) {
                    dbs.put(dbName, jpaContext.getEntityController(puClass));
                }
            }
        }
        
        for(String dbName:dbs.keySet()) {
            
            EntityController ec = dbs.get(dbName);

            LOG.log(Level.INFO, "Dropping database: {0}", dbName);
            
            ec.executeUpdate("drop database `"+dbName+"`");
        }

        try{
            Files.deleteIfExists(Paths.get(System.getProperty("user.home"), INSTALLATION_MARKER));
            return true;
        }catch(IOException e) {
            throw new RuntimeException(e);
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
        
        final boolean installed = this.updatePersistenceConfig(databaseName, userName, password);
        
        if(installed) {
            this.setInstalled();
        }
        
        return installed;
    }   
    
    private boolean updatePersistenceConfig(
            String databaseName, String userName, String password) 
            throws IOException {
        
        final String fname = "META-INF/persistence_xml.template";
        
        String sval = this.getFileContents(fname);
        
        String url = this.getDatabaseURL(databaseName);
        
        sval = sval.replace("${url}", url);
        
        sval = sval.replace("${driver}", this.getDriverName());
        
        sval = sval.replace("${user}", userName);
        
        sval = sval.replace("${password}", password);
        
        final File file = new File(fname);
        
        final File persistence = new File(file.getParent(), "persistence.xml");

        LOG.log(Level.INFO, "Creating file: {0}", file);
        
        new CharFileIO().write(false, sval, persistence);
        
        return file.exists();
    }
        
    private void createDatabase(String databaseName, 
            String userName, String password) 
            throws IOException, ClassNotFoundException, SQLException {
        
        final String contents = this.getFileContents("META-INF/dbinstall_sql.template");
        
        String sql = contents.replace("${database}", databaseName);
        
        Connection conn = null;
        
        Statement stmt = null;
        
        try{

            conn = this.newConnection(databaseName, userName, password);
        
            stmt = conn.createStatement();

            LOG.log(Level.INFO, "Creating database: {0}", databaseName);
            
            stmt.execute(sql);
            
        }finally{
            
            this.close(stmt);
            
            this.close(conn);
        }
    }
    
    private String getFileContents(String fname) {
        try{
            final CharSequence contents;
            try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fname);
                    Reader reader = new InputStreamReader(in, "utf-8")) {
                contents = new CharFileIO().readChars(reader);
            }
            return contents.toString();
        }catch(IOException e) {
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

        LOG.log(Level.FINE, "New connection:: {0}", con);
        return con;
    }
    
    private void close(Connection con) {
        try{
            if (con == null || con.isClosed()) return; 
        }catch(SQLException e) {
            LOG.log(Level.WARNING, "Error checking if connection is closed", e);
        }
        
        try {
            if (!con.getAutoCommit()) con.setAutoCommit(true);
        }catch (SQLException e) {
            LOG.log(Level.WARNING, "Error re-setting connection property (auto-commit) to 'true'", e);
        }finally{

            try {

                con.close();

                LOG.log(Level.FINER, "Closed connection:: {0}", con);
            }catch (SQLException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
    }

    private void close(Statement stmt) {
        try{
            if (stmt == null || stmt.isClosed()) return; 
        }catch(SQLException e) {
            LOG.log(Level.WARNING, "Error checking if statement is closed", e);
        }
        try {
            stmt.close();
        }catch (SQLException e) {
            LOG.log(Level.WARNING, null, e);
        }
    }
}
/**
 * 
    private boolean isExistingFile(String fname) {
        final File file = this.getFile(fname);
        final String jarFname = this.getJarFilename(file.toString(), null);
        if(jarFname != null) {
            try{
                final JarFile jar = new JarFile(jarFname);
                final JarEntry entry = jar.getJarEntry(fname);
                return (entry != null);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            return file.exists();
        }
    }
    
    private File getFile(String fname) {
        final ClassLoader classLoader = getClass().getClassLoader();
	final File file = new File(classLoader.getResource(fname).getFile());
        return file;
    }
    
    private boolean isJarFile(String path) {
        return this.getJarFilename(path, null) != null;
    }

    private String getJarFilename(String path, String outputIfNone) {
        path = path.replace('/', '\\');
        final int end = path.lastIndexOf(".jar!");
        if(end == -1) {
            return outputIfNone;
        }else{
            final String jarFile = path.substring(0, end + 4);
            final String jarFileName = new File(jarFile).getName();
            return jarFileName;
        }
    }

 * 
 */