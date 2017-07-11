<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${appName} - Signup</title>
  </head>
  <body>
      
    <h3>Signup for ${appName} Authentication Service</h3>  
    
    <form name="signupform" id="signupformid" 
          method="post" action="${pageContext.servletContext.contextPath}/createapp">
        
      <p><label>Email address: <input type="text" name="emailAddress"/></label></p>
      <p><label>App name: <input type="text" name="username"/></label></p>
      <p><label>Password: <input type="password" name="password"/></label></p>
      
      <input type="submit" value="Submit"/>
      
    </form>
      
  </body>
</html>
