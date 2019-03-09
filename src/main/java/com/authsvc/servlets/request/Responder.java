package com.authsvc.servlets.request;

import com.authsvc.AuthException;
import com.bc.util.JsonFormat;
import com.bc.validators.ValidationException;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * @(#)Responder.java   20-Dec-2014 08:36:27
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
public class Responder implements Serializable {
    
    private transient static final Logger LOG = Logger.getLogger(Responder.class.getName());

    public PrintWriter respond(
            HttpServletResponse response, int statusCode, String msg) 
            throws IOException {

        response.setContentType("text/html;charset=utf-8");

        response.setStatus(statusCode);

        PrintWriter out = response.getWriter();

        if(msg != null) {

            out.println(msg);
            
            if(LOG.isLoggable(Level.FINER)){
                LOG.log(Level.FINER, "Response: {0}", msg);
            }

        }else{

            throw new NullPointerException();
        }
        
        return out;
    }
    
    public void respond(HttpServletResponse response, Object message) {

        PrintWriter out = null;
        
        try {

            int statusCode = this.getSuccessCode();
            
            String msg = this.getSuccessMessage(message);
            
            out = this.respond(response, statusCode, msg);
            
        }catch(Throwable t) {
            
            this.respond(response, t);
            
        }finally{
            if(out != null) {
                out.close();
            }
        } 
    }
    
    public void respond(HttpServletResponse response, Throwable t) {

        PrintWriter out = null;
        
        try{

            if(t instanceof ValidationException) {
                LOG.log(Level.WARNING, "{0}", t.toString());
                LOG.log(Level.FINE, null, t);
            }else{
                LOG.log(Level.WARNING, "Error processing request", t);
            }

            int statusCode = this.getErrorCode(t);
            
            String errMsg = this.getErrorMessage(t);

            if(LOG.isLoggable(Level.FINE)){
                LOG.log(Level.FINE, "Error. status code: {0}, message: {1}",
                    new Object[]{ statusCode,  errMsg});
            }
            
            out = this.respond(response, statusCode, errMsg);
            
        }catch(IOException e) {

            LOG.log(Level.WARNING, "Error sending error response", e);
            
        }catch(RuntimeException e) {

            LOG.log(Level.WARNING, "Error sending error response", e);
            
        }finally{

            if(out != null) {
                out.close();
            }
        }   
    }
    
    public int getSuccessCode() {
        return HttpServletResponse.SC_OK;
    }

    public String getSuccessMessage(Object message) {
        if(message == null) {
            throw new NullPointerException();
        }
        JsonFormat jsonFmt = new JsonFormat();
        StringBuilder builder = new StringBuilder();
        jsonFmt.appendJSONString(
                message.equals(Boolean.TRUE) ? Collections.singletonMap("SuccessMessage", "success") : message, 
                builder);
        return builder.toString();
    }
    
    public int getErrorCode(Throwable t) {
        if(t instanceof AuthException) {
            return HttpServletResponse.SC_UNAUTHORIZED;
        }else if(t instanceof FileNotFoundException) {
            return HttpServletResponse.SC_NOT_FOUND;
        }else if(t instanceof ValidationException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }else{
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    public String getErrorMessage(Throwable t) {
        String msg;
        if(t instanceof ServletException || 
                t instanceof ValidationException ||
                t instanceof AuthException) {
            msg = t.getLocalizedMessage();
        }else{
            msg = "Unexpected Error";
        }
        JsonFormat jsonFmt = new JsonFormat();
        StringBuilder builder = new StringBuilder();
        //@related error message format. Don't change this without changing all related
        //
        jsonFmt.appendJSONString("ErrorMessage", msg, builder);
        return builder.toString();
    }
}
