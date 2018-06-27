package com.authsvc.web;

import com.authsvc.ConfigNames;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * Web application lifecycle listener.
 *
 * @author Josh
 */
@WebListener()
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        final ServletContext context = sce.getServletContext();
            
        try{
            
            final WebApp app = WebApp.getInstance();

            final boolean installed = app.isInstalled();
            if(!installed) {
                app.install(context.getInitParameter("databasename"), 
                        context.getInitParameter("username"), 
                        context.getInitParameter("password"));
            }
            
            app.init(context);
            
            final String [] names = {ConfigNames.APP_NAME, ConfigNames.BASE_URL};
            
            this.updateAttributesFromConfig(names, app.getConfiguration(), context);

        }catch(IOException | ClassNotFoundException | SQLException | ServletException | ConfigurationException e) {

            throw new RuntimeException("This program has to exit due to the following problem:", e);

//            System.exit(1); // The system exited without calling context destroyed

//            this.contextDestroyed(evt);; // The web page was still opened after
            // manually calling this method. Also I am suspicious of passing the
            // Initialized Event object into the contextDestroyed method.
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        final Logger LOG = Logger.getLogger(this.getClass().getName());
        
        final ServletContext ctx = sce.getServletContext();
        if(ctx == null) {
            return;
        }
        Enumeration<String> en = ctx.getAttributeNames();
        if(en == null) {
            return;
        }
        try{
            final List<String> toRemove = new ArrayList<>();
            while(en.hasMoreElements()) {
                toRemove.add(en.nextElement());
            }
            toRemove.forEach((attrName) -> {
                ctx.removeAttribute(attrName);
            });
        }catch(Throwable ignored) { 
            LOG.log(Level.WARNING, "Error destroying Servlet Context", ignored);
        }
        LOG.log(Level.INFO, "Done destroying Servlet Context  {0}", new Date());
    }

    private void updateAttributesFromConfig(String [] names, Configuration config, ServletContext context) {
        for(String name:names) {
            context.setAttribute(name, config.getString(name));
        }
    }
}
