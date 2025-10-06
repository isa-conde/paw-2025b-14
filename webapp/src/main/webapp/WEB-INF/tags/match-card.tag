<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchId" required="true" rtexprvalue="true" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="localPlayer" required="true" rtexprvalue="true" type="ar.edu.itba.paw.model.Participant" %>
<%@ attribute name="visitorPlayer" required="true" rtexprvalue="true" type="ar.edu.itba.paw.model.Participant" %>
<%@ attribute name="localPlayerId" required="true" rtexprvalue="true" %>
<%@ attribute name="visitorPlayerId" required="true" rtexprvalue="true" %>
<%@ attribute name="winner" required="false" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ attribute name="groupNumber" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:set var="localName" value="${localPlayer == null ? 'TBD' : localPlayer.name}"/>
<c:set var="visitorName" value="${visitorPlayer == null ? 'TBD' : visitorPlayer.name}"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="localPfp" value="${localPlayer == null ? contextPath.concat('/images/empty_user.png') : contextPath.concat('/pfp/').concat(localPlayer.pfp_id)}"/>
<c:set var="visitorPfp" value="${visitorPlayer == null ? contextPath.concat('/images/empty_user.png') : contextPath.concat('/pfp/').concat(visitorPlayer.pfp_id)}"/>

<div class="match-card">
    <div class="match-players">
        <div class="player local-player ${winner == 1 ? 'winner' : ''}">
            <img src="${localPfp}" alt="Local Player" class="player-avatar"/>
            <paw:text size="s" weight="semi-bold"><c:out value="${localName}"/></paw:text>
        </div>
        
        <div class="vs-container">
            <paw:text size="xs" weight="bold">VS</paw:text>
        </div>
        
        <div class="player visitor-player ${winner == 2 ? 'winner' : ''}">
            <img src="${visitorPfp}" alt="Visitor Player" class="player-avatar"/>
            <paw:text size="s" weight="semi-bold"><c:out value="${visitorName}"/></paw:text>
        </div>
    </div>
    <spring:message code="tournament.wins" var="wins"/>
    <c:if test="${isCreator == true && winner == 0 && localPlayerId > 0 && visitorPlayerId > 0}">
        <div class="match-actions">
            <form method="post" action="${pageContext.request.contextPath}/tournament/setWinner" style="display: inline;">
                <input type="hidden" name="matchId" value="${matchId}"/>
                <input type="hidden" name="tournamentId" value="${tournamentId}"/>
                <input type="hidden" name="winner" value="1"/>
                <button type="submit" class="action-button local-win">
                    <paw:text size="xs"><c:out value="${localPlayer} ${wins}"/></paw:text>
                </button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/tournament/setWinner" style="display: inline;">
                <input type="hidden" name="matchId" value="${matchId}"/>
                <input type="hidden" name="tournamentId" value="${tournamentId}"/>
                <input type="hidden" name="group" value="${groupNumber}"/>
                <input type="hidden" name="winner" value="2"/>
                <button type="submit" class="action-button visitor-win">
                    <paw:text size="xs"><c:out value="${visitorPlayer} ${wins}"/></paw:text>
                </button>
            </form>
        </div>
    </c:if>
</div>
