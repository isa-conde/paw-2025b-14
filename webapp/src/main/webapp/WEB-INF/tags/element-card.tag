<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="image" required="true" rtexprvalue="true" %>
<%@ attribute name="game" required="false" rtexprvalue="true" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="start_date" required="false" type="java.time.LocalDate" %>
<%@attribute name="end_date" required="false" type="java.time.LocalDate" %>
<%@ attribute name="tags" required="false" rtexprvalue="true" type="java.util.List" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" %>

<c:set var="hasDate" value="${not empty start_date && not empty end_date}"/>
<c:set var="url" value="${pageContext.request.contextPath}/${isGame == 'true' ? 'game?game_id=' : 'tournament?tournamentId='}${id}"/>

<a href="${url}" class="element-card">
    <img src="${image}" alt="Background" class="element-card-image">
    <div class="element-card-content game">
        <paw:text type="title" size="xs"><c:out value="${game}"/></paw:text>
    </div>
    <div class="element-card-content">
        <paw:text type="title" size="s"><c:out value="${title}"/></paw:text>
        <c:if test="${hasDate}">
            <div class="date-container">
                <paw:datetime date="${start_date}" size="s" weight="thin"/>
                <paw:text size="s" weight="thin"> - </paw:text>
                <paw:datetime date="${end_date}" size="s" weight="thin"/>
            </div>
        </c:if>
    </div>
</a>
