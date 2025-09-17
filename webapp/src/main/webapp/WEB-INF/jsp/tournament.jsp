<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/tournament/join" var="joinUrl"/>

<c:set var="isParticipant" value="false"/>



<paw:layout user="${user}" isIndex="false">
  <c:choose>
    <c:when test="${user == null}">
      <paw:no-access/>
    </c:when>
    <c:otherwise>
        <c:forEach var="participant" items="${participants}">
            <c:if test="${participant.user_id == user.id}">
                <c:set var="isParticipant" value="true"/>
            </c:if>
        </c:forEach>
        <paw:banner image="data:image/png;base64,${tournamentImg.base64Img}">
            <paw:text type="title" size="m" stroke="true">${game.name}</paw:text>
            <paw:text type="title" size="xl" stroke="true"><c:out value="${tournamentImg.tournament.name}"/></paw:text>
            <div class="date-container">
                <paw:datetime date="${tournamentImg.tournament.start_date}" weight="semi-bold"/>
                <paw:text weight="semi-bold"> - </paw:text>
                <paw:datetime date="${tournamentImg.tournament.end_date}" weight="semi-bold"/>
            </div>
            <br/>
            <div class="organizer-container">
                <paw:text size="s"><spring:message code="tournament.organizedBy"/></paw:text>
                <paw:profileButton text="${creator.username}" onclick="" size="xs" fill="false" disabled="true" isNotSafe="true"/>
            </div>
        </paw:banner>
        <spring:message code="tournament.overview" var="overview"/>
        <spring:message code="tournament.matches" var="matches"/>
        <c:set var="navbarSections" value="${[overview, matches]}"/>
        <c:set var="activeSection" value="${param.section != null ? param.section : overview}"/>
        <c:set var="isCreator" value="${user.id == tournamentImg.tournament.creator_id}"/>
        <paw:navbar sections="${navbarSections}" activeSection="${activeSection}"/>
        <div class="content-container">
            <c:choose>
                <c:when test="${activeSection == overview}">
                    <div class="icon-card-container">
                        <spring:message code="tournament.teams" var="teams"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournamentImg.tournament.region}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournamentImg.tournament.format}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${tournamentImg.tournament.elo}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${tournamentImg.tournament.max_participants} ${teams}"/>
                    </div>
                    <div class="cards-container">
                        <c:choose>
                            <c:when test="${!hasJoined && tournamentImg.tournament.openInscriptions}">
                                <paw:button-card
                                        title="tournament.joinCard.title"
                                        text="tournament.joinCard.text"
                                        butText="tournament.joinCard.butText"
                                        icon="${pageContext.request.contextPath}/images/grid.png"
                                        method="post"
                                        onclick="${joinUrl}"
                                        tournamentId="${tournamentImg.tournament.id}"
                                        texture="true"/>
                            </c:when>
                        </c:choose>
                    </div>
                    <c:if test="${not empty participants}">
                        <paw:text type="title" size="l"><spring:message code="tournament.participants.title"/></paw:text>
                        <paw:board participants="${participants}"/>
                    </c:if>
                    <c:if test="${user.id == tournamentImg.tournament.creator_id && tournamentImg.tournament.openInscriptions}">
                        <div class="cards-container">
                            <form method="post" action="${pageContext.request.contextPath}/tournament/closeInscriptions" style="display: inline;">
                                <input type="hidden" name="tournamentId" value="${tournamentImg.tournament.id}"/>
                                <button type="submit" class="btn"><paw:text size="l"><spring:message code="tournament.closeInscriptions"/></paw:text></button>
                            </form>
                        </div>
                    </c:if>
                </c:when>
                <c:when test="${activeSection == matches}">
                    <c:choose>
                        <c:when test="${empty matchesByStage}">
                            <div class="no-cards-container">
                                <paw:text size="l" weight="thin"><spring:message code="tournament.noMatches"/></paw:text>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="stageEntry" items="${matchesByStage}">
                                <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}"/>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:when>
            </c:choose>
        </div>
    </c:otherwise>
  </c:choose>
</paw:layout>