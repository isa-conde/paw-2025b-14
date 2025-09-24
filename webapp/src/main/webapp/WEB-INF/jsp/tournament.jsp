<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/tournament/join" var="joinUrl"/>

<c:set var="isParticipant" value="false"/>

<paw:layout user="${user}">
    <c:forEach var="group" items="${participants}">
        <c:forEach var="participant" items="${group.value}">
            <c:if test="${participant.user_id == user.id}">
                <c:set var="isParticipant" value="true"/>
            </c:if>
        </c:forEach>
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
    <c:set var="sections" value="${['overview','matches']}"/>
    <c:set var="labels" value="${[overview, matches]}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'overview'}"/>
    <paw:navbar sections="${sections}" labels="${labels}" activeSection="${activeSection}"/>

    <c:set var="isCreator" value="${user.id == tournamentImg.tournament.creator_id}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'overview'}">
                <div class="icon-card-container">
                    <spring:message code="tournament.teams" var="teams"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournamentImg.tournament.region}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournamentImg.tournament.format}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${tournamentImg.tournament.elo}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${tournamentImg.tournament.max_participants} ${teams}"/>
                </div>
                <c:choose>
                    <c:when test="${tournamentWinner != null && tournamentWinner > 0}">
                        <c:forEach var="g" items="${participants}">
                            <c:if test="${g.key == 0}">
                                <c:forEach var="p" items="${g.value}">
                                    <c:if test="${p.user_id == tournamentWinner}">
                                        <div class="cards-container">
                                            <paw:winner-card winnerName="${p.username}"/>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:when>
                </c:choose>
                <div class="cards-container">
                    <c:choose>
                        <c:when test="${!hasJoined && tournamentImg.tournament.openInscriptions}">
                            <c:choose>
                                <c:when test="${tournamentImg.tournament.structure == LEAGUE}">
                                    <c:set var="title" value="tournament.joinCard.title.league" />
                                    <c:set var="text" value="tournament.joinCard.text.league" />
                                    <c:set var="icon" value="grid.png" />
                                </c:when>
                                <c:when test="${tournamentImg.tournament.structure == ELIMINATION}">
                                    <c:set var="title" value="tournament.joinCard.title.elimination" />
                                    <c:set var="text" value="tournament.joinCard.text.elimination" />
                                    <c:set var="icon" value="bracket.png" />
                                </c:when>
                                <c:when test="${tournamentImg.tournament.structure == HYBRID}">
                                    <c:set var="title" value="tournament.joinCard.title.hybrid" />
                                    <c:set var="text" value="tournament.joinCard.text.hybrid" />
                                    <c:set var="icon" value="bracket.png" />
                                </c:when>
                            </c:choose>

                            <paw:button-card
                                    title="${title}"
                                    text="${text}"
                                    butText="tournament.joinCard.butText"
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
                            <paw:text type="title" size="l"><spring:message code="tournament.standings"/></paw:text>
                            <paw:board participants="${participants}"/>
                            <c:forEach var="g" items="${participants}">
                                <c:if test="${g.key == 0}">
                                    <paw:board participants="${g.value}"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </c:when>
                    <c:when test="${tournamentImg.tournament.structure == ELIMINATION}">
                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                            <c:if test="${groupEntry.key == 0}">
                                <paw:text type="title" size="l">
                                    <spring:message code="tournament.standings"/>
                                </paw:text>
                                <paw:bracket matchesByStage="${groupEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}"/>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:when test="${tournamentImg.tournament.structure == 'HYBRID'}">
                        <c:choose>
                            <c:when test="${not empty participants}">
                                <c:set var="hasGroupZero" value="false" />
                                <c:forEach var="g" items="${participants}">
                                    <c:if test="${g.key == 0}">
                                        <c:set var="hasGroupZero" value="true" />
                                    </c:if>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${hasGroupZero}">
                                        <paw:text type="title" size="l">
                                            <spring:message code="tournament.standings"/>
                                        </paw:text>
                                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                                            <c:if test="${groupEntry.key == 0}">
                                                <paw:bracket matchesByStage="${groupEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}"/>
                                            </c:if>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="g" items="${participants}">
                                            <paw:text type="title" size="l">
                                                <spring:message code="tournament.groupStandings" arguments="${g.key}"/>
                                            </paw:text>
                                            <paw:board participants="${g.value}"/>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                        </c:choose>
                    </c:when>
                </c:choose>
                <c:if test="${user.id == tournamentImg.tournament.creator_id && tournamentImg.tournament.openInscriptions}">
                    <div class="cards-container">
                        <form method="post" action="${pageContext.request.contextPath}/tournament/closeInscriptions" style="display: inline;">
                            <input type="hidden" name="tournamentId" value="${tournamentImg.tournament.id}"/>
                            <button type="submit" class="btn"><paw:text size="l"><spring:message code="tournament.closeInscriptions"/></paw:text></button>
                        </form>
                    </div>
                </c:if>
            </c:when>
            <c:when test="${activeSection == 'matches'}">
                <c:choose>
                    <c:when test="${empty matchesByGroup}">
                        <div class="no-cards-container">
                            <paw:text size="l" weight="thin"><spring:message code="tournament.noMatches"/></paw:text>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${tournamentImg.tournament.structure == 'HYBRID'}">
                                <c:set var="hasGroupZero" value="false" />
                                <c:set var="groupZeroMatches" value="0" />
                                <c:forEach var="groupEntry" items="${matchesByGroup}">
                                    <c:if test="${groupEntry.key == 0}">
                                        <c:set var="hasGroupZero" value="true" />
                                        <c:forEach var="stageEntry" items="${groupEntry.value}">
                                            <c:set var="groupZeroMatches" value="${groupZeroMatches + stageEntry.value.size()}" />
                                        </c:forEach>
                                    </c:if>
                                </c:forEach>

                                <c:choose>
                                    <c:when test="${hasGroupZero && groupZeroMatches > 0}">
                                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                                            <c:if test="${groupEntry.key == 0}">
                                                <c:forEach var="stageEntry" items="${groupEntry.value}">
                                                    <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournamentImg.tournament.structure}" groupNumber="${groupEntry.key}" totalMatches="${stageEntry.value.size()}"/>
                                                </c:forEach>
                                            </c:if>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="subActiveGroup" value="${param.group != null ? param.group : groupSections[0]}"/>

                                        <paw:navbar sections="${groupSections}"
                                                    labels="${groupLabels}"
                                                    activeSection="${subActiveGroup}"
                                                    paramName="group"/>


                                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                                            <c:if test="${subActiveGroup == groupEntry.key}">
                                                <c:forEach var="stageEntry" items="${groupEntry.value}">
                                                    <paw:date-matches
                                                            dateNumber="${stageEntry.key}"
                                                            matches="${stageEntry.value}"
                                                            tournamentId="${tournamentImg.tournament.id}"
                                                            isCreator="${isCreator}"
                                                            tournamentStructure="${tournamentImg.tournament.structure}"
                                                            groupNumber="${groupEntry.key}"
                                                            totalMatches="${stageEntry.value.size()}"/>
                                                </c:forEach>
                                            </c:if>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="groupEntry" items="${matchesByGroup}">
                                    <c:if test="${groupEntry.key == 0}">
                                        <c:forEach var="stageEntry" items="${groupEntry.value}">
                                             <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournamentImg.tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournamentImg.tournament.structure}" groupNumber="${groupEntry.key}" totalMatches="${stageEntry.value.size()}"/>
                                        </c:forEach>
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</paw:layout>