<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="page" required="true" %>
<%@ attribute name="url" required="true" %>
<c:url var="completeUrl" value="${url}">
    <c:param name="page" value="${page}" />
    <c:forEach var="entry" items="${pageContext.request.parameterMap}">
        <c:if test="${entry.key ne 'page'}">
            <c:forEach var="value" items="${entry.value}">
                <c:param name="${entry.key}" value="${value}" />
            </c:forEach>
        </c:if>
    </c:forEach>
</c:url>
<a href="<c:out value='${completeUrl}'/>" class="title-link">
    <jsp:doBody />
</a>
