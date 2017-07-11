<%-- 
    Document   : sendmail
    Created on : May 31, 2016, 10:09:57 PM
    Author     : Josh
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Send Email</title>
    </head>
    <body>
        <p><b><a href="${pageContext.servletContext.contextPath}/sendmail.jsp">Refresh Page</a></b></p>
        <%
            Object message = session.getAttribute("sessionMessage");
            if(message != null) {
                out.println(message);
                session.setAttribute("sessionMessage", null);
            } 
        %>
        <c:if test="${param.submit != null}">
            
            <%
                
                final String emailtype = request.getParameter("emailtype");
                final String emailsubject = request.getParameter("emailsubject");
                final String emailmessage = request.getParameter("emailmessage");
                final String sslStr = request.getParameter("ssl");
                final boolean ssl = sslStr == null ? false : true;
                final String tlsenabledStr = request.getParameter("starttlsenabled");
                final boolean tlsenabled = tlsenabledStr == null ? false : true;
                final String host = request.getParameter("host");
                final String port = request.getParameter("port");
                final String senderemail = request.getParameter("senderemail");
                final String senderpass = request.getParameter("senderpass");
                final String recipient0 = request.getParameter("recipient0");
                
                final HttpSession currSess = session;
                
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder builder = new StringBuilder();
                        try{

                            builder.append("<p style='font-size:0.75em;'>");
                            builder.append("<b>").append(new java.util.Date()).append("</b>, type: <b>").append(emailtype).append("</b><br/>");
                            builder.append("Subject: ").append(emailsubject).append("<br/>");
                            builder.append("Message: ").append(emailmessage).append("<br/>");
                            builder.append("SSL: ").append(ssl).append(", tls enabled: ").append(tlsenabled).append("<br/>");
                            builder.append("Host: ").append(host).append(", port: ").append(port).append("<br/>");
                            builder.append("Sender email: ").append(senderemail).append(", sender pass: ").append(senderpass).append(", recipient 1: ").append(recipient0).append("<br/>");
                            builder.append("</p>");

                            final org.apache.commons.mail.Email email;
                            if(emailtype != null && emailtype.endsWith("html")) {
                                org.apache.commons.mail.HtmlEmail htmlemail = new org.apache.commons.mail.HtmlEmail();
                                htmlemail.setHtmlMsg(emailmessage);
                                email = htmlemail;
                            }else{
                                email = new org.apache.commons.mail.SimpleEmail();
                            }
                            
                            email.setSubject(emailsubject);
                            email.setMsg(emailmessage);
                            email.setHostName(host);
                            email.setFrom(senderemail);
                            email.addTo(recipient0);
                            
                            email.setStartTLSEnabled(tlsenabled);
                            email.setSSLOnConnect(ssl);
                            if(ssl) {
                                email.setSslSmtpPort(port);
                            }else{
                                email.setSmtpPort(Integer.parseInt(port));
                            }
                            if(senderemail != null && senderpass != null) {
                                email.setAuthentication(senderemail, senderpass);
                            }
                            
                            email.send();

                            builder.append("<p><b>SUCCESS</b></p>");

                        }catch(Exception e) {
                            builder.append("<p style='font-size:0.75em;'>");
                            builder.append("<b>").append(e).append("</b><br/>");
                            StackTraceElement [] arr = e.getStackTrace();
                            for(StackTraceElement ste:arr) {
                                builder.append(ste).append("<br/>");
                            }
                            Throwable cause = e.getCause();
                            do{
                                if(cause == null) {
                                    break;
                                }
                                builder.append("<b>Caused by</b>&nbsp;<b>").append(cause).append("</b><br/>");
                                StackTraceElement [] arr_cause = cause.getStackTrace();
                                for(StackTraceElement ste:arr_cause) {
                                    builder.append(ste).append("<br/>");
                                }
                                cause = cause.getCause();
                            }while(true);
                            builder.append("</p>");
                        }
                        
                        String currMsg = builder.toString();
                        Object prevMsg = currSess.getAttribute("sessionMessage");
                        currSess.setAttribute("sessionMessage", prevMsg==null?currMsg:prevMsg+"<br/>"+currMsg);
                    }
                };
                
                new Thread(runnable).start();
                
                String currMsg = "<b>Sending Email</b>";
                Object prevMsg = currSess.getAttribute("sessionMessage");
                currSess.setAttribute("sessionMessage", prevMsg==null?currMsg:prevMsg+"<br/>"+currMsg);
            %>
        </c:if>
        <form method="post" action="${pageContext.servletContext.contextPath}/sendmail.jsp">
            Email type: <select name="emailtype">
                <option disabled>Select email type</option> 
                <option value="text/plain">Plain</option> 
                <option value="text/html">HTML</option> 
            </select><br/><br/>
<%-- looseboxes@gmail.com = m4ScSe-Vu; mail.newsminute@gmail.com = rAmC-1p5; noreply@looseboxes.com = 7345xT-eeSw --%>
            Sender email: <input type="text" name="senderemail" value="mail.newsminute@gmail.com"/><br/><br/>
            Sender pass: <input type="text" name="senderpass" value="rAmC-1p5"/><br/><br/>
            Subject: <input type="text" name="emailsubject" value="Jesus is Lord"/><br/><br/>
            Message: <input type="text" name="emailmessage" value="Its important you know that: Jesus is Lord"/><br/><br/>
            Recipient 1: <input type="text" name="recipient0" value="posh.bc@gmail.com"/><br/><br/>
            SSL: <input type="checkbox" checked name="ssl" value="true"/><br/><br/>
            Start TLS enabled: <input type="checkbox" checked name="starttlsenabled" value="true"/><br/><br/>
            Host: <input type="text" name="host" value="smtp.gmail.com"/><br/><br/>
            Port: <input type="text" name="port" value="465"/><br/><br/>
            <input type="submit" name="submit" value="Submit"/>
        </form>
    </body>
</html>
