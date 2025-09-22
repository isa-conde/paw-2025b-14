<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchesByStage" required="true" type="java.util.Map" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="bracket-container">
    <div class="bracket">
        <c:forEach var="stageEntry" items="${matchesByStage}">
            <c:set var="stageMatches" value="${stageEntry.value}"/>
            <div class="bracket-round">
                <div class="bracket-matches">
                    <c:forEach var="match" items="${stageMatches}" varStatus="loop">
                        <div class="bracket-match round-${stageEntry.key} match-${loop.index}">
                            <div class="bracket-team ${match.winner == 1 ? 'winner' : ''}">
                                <span><c:out value="${match.localPlayerName}"/></span>
                            </div>
                            <div class="bracket-team ${match.winner == 2 ? 'winner' : ''}">
                                <span><c:out value="${match.visitorPlayerName}"/></span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
