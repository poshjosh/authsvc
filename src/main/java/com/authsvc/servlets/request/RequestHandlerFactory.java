package com.authsvc.servlets.request;

import com.authsvc.handlers.BaseHandler;
import com.authsvc.handlers.AuthHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;


/**
 * @(#)RequestHandlerFactory.java   24-Nov-2014 23:01:42
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
public class RequestHandlerFactory {
    private transient static final Logger LOG = Logger.getLogger(RequestHandlerFactory.class.getName());
    
    private final String packageName;
    
    public RequestHandlerFactory() {
        packageName = BaseHandler.class.getPackage().getName();        
    }
    

    public AuthHandler get(HttpServletRequest request) {
        String servletPath = request.getServletPath();
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Servlet path: {0}", servletPath);
}
        if(servletPath == null) {
            return null;
        }else{
            return get(servletPath);
        }
    }
    
    /**
     * @param name
     * @return <tt>[packagename].Login</tt> for an input of <tt>login</tt>
     * where <tt>packagename</tt> is the package name of the object instance
     * which called this method
     */
    public AuthHandler get(String name) {
        String className = this.toClassName(packageName, name);
        try{
            return (AuthHandler)Class.forName(className).getConstructor().newInstance();
        }catch(Exception e) {
            if(LOG.isLoggable(Level.WARNING)){
                  LOG.log(Level.WARNING, "Failed to create new instance of: "+className, e);
            }
            return null;
        }
    }

    /**
     * <p>For inputs <tt>prefix</tt> and <tt>abc</tt> returns <tt>prefix.Abc</tt></p>
     * <p>For inputs <tt>prefix</tt> and <tt>/ABC</tt> returns <tt>prefix.Abc</tt></p>
     * @param prefix
     * @param paramValue
     * @return The input parameters formatted into an appropriate class name
     */
    private String toClassName(String prefix, String paramValue) {
        StringBuilder builder = new StringBuilder(prefix.length() + 1 + paramValue.length());
        builder.append(prefix).append('.');
        int indexOfFirst = 0;
        for(int i=0; i<paramValue.length(); i++) {
            char ch = paramValue.charAt(i);
            if(i == 0) {
                if(ch == '/') {
                    indexOfFirst = 1;
                    continue;
                }
            }
            if(i == indexOfFirst) {
                ch = Character.toTitleCase(ch);
            }else{
                ch = Character.toLowerCase(ch);
            }
            builder.append(ch);
        }
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Prefix: {0}, param: {1}, class name: {2}",
new Object[]{ prefix,  paramValue,  builder});
}
        return builder.toString();
    }
}
