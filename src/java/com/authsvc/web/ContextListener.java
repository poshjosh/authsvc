package com.authsvc.web;

import com.authsvc.ConfigNames;
import com.bc.util.XLogger;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
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
        
        ServletContext context = sce.getServletContext();
        
        try{
            
            WebApp app = WebApp.getInstance();
            if(!app.isInstalled()) {
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
        ServletContext ctx = sce.getServletContext();
        if(ctx == null) {
            return;
        }
        Enumeration<String> en = ctx.getAttributeNames();
        if(en == null) {
            return;
        }
        try{
            while(en.hasMoreElements()) {
                ctx.removeAttribute(en.nextElement());
            }
        }catch(Throwable ignored) { 
            XLogger.getInstance().log(Level.WARNING, 
            "Error destroying Servlet Context", this.getClass(), ignored);
        }
XLogger.getInstance().log(Level.INFO, 
"Done destroying Servlet Context  {0}", this.getClass(), new Date());
    }

    private void updateAttributesFromConfig(String [] names, Configuration config, ServletContext context) {
        for(String name:names) {
            context.setAttribute(name, config.getString(name));
        }
    }
}
