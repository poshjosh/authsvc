package com.authsvc;

import com.authsvc.pu.AuthSvcJpaContext;
import com.bc.mail.config.MailConfig;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import com.bc.jpa.context.JpaContext;
import com.bc.security.Encryption;
import com.bc.security.SecurityProvider;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @(#)WebApp.java   16-Oct-2014 10:08:15
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
public class AuthSvcApp extends Installer {

    private transient static final Logger LOG = Logger.getLogger(AuthSvcApp.class.getName());
    
    private Configuration config;
    
    private Encryption encryption;
    
    private MailConfig mailConfig;
    
    private JpaContext jpaContext;
    
    public void init(String defaultPropertiesFilename, String propertiesFilename) 
            throws IOException, ConfigurationException {
        
        try{
            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            
            URL defaultFileLoc = loader.getResource(defaultPropertiesFilename);
        
            URL fileLoc = loader.getResource(propertiesFilename);
            
            this.init(defaultFileLoc, fileLoc);
            
        }catch(MalformedURLException e) {
            
            throw new RuntimeException(e);
        }
    }
    
    public void init( 
            URL defaultFileLocation, URL fileLocation) 
            throws IOException, ConfigurationException {
        
        if(!this.isInstalled()) {
            throw new UnsupportedOperationException(
            "App not yet installed. First call: "+this.getClass().getName()+"#install()");
        }
        
        // Enable list delimiter handling using a comma as delimiter character
        this.config = this.loadConfig(defaultFileLocation, fileLocation, ',');
        
        final String loggingConfig = this.isProductionMode() ? 
                "META-INF/logging.properties" : "META-INF/logging_devmode.properties";
        
        try(InputStream ins = 
                Thread.currentThread().getContextClassLoader().getResourceAsStream(loggingConfig)) {
            LogManager.getLogManager().readConfiguration(ins);
        }
        
        try{
            
            LOG.log(Level.FINER, "Creating security tool");

            
            this.encryption = SecurityProvider.DEFAULT.getEncryption(
                    this.config.getString(ConfigNames.ALGORITH),
                    this.config.getString(ConfigNames.ENCRYPTION_KEY));
            
        }catch(GeneralSecurityException e) {
            throw new RuntimeException("Failed to create instance of type: "+Encryption.class.getName(), e);
        }

        final String uriStr = this.config.getString(ConfigNames.PERSISTENCE_CONFIG_URI);
        try{
            final URI persistenceConfigUri = uriStr == null ? null : new URI(uriStr);
            this.jpaContext = persistenceConfigUri == null ? 
                    new AuthSvcJpaContext() :
                    new AuthSvcJpaContext(persistenceConfigUri);
        }catch(URISyntaxException e) {
            throw new RuntimeException("Invalid URI syntax: "+uriStr, e);
        }
    }
    
    public void setMailConfig(MailConfig conf) {
        this.mailConfig = conf;
    }
    
    public MailConfig getMailConfig() {
        if(mailConfig == null) {
            mailConfig = MailConfig.builder().build();
        }
        return mailConfig;
    }
    
    public Configuration loadConfig(
            URL defaultFileLocation, URL fileLocation, char listDelimiter) 
            throws ConfigurationException {
        
        LOG.info(() -> 
                MessageFormat.format("Loading properties configuration. List delimiter: {0}\nDefault file: {1}\nFile: {2}", 
                listDelimiter, defaultFileLocation, fileLocation));

        if(fileLocation == null) {
            throw new NullPointerException();
        }
        
        Configuration output; 
        
        if(defaultFileLocation != null) {
            
            CompositeConfiguration composite = new CompositeConfiguration();

            PropertiesConfiguration cfg = this.loadConfig(
                    fileLocation, listDelimiter);
            composite.addConfiguration(cfg, true);
            
            PropertiesConfiguration defaults = this.loadConfig(
                    defaultFileLocation, listDelimiter);
            composite.addConfiguration(defaults);
            
            output = composite;
            
        }else{
            
            output = this.loadConfig(fileLocation, listDelimiter);
        }
        
        return output;
    }
    
    private PropertiesConfiguration loadConfig(
            URL fileLocation, char listDelimiter) 
            throws ConfigurationException {
        PropertiesConfiguration cfg = new PropertiesConfiguration();
        cfg.setListDelimiter(listDelimiter);
        cfg.setURL(fileLocation);
        cfg.load();
        return cfg;
    }
    
    public boolean isProductionMode() {
        return true;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public JpaContext getJpaContext() {
        return jpaContext;
    }
}
