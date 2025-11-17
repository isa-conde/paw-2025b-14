<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="true" type="java.util.List" %>
<%@ attribute name="headerElements" required="false" type="java.util.List" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="isUser" required="false" type="java.lang.Boolean" %>
<%@ attribute name="isTeam" required="false" type="java.lang.Boolean" %>
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
                                    image="${pageContext.request.contextPath}/image/${e.imageId}"
                                    title="${e.name}"
                                    id="${e.id}"
                                    isGame="true"/>
                    </c:when>
                    <c:when test="${isUser}">
                        <paw:profile-card isUser="${true}" userProfile="${e}"/>
                    </c:when>
                    <c:when test="${isTeam}">
                        <paw:profile-card isTeam="${true}" teamProfile="${e}"/>
                    </c:when>
                    <c:otherwise>
                            <c:if test="${hasHeader}">
                                <c:forEach var="game" items="${headerElements}">
                                    <c:if test="${game.id == e.gameId}">
                                        <c:set var="tournamentGame" value="${game}"/>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                            <paw:element-card
                                    image="${pageContext.request.contextPath}/image/${e.imageId}"
                                    title="${e.name}"
                                    started="${e.tournamentStarted}"
                                    finished="${e.isFinished}"
                                    game="${hasHeader? tournamentGame.name : ''}"
                                    id="${e.id}"
                                    isGame="false"/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>