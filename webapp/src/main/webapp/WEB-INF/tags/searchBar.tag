<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<form action="${pageContext.request.contextPath}/search" method="get">
    <div class="search-container">
        <input name="q" type="text" class="search-bar"
               placeholder="<spring:message code='searchBar.placeHolder'/>"
               value="<c:out value="${param.q}"/>">
        <img src="${pageContext.request.contextPath}/images/search.png"
             alt="<spring:message code="searchBar.placeHolder"/>"
             class="search-icon"
        />
    </div>
</form>
