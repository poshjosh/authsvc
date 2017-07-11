<%-- 
    Document   : index
    Created on : 24-Nov-2014, 01:24:33
    Author     : Josh
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/tlds/bcauthsvc" prefix="bcauthsvc"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Authentication Service</title>
  </head>
  <body>
    <jsp:useBean id="IndexPageBean" class="com.authsvc.web.IndexPageBean" scope="page"/>  
    <ul>
      <li>
        <p><b>Register a new app</b></p>
        <jsp:useBean id="Createapp" class="com.authsvc.handlers.Createapp" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Createapp}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>  
      <li>
        <p><b>You need to activate your account to proceed</b></p>  
        <p><b>* Activate app account via email</b></p>
        <p>
            If request parameter <tt>sendregistrationmail</tt> was <tt>true</tt> 
            when registering the app, then registration causes an email to be 
            sent to the provided email address from where the registered user 
            should click the provided link to activate their account. 
            Otherwise you need to manually activate your account:
        </p>
        <p><b>* Activate app account manually</b></p>
        <p>
            If request parameter <tt>sendregistrationmail</tt> was <tt>false</tt> 
            when registering the app, then the app would need to be manually 
            activated.
        </p>
        <jsp:useBean id="Editappstatus" class="com.authsvc.handlers.Editappstatus" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Editappstatus}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
      <li>
        <p><b>Request for a new token for the app:</b></p>
        <jsp:useBean id="Authorizeapp" class="com.authsvc.handlers.Authorizeapp" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Authorizeapp}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
      <li>
        <p><b>Create a new user.</b></p>
        <jsp:useBean id="Createuser" class="com.authsvc.handlers.Createuser" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Createuser}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
      <li>
        <p><b>Login the user.</b></p>
        <jsp:useBean id="Loginuser" class="com.authsvc.handlers.Loginuser" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Loginuser}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
      <li>
        <p><b>Request for a new token for the user.</b></p>
        <jsp:useBean id="Authorizeuser" class="com.authsvc.handlers.Authorizeuser" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Authorizeuser}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
      <li>
        <p><b>Authenticate the user.</b></p>
        <jsp:useBean id="Authenticateuser" class="com.authsvc.handlers.Authenticateuser" scope="page"/>
        <jsp:setProperty name="IndexPageBean" property="requestHandler" value="${Authenticateuser}"/>        
        <bcauthsvc:requesthandlerinfo requestHandlerBean="${IndexPageBean}"/>        
      </li>
    </ul>  
  </body>
</html>
