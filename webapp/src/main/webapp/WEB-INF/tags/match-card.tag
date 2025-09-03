<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchId" required="true" rtexprvalue="true" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="localPlayer" required="true" rtexprvalue="true" %>
<%@ attribute name="visitorPlayer" required="true" rtexprvalue="true" %>
<%@ attribute name="localPlayerId" required="true" rtexprvalue="true" %>
<%@ attribute name="visitorPlayerId" required="true" rtexprvalue="true" %>
<%@ attribute name="winner" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="match-card">
    <div class="match-players">
        <div class="player local-player ${winner == 1 ? 'winner' : ''}">
            <img src="${pageContext.request.contextPath}/images/empty_user.png" alt="Player" class="player-avatar"/>
            <paw:text size="s" weight="semi-bold">${localPlayer}</paw:text>
        </div>
        
        <div class="vs-container">
            <paw:text size="xs" weight="bold">VS</paw:text>
        </div>
        
        <div class="player visitor-player ${winner == 2 ? 'winner' : ''}">
            <img src="${pageContext.request.contextPath}/images/empty_user.png" alt="Player" class="player-avatar"/>
            <paw:text size="s" weight="semi-bold">${visitorPlayer}</paw:text>
        </div>
    </div>
    
    <c:if test="${winner == 0}">
        <div class="match-actions">
            <form method="post" action="${pageContext.request.contextPath}/tournament/setWinner" style="display: inline;">
                <input type="hidden" name="matchId" value="${matchId}"/>
                <input type="hidden" name="tournamentId" value="${tournamentId}"/>
                <input type="hidden" name="winner" value="1"/>
                <button type="submit" class="action-button local-win">
                    <paw:text size="xs">${localPlayer} Wins</paw:text>
                </button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/tournament/setWinner" style="display: inline;">
                <input type="hidden" name="matchId" value="${matchId}"/>
                <input type="hidden" name="tournamentId" value="${tournamentId}"/>
                <input type="hidden" name="winner" value="2"/>
                <button type="submit" class="action-button visitor-win">
                    <paw:text size="xs">${visitorPlayer} Wins</paw:text>
                </button>
            </form>
        </div>
    </c:if>
    
    <c:if test="${winner != 0}">
        <div class="match-result">
            <div class="result-text">
                <paw:text size="xs" weight="bold">
                    ${winner == 1 ? localPlayer : visitorPlayer} Won
                </paw:text>
            </div>
        </div>
    </c:if>
</div>
