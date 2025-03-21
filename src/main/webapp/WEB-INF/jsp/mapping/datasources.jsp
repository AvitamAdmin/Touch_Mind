<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<option value="">Please select the data source</option>
<c:forEach items="${dataSources}" var="dataSource">
    <option value="${dataSource.recordId}" >${dataSource.identifier}</option>
</c:forEach>