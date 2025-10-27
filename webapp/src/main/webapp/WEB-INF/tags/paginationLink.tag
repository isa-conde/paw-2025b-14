<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="page" required="true" %>
<%@ attribute name="url" required="true" %>
<%@attribute name="pageNumber" required="false" type="java.lang.String" %>
<c:if test="${empty pageNumber}">
    <c:set var="pageNumber" value="" scope="page"/>
</c:if>
<c:set var="pageParamName" value="page${pageNumber}" />
<c:url var="completeUrl" value="${url}">
    <c:param name="${pageParamName}" value="${page}" />
    <c:forEach var="entry" items="${pageContext.request.parameterMap}">
        <c:if test="${entry.key ne pageParamName}">
            <c:forEach var="value" items="${entry.value}">
                <c:param name="${entry.key}" value="${value}" />
            </c:forEach>
        </c:if>
    </c:forEach>
</c:url>
<a href="<c:out value='${completeUrl}'/>" class="title-link">
    <jsp:doBody />
</a>
