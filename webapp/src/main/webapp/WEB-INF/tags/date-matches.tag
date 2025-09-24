<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="dateNumber" required="true" rtexprvalue="true" %>
<%@ attribute name="matches" required="true" type="java.util.List" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ attribute name="tournamentStructure" required="false" rtexprvalue="true" %>
<%@ attribute name="groupNumber" required="false" rtexprvalue="true" %>
<%@ attribute name="totalMatches" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="date-section">
    <div class="date-header">
        <c:choose>
            <c:when test="${tournamentStructure == 'ELIMINATION' || (tournamentStructure == 'HYBRID' && groupNumber == 0)}">
                <c:set var="participantsInRound" value="${totalMatches * 2}"/>
                
                <c:choose>
                    <c:when test="${participantsInRound >= 64}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.roundOf64"/></paw:text>
                    </c:when>
                    <c:when test="${participantsInRound >= 32}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.roundOf32"/></paw:text>
                    </c:when>
                    <c:when test="${participantsInRound >= 16}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.roundOf16"/></paw:text>
                    </c:when>
                    <c:when test="${participantsInRound >= 8}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.quarterFinal"/></paw:text>
                    </c:when>
                    <c:when test="${participantsInRound >= 4}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.semiFinal"/></paw:text>
                    </c:when>
                    <c:when test="${participantsInRound >= 2}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.final"/></paw:text>
                    </c:when>
                    <c:otherwise>
                        <paw:text type="title" size="m"><spring:message code="dateMatch.other" arguments="${participantsInRound}"/></paw:text>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <paw:text type="title" size="m">Date ${dateNumber}</paw:text>
            </c:otherwise>
        </c:choose>
    </div>
    
    <div class="matches-grid">
        <c:forEach var="match" items="${matches}">
            <paw:match-card 
                matchId="${match.id}"
                tournamentId="${tournamentId}"
                localPlayer="${match.localPlayerName}"
                visitorPlayer="${match.visitorPlayerName}"
                localPlayerId="${match.localId}"
                visitorPlayerId="${match.visitorId}"
                winner="${match.winner}"
                isCreator="${isCreator}"/>
        </c:forEach>
    </div>
</div>
