<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="image" required="true" rtexprvalue="true" %>
<%@ attribute name="game" required="false" rtexprvalue="true" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="startDate" required="false" type="java.time.LocalDate" %>
<%@attribute name="endDate" required="false" type="java.time.LocalDate" %>
<%@ attribute name="tags" required="false" rtexprvalue="true" type="java.util.List" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" %>

<c:set var="hasDate" value="${not empty startDate && not empty endDate}"/>
<c:set var="url" value="${pageContext.request.contextPath}/${isGame == 'true' ? 'tournamentsPage?gameId=' : 'tournament/'}${id}"/>

<a href="${url}" class="element-card">
    <img src="${image}" alt="Background" class="element-card-image">
    <div class="element-card-content game">
        <paw:text type="title" size="xs" stroke="true"><c:out value="${game}"/></paw:text>
    </div>
    <div class="element-card-content">
        <paw:text type="title" size="s" stroke="true"><c:out value="${title}"/></paw:text>
        <c:if test="${hasDate}">
            <div class="element-card-date">
                <paw:datetime date="${startDate}" size="s" weight="semi-bold"/>
                <paw:text size="s" weight="semi-bold"> - </paw:text>
                <paw:datetime date="${endDate}" size="s" weight="semi-bold"/>
            </div>
        </c:if>
    </div>
</a>
