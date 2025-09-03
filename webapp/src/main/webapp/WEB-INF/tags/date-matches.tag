<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="dateNumber" required="true" rtexprvalue="true" %>
<%@ attribute name="matches" required="true" type="java.util.List" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="date-section">
    <div class="date-header">
        <paw:text type="title" size="m">Date ${dateNumber}</paw:text>
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
