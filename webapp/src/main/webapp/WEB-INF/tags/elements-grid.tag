<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="true" type="java.util.List" %>
<%@ attribute name="headerElements" required="false" type="java.util.List" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="tournamentGame" value=""/>
<c:set var="hasHeader" value="${not empty headerElements}"/>



<c:choose>
    <c:when test="${elements.size() <= '0'}">
        <spring:message code="elementGrid.noGames" var="noGames"/>
        <spring:message code="elementGrid.noTournaments" var="noTournaments"/>
        <div class="no-cards-container"><paw:text size="l" weight="thin">${isGame? noGames : noTournaments}</paw:text></div>
    </c:when>
    <c:otherwise>
        <div class="grid">
            <c:forEach var="e" items="${elements}">
                <c:choose>
                    <c:when test="${isGame}">
                            <paw:element-card
                                    image="${pageContext.request.contextPath}/image/${e.image_id}"
                                    title="${e.name}"
                                    id="${e.id}"
                                    isGame="true"/>
                    </c:when>
                    <c:otherwise>
                            <c:if test="${hasHeader}">
                                <c:forEach var="game" items="${headerElements}">
                                    <c:if test="${game.id == e.game_id}">
                                        <c:set var="tournamentGame" value="${game}"/>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                            <paw:element-card
                                    image="${pageContext.request.contextPath}/image/${e.image_id}"
                                    title="${e.name}"
                                    start_date="${e.start_date}"
                                    end_date="${e.end_date}"
                                    game="${hasHeader? tournamentGame.name : ''}"
                                    id="${e.id}"
                                    isGame="false"/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>