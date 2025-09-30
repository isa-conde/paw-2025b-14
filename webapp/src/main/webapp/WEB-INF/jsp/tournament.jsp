<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/tournament/join" var="joinUrl"/>

<c:set var="isParticipant" value="false"/>
<c:set var="isCreator" value="${user.id == tournament.creator_id}"/>
<c:set var="editMode" value="${param.edit eq 'true' && isCreator}"/>

<paw:layout user="${user}">
    <c:forEach var="group" items="${participants}">
        <c:forEach var="participant" items="${group.value}">
            <c:if test="${participant.user_id == user.id}">
                <c:set var="isParticipant" value="true"/>
            </c:if>
        </c:forEach>
    </c:forEach>
    <paw:banner image="${pageContext.request.contextPath}/image/${tournament.image_id}">
        <paw:text type="title" size="m" stroke="true">${game.name}</paw:text>
        <paw:text type="title" size="xl" stroke="true"><c:out value="${tournament.name}"/></paw:text>
        <div class="date-container">
            <paw:datetime date="${tournament.start_date}" weight="semi-bold"/>
            <paw:text weight="semi-bold"> - </paw:text>
            <paw:datetime date="${tournament.end_date}" weight="semi-bold"/>
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

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'overview'}">
                <div class="icon-card-container">
                    <spring:message code="tournament.teams" var="teams"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournament.region}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournament.format}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${tournament.elo}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${tournament.max_participants} ${teams}"/>
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
                        <c:when test="${!isParticipant && tournament.openInscriptions}">
                            <c:choose>
                                <c:when test="${tournament.structure == LEAGUE}">
                                    <c:set var="title" value="tournament.joinCard.title.league" />
                                    <c:set var="text" value="tournament.joinCard.text.league" />
                                    <c:set var="icon" value="grid.png" />
                                </c:when>
                                <c:when test="${tournament.structure == ELIMINATION}">
                                    <c:set var="title" value="tournament.joinCard.title.elimination" />
                                    <c:set var="text" value="tournament.joinCard.text.elimination" />
                                    <c:set var="icon" value="bracket.png" />
                                </c:when>
                                <c:when test="${tournament.structure == HYBRID}">
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
                                    tournamentId="${tournament.id}"
                                    texture="true"/>
                        </c:when>
                    </c:choose>
                </div>
                <c:choose>
                    <c:when test="${tournament.structure == LEAGUE}">
                        <c:if test="${not empty participants}">
                            <paw:text type="title" size="l"><spring:message code="tournament.standings"/></paw:text>
                            <c:forEach var="g" items="${participants}">
                                <c:if test="${g.key == 0}">
                                    <paw:board participants="${g.value}"/>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </c:when>
                    <c:when test="${tournament.structure == ELIMINATION || (tournament.structure == HYBRID && !tournament.is_group_stage)}">
                        <c:set var="edit" value="tournament.edit.matches" />
                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                            <c:if test="${groupEntry.key == 0}">
                                <paw:text type="title" size="l">
                                    <spring:message code="tournament.standings"/>
                                </paw:text>
                                <paw:bracket matchesByStage="${groupEntry.value}" tournamentId="${tournament.id}" isCreator="${isCreator}" isEditing="${editMode}" formId="swapMembersForm"/>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:when test="${tournament.structure == HYBRID && tournament.is_group_stage}">
                        <c:set var="edit" value="tournament.edit.groups" />
                        <c:if test="${not empty participants}">
                            <c:forEach var="g" items="${participants}">
                                <div class="board-container">
                                    <paw:text type="title" size="m">
                                        <spring:message code="tournament.groupStandings" arguments="${g.key}"/>
                                    </paw:text>
                                    <paw:board participants="${g.value}" isEditing="${editMode}" size="l" formId="swapGroupsForm"/>
                                </div>
                            </c:forEach>
                        </c:if>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${user.id == tournament.creator_id && tournament.openInscriptions}">
                        <div class="cards-container">
                            <form method="post" action="${pageContext.request.contextPath}/tournament/closeInscriptions">
                                <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                <button type="submit" class="btn"><paw:text size="l"><spring:message code="tournament.closeInscriptions"/></paw:text></button>
                            </form>
                        </div>
                    </c:when>
                    <c:when test="${user.id == tournament.creator_id && !tournament.tournamentStarted}">
                        <div class="cards-container">
                            <c:if test="${tournament.structure eq 'ELIMINATION' or tournament.structure eq 'HYBRID'}">
                                <c:choose>
                                    <c:when test="${not editMode}">
                                        <form method="get" action="">
                                            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                            <input type="hidden" name="section" value="${param.section != null ? param.section : 'overview'}"/>
                                            <input type="hidden" name="edit" value="true"/>
                                            <button type="submit" class="btn">
                                                <paw:text size="l"><spring:message code="${edit}"/></paw:text>
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="get" action="">
                                            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                            <input type="hidden" name="section" value="${param.section != null ? param.section : 'overview'}"/>
                                            <button type="submit" class="btn empty">
                                                <paw:text size="l"><spring:message code="tournament.edit.exit"/></paw:text>
                                            </button>
                                        </form>
                                        <c:choose>
                                            <c:when test="${tournament.structure == HYBRID && tournament.is_group_stage}">
                                                <form id="swapGroupsForm" method="post" action="${pageContext.request.contextPath}/tournament/swap/groups">
                                                    <input type="hidden" name="tournamentId" value="${tournament.id}">
                                                    <button type="submit" id="swapBtn" class="btn" disabled>
                                                        <paw:text size="l"><spring:message code="tournament.edit.swap"/></paw:text>
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form id="swapMembersForm" method="post" action="${pageContext.request.contextPath}/tournament/swap/matches">
                                                    <input type="hidden" name="tournamentId" value="${tournament.id}">
                                                    <button type="submit" id="swapBtn" class="btn" disabled>
                                                        <paw:text size="l"><spring:message code="tournament.edit.swap"/></paw:text>
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <form method="post" action="${pageContext.request.contextPath}/tournament/startTournament" style="display:inline;">
                                <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                <button type="submit" class="btn">
                                    <paw:text size="l"><spring:message code="tournament.startTournament"/></paw:text>
                                </button>
                            </form>
                        </div>
                    </c:when>
                </c:choose>
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
                            <c:when test="${tournament.structure == 'HYBRID'}">
                                <c:choose>
                                    <c:when test="${!tournament.is_group_stage}">
                                        <c:forEach var="groupEntry" items="${matchesByGroup}">
                                            <c:if test="${groupEntry.key == 0}">
                                                <c:forEach var="stageEntry" items="${groupEntry.value}">
                                                    <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournament.structure}" groupNumber="${groupEntry.key}" totalMatches="${stageEntry.value.size()}"/>
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
                                                            tournamentId="${tournament.id}"
                                                            isCreator="${isCreator}"
                                                            tournamentStructure="${tournament.structure}"
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
                                             <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournament.structure}" groupNumber="${groupEntry.key}" totalMatches="${stageEntry.value.size()}"/>
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

<script>
        (function initEditChecks(){
            document.addEventListener('change', function(e){
                if (!e.target.classList.contains('edit-check')) return;
                const checked = [...document.querySelectorAll('.edit-check:checked')];
                if (checked.length > 2) {
                    e.target.checked = false;
                }
                updateSwapButton();
            });
            document.addEventListener('DOMContentLoaded', updateSwapButton);
        })();
            function updateSwapButton(){
            const btn = document.getElementById('swapBtn');
            if (!btn) return;
            const selected = [...document.querySelectorAll('.edit-check:checked')];

            btn.disabled = !(selected.length === 2);
        }
</script>
