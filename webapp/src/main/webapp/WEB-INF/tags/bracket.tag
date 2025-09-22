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
            <c:set var="participantsInStage" value="${stageEntry.value.size() * 2}"/>

            <div class="bracket-round">
                <c:choose>
                    <c:when test="${participantsInStage >= 64}">
                        <paw:text type="title" size="m">Round of 64</paw:text>
                    </c:when>
                    <c:when test="${participantsInStage >= 32}">
                        <paw:text type="title" size="m">Round of 32</paw:text>
                    </c:when>
                    <c:when test="${participantsInStage >= 16}">
                        <paw:text type="title" size="m">Round of 16</paw:text>
                    </c:when>
                    <c:when test="${participantsInStage >= 8}">
                        <paw:text type="title" size="m">Quarter Final</paw:text>
                    </c:when>
                    <c:when test="${participantsInStage >= 4}">
                        <paw:text type="title" size="m">Semi Final</paw:text>
                    </c:when>
                    <c:when test="${participantsInStage >= 2}">
                        <paw:text type="title" size="m">Final</paw:text>
                    </c:when>
                    <c:otherwise>
                        <paw:text type="title" size="m">Round of ${participantsInStage}</paw:text>
                    </c:otherwise>
                </c:choose>
                <div class="bracket-matches">
                    <c:forEach var="match" items="${stageMatches}">
                        <div class="bracket-match">
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
