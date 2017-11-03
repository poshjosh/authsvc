<%-- 
    Document   : oops
    Created on : Jul 15, 2017, 12:50:15 AM
    Author     : Josh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>${appName} - Error Page</title>
    </head>
    <body>
        <p>The following error occurred while processing the request:</p>
        <p>
            Requested resource: ${pageContext.errorData.requestURI}
            <br/><br/>
            Response status code: ${ pageContext.errorData.statusCode}
            <br/><br/>
            Response message: ${ pageContext.errorData.throwable.message}
        </p>
    </body>
</html>
