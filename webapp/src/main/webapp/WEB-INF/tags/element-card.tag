<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="image" required="true" rtexprvalue="true" %>
<%@ attribute name="game" required="false" rtexprvalue="true" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="tags" required="false" rtexprvalue="true" type="java.util.List" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" %>
<%@ attribute name="started" required="false" type="java.lang.Boolean" %>
<%@ attribute name="finished" required="false" type="java.lang.Boolean" %>

<c:set var="hasChip" value="${not empty started && not empty finished && !isGame}"/>
<c:set var="url" value="${pageContext.request.contextPath}/${isGame == 'true' ? 'tournamentsPage?gameId=' : 'tournament/'}${id}"/>
<a href="${url}" class="element-card">
    <img src="${image}" alt="Background" class="element-card-image">
    <div class="element-card-content game">
        <paw:text type="title" size="xs" stroke="true"><c:out value="${game}"/></paw:text>
    </div>
    <div class="element-card-content">
        <paw:text type="title" size="s" stroke="true"><c:out value="${title}"/></paw:text>
        <c:if test="${hasChip}">
            <div class="date-chip">
            <c:choose>
                <c:when test="${started && !finished}">
                    <spring:message code="card.inProgress"/>
                </c:when>
                <c:when test="${finished}">
                    <spring:message code="card.finished"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="card.comingSoon"/>
                </c:otherwise>
            </c:choose>
            </div>
        </c:if>
    </div>
</a>
