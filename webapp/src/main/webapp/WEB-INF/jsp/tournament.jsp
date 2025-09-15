<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:url value="/tournament/join" var="joinUrl"/>

<paw:layout user="${user}" isIndex="false">
  <c:choose>
    <c:when test="${user == null}">
      <paw:no-access/>
    </c:when>
    <c:otherwise>
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
                <paw:text size="s">Organized by</paw:text>
                <paw:profileButton text="${creator.username}" onclick="" size="xs" fill="false" disabled="true"/>
            </div>
        </paw:banner>
        <c:set var="navbarSections" value="${['Overview', 'Matches']}"/>
        <c:set var="activeSection" value="${param.section != null ? param.section : 'Overview'}"/>
        <c:set var="isCreator" value="${user.id == tournamentImg.tournament.creator_id}"/>
        <paw:navbar sections="${navbarSections}" activeSection="${activeSection}"/>
        <div class="content-container">
            <c:choose>
                <c:when test="${activeSection == 'Overview'}">
                    <div class="icon-card-container">
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournamentImg.tournament.region}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournamentImg.tournament.format}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${tournamentImg.tournament.elo}"/>
                        <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${tournamentImg.tournament.max_participants} Teams"/>
                    </div>
                    <div class="cards-container">
                        <c:choose>
                            <c:when test="${!hasJoined && tournamentImg.tournament.openInscriptions}">
                                <c:choose>
                                    <c:when test="${tournamentImg.tournament.structure == LEAGUE}">
                                        <c:set var="title" value="Become the first in the league" />
                                        <c:set var="text" value="Play against all your oponents to collect points and win the tournament" />
                                        <c:set var="icon" value="grid.png" />
                                    </c:when>
                                    <c:when test="${tournamentImg.tournament.structure == ELIMINATION}">
                                        <c:set var="title" value="Beat them all and take the prize" />
                                        <c:set var="text" value="Avoid being eliminated by winning every match" />
                                        <c:set var="icon" value="bracket.png" />
                                    </c:when>
                                    <c:when test="${tournamentImg.tournament.structure == HYBRID}">
                                        <c:set var="title" value="Get advantage and beat them all" />
                                        <c:set var="text" value="Get a top position in your team and then win every match" />
                                        <c:set var="icon" value="bracket.png" />
                                    </c:when>
                                </c:choose>

                                <paw:button-card
                                        title="${title}"
                                        text="${text}"
                                        butText="Join Tournament"
                                        icon="${pageContext.request.contextPath}/images/${icon}"
                                        method="post"
                                        onclick="${joinUrl}"
                                        tournamentId="${tournamentImg.tournament.id}"
                                        texture="true"/>
                            </c:when>
                        </c:choose>
                    </div>
                    <c:choose>
                        <c:when test="${tournamentImg.tournament.structure == LEAGUE}">
                            <c:if test="${not empty participants}">
                                <paw:text type="title" size="l">Standings</paw:text>
                                <paw:board participants="${participants[0]}"/>
                            </c:if>
                        </c:when>
                        <c:when test="${tournamentImg.tournament.structure == ELIMINATION}">
                            <!-- Show bracket -->
                        </c:when>
                        <c:when test="${tournamentImg.tournament.structure == 'HYBRID'}">
                            <c:choose>
                                <c:when test="${not empty participants[0]}">
                                    <!-- Show bracket -->
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="g" items="${participants}">
                                        <paw:text type="title" size="l">
                                            Group ${g.key} Standings
                                        </paw:text>
                                        <paw:board participants="${g.value}"/>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                    </c:choose>
                    <c:if test="${user.id == tournamentImg.tournament.creator_id && tournamentImg.tournament.openInscriptions}">
                        <div class="cards-container">
                            <form method="post" action="${pageContext.request.contextPath}/tournament/closeInscriptions" style="display: inline;">
                                <input type="hidden" name="tournamentId" value="${tournamentImg.tournament.id}"/>
                                <button type="submit" class="btn"><paw:text size="l">Close Inscriptions</paw:text></button>
                            </form>
                        </div>
                    </c:if>
                </c:when>
                <c:when test="${activeSection == 'Matches'}">
                    <c:choose>
                        <c:when test="${tournamentImg.tournament.structure == 'HYBRID' && empty matchesByGroup[0]}">
                            <c:set var="subNavbarSections" value="${[]}" />
                            <c:forEach var="groupEntry" items="${matchesByGroup}">
                                <c:set var="subNavbarSections" value="${subNavbarSections += ['Group ' += groupEntry.key]}" />
                            </c:forEach>
                            <c:set var="subActiveSection" value="${param.section != null ? param.section : subNavbarSections[0]}" />

                            <paw:navbar sections="${subNavbarSections}" activeSection="${subActiveSection}" />
                            <c:forEach var="groupEntry" items="${matchesByGroup}">
                                <c:if test="${subActiveSection == ('Group ' += groupEntry.key)}">
                                    <c:forEach var="stageEntry" items="${groupEntry.value}">
                                        <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}"/>
                                    </c:forEach>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:when test="${empty matchesByGroup[0]}">
                                <div class="no-cards-container">
                                    <paw:text size="l" weight="thin">No matches available</paw:text>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="stageEntry" items="${matchesByGroup[0]}">
                                    <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}"/>
                                </c:forEach>
                            </c:otherwise>
                        </c:otherwise>
                    </c:choose>
                </c:when>
            </c:choose>
        </div>
    </c:otherwise>
  </c:choose>
</paw:layout>