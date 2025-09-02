<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchId" required="true" rtexprvalue="true" %>
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
        
        <div class="player visitor-player ${winner == 0 ? 'winner' : ''}">
            <img src="${pageContext.request.contextPath}/images/empty_user.png" alt="Player" class="player-avatar"/>
            <paw:text size="s" weight="semi-bold">${visitorPlayer}</paw:text>
        </div>
    </div>
    
    <c:if test="${winner == null}">
        <div class="match-actions">
            <button class="action-button local-win" onclick="setWinner(${matchId}, ${localPlayerId})">
                <paw:text size="xs">${localPlayer} Wins</paw:text>
            </button>
            <button class="action-button visitor-win" onclick="setWinner(${matchId}, ${visitorPlayerId})">
                <paw:text size="xs">${visitorPlayer} Wins</paw:text>
            </button>
        </div>
    </c:if>
    
    <c:if test="${winner != null}">
        <div class="match-result">
            <div class="result-text">
                <paw:text size="xs" weight="bold">
                    ${winner == 1 ? localPlayer : visitorPlayer} Won
                </paw:text>
            </div>
        </div>
    </c:if>
</div>
