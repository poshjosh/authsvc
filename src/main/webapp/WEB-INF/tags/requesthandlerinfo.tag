<%-- 
    Document   : requesthandlerinfo
    Created on : 16-Jan-2015, 02:20:35
    Author     : Josh
--%>
<%@tag description="Displays info of a RequestHandler" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@attribute name="requestHandlerBean" required="true" type="com.authsvc.web.IndexPageBean"%>

<code>
    ${pageContext.servletContext.contextPath}/${requestHandlerBean.servletPath}
</code>
<table>
  <thead>
    <tr>
      <th>Parameter Name(s)</th><th>Optional</th>      
    </tr>      
  </thead>  
  <c:forEach var="inputName" items="${requestHandlerBean.inputNames}">
    <jsp:setProperty name="requestHandlerBean" property="columnName" value="${inputName}"/>          
    <tr>
      <td>
        ${inputName}  
        <c:if test="${requestHandlerBean.alternateColumnNames != null && not empty requestHandlerBean.alternateColumnNames}">
          <c:forEach var="altcol" items="${requestHandlerBean.alternateColumnNames}">
            / ${altcol}    
          </c:forEach>                      
        </c:if>
      </td>    
      <td>
        <c:choose>
          <c:when test="${requestHandlerBean.columnNullable}">true</c:when>
          <c:otherwise>false</c:otherwise>
        </c:choose>  
      </td>
    </tr>
  </c:forEach>
</table>
<p>The service will return json text of the format:</p>
<c:choose>
  <c:when test="${requestHandlerBean.outputNames == null || empty requestHandlerBean.outputNames}">
    <pre>
    {
        "SuccessMessage":"Success"
    }
    or
    {
        "ErrorMessage":"Error"
    } 
    </pre>
  </c:when>    
  <c:otherwise>
    <table>
      <c:forEach var="outputName" items="${requestHandlerBean.outputNames}">
        <jsp:setProperty name="requestHandlerBean" property="columnName" value="${outputName}"/>          
        <tr>
          <td>
            ${outputName}  
          </td>    
          <td>
            <tt>${outputName} value</tt>    
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>
