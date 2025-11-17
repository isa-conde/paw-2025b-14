<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="dateNumber" required="true" rtexprvalue="true" %>
<%@ attribute name="maxStage" required="false" rtexprvalue="true" %>
<%@ attribute name="matches" required="true" type="java.util.List" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ attribute name="tournamentStructure" required="false" rtexprvalue="true" %>
<%@ attribute name="groupStage" required="false" rtexprvalue="true" %>
<%@ attribute name="groupNumber" required="false" rtexprvalue="true" %>
<%@ attribute name="totalMatches" required="false" rtexprvalue="true" %>
<%@ attribute name="setMatchResultsForm" required="false" type="ar.edu.itba.paw.webapp.form.SetMatchResultsForm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="isGroupStage" value="${not empty groupStage? groupStage : 'false'}"/>

<div class="date-section">
    <div class="date-header">
        <c:choose>
            <c:when test="${tournamentStructure == 'ELIMINATION' || (tournamentStructure == 'HYBRID' && !groupStage)}">
                <c:set var="diff" value="${maxStage - dateNumber}"/>
                <c:set var="participantsInRound" value="${totalMatches * 2}"/>

                <c:choose>
                    <c:when test="${diff == 0}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.final"/></paw:text>
                    </c:when>
                    <c:when test="${diff == 1}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.semiFinal"/></paw:text>
                    </c:when>
                    <c:when test="${diff == 2}">
                        <paw:text type="title" size="m"><spring:message code="dateMatch.quarterFinal"/></paw:text>
                    </c:when>
                    <c:otherwise>
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
                            <c:otherwise>
                                <paw:text type="title" size="m">
                                    <spring:message code="dateMatch.other" arguments="${participantsInRound}"/>
                                </paw:text>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <c:otherwise>
                <spring:message code="tournament.date" arguments="${dateNumber}" var="dateLabel"/>
                <paw:text type="title" size="m">${dateLabel}</paw:text>
            </c:otherwise>
        </c:choose>
    </div>
    <c:url value="/images/pencil.png" var="pencilUrl"/>
    <div class="matches-grid">
        <c:forEach var="match" items="${matches}">
            <c:if test="${empty groupNumber or match.local.groupNumber eq groupNumber}">
                <div class="match-row">
                    <div class="left">
                        <paw:match-card
                                matchId="${match.id}"
                                tournamentId="${tournamentId}"
                                localPlayer="${match.local}"
                                visitorPlayer="${match.visitor}"
                                localScore="${match.localScore}"
                                visitorScore="${match.visitorScore}"
                                winner="${match.winner}"
                                groupNumber="${groupNumber}"
                                isCreator="${isCreator}"/>
                    </div>
                    <div class="right">
                        <c:choose>
                            <c:when test="${match.local == null || match.visitor == null}">
                                <paw:text size="m" weight="semi-bold">-</paw:text>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${match.localScore == null || match.visitorScore == null}">
                                        <c:choose>
                                            <c:when test="${isCreator}">
                                                <paw:button onclick="openSetMatchModal(${match.id})" image="${pencilUrl}" size="m" secondary="true"/>
                                            </c:when>
                                            <c:otherwise>
                                                <paw:text size="m" weight="semi-bold"><spring:message code="tournament.match.notPlayed"/></paw:text>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${match.date == null}">
                                                <paw:text size="m" weight="thin"><spring:message code="tournament.match.noDate"/></paw:text>
                                            </c:when>
                                            <c:otherwise>
                                                <paw:datetime date="${match.date}" weight="semi-bold"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>
