<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/tournament/join" var="joinUrl"/>
<c:url value="/tournament/leave" var="leaveUrl"/>
<c:url value="/tournament/update/${tournament.id}" var="tournamentUpdateUrl"/>
<c:url value="/tournament/contactOwner" var="contactOwnerUrl"/>
<c:url value="/tournament/rate" var="rateTournamentUrl"/>
<c:url value="/images/pencil.png" var="pencilUrl"/>

<c:set var="isCreator" value="${user.id == tournament.creatorId}"/>
<c:set var="editMode" value="${param.edit eq 'true' && isCreator}"/>
<c:set var="cornerModal" value="${null}"/>
<c:set var="cornerIcon" value="${null}"/>
<c:set var="cornerText" value="${null}"/>
<c:set var="playersPerTeam" value="${empty format.playersPerTeam ? 1 : format.playersPerTeam}"/>

<c:choose>
    <c:when test="${isCreator && !tournament.finished}">
        <c:set var="cornerModal" value="editTournamentModal"/>
        <c:set var="cornerIcon" value="${pencilUrl}"/>
    </c:when>
    <c:when test="${isParticipant && !isCreator}">
        <c:choose>
            <c:when test="${!tournament.isFinished}">
                <c:set var="cornerModal" value="contactOwnerModal"/>
                <c:set var="cornerText" value="tournament.contactOwner.buttonLabel"/>
            </c:when>
            <c:when test="${!hasRankedTournament}">
                <c:set var="cornerModal" value="rateTournamentModal"/>
                <c:set var="cornerText" value="tournament.ratings.buttonLabel"/>
            </c:when>
        </c:choose>
    </c:when>
</c:choose>

<paw:layout user="${user}" pageTitle="${tournament.name}" function="${openModal}">
    <paw:banner
            image="${pageContext.request.contextPath}/image/${tournament.imageId}"
            cornerIcon="${cornerIcon}"
            cornerText="${cornerText}"
            cornerOnClick="openModal('${cornerModal}')">
        <paw:text type="title" size="m" stroke="true">${game.name}</paw:text>
        <paw:text type="title" size="xl" stroke="true"><c:out value="${tournament.name}"/></paw:text>
        <div class="date-container">
            <paw:datetime date="${tournament.startDate}" weight="semi-bold"/>
            <paw:text weight="semi-bold"> - </paw:text>
            <paw:datetime date="${tournament.endDate}" weight="semi-bold"/>
        </div>
        <br/>
        <div class="organizer-container">
            <paw:text size="s"><spring:message code="tournament.organizedBy"/></paw:text>
            <c:url value="/profile/${creator.id}" var="profileurl"/>
            <paw:profileButton imageId="${creator.pfpId}" text="${creator.username}" onclick="window.location.href='${profileurl}'" size="xs" isNotSafe="true" rating="${creatorRating}"/>
        </div>
    </paw:banner>
    <spring:message code="tournament.overview" var="overview"/>
    <spring:message code="tournament.matches" var="matchesTab"/>
    <spring:message code="tournament.participants.title" var="participantsTab"/>
    <spring:message code="tournament.rules" var="rulesTab"/>
    <c:set var="showMatches" value="${tournament.tournamentStarted}"/>
    <c:set var="showRules" value="${not empty tournament.rules}"/>
    <c:choose>
        <c:when test="${showRules && showMatches}">
            <c:set var="sections" value="${['overview','matchesTab', 'participantsTab', 'rulesTab']}"/>
            <c:set var="labels"   value="${[overview, matchesTab, participantsTab, rulesTab]}"/>
        </c:when>
        <c:when test="${showMatches}">
            <c:set var="sections" value="${['overview','matchesTab', 'participantsTab']}"/>
            <c:set var="labels"   value="${[overview, matchesTab, participantsTab]}"/>
        </c:when>
        <c:when test="${showRules}">
            <c:set var="sections" value="${['overview', 'participantsTab', 'rulesTab']}"/>
            <c:set var="labels"   value="${[overview, participantsTab, rulesTab]}"/>
        </c:when>
        <c:otherwise>
            <c:set var="sections" value="${['overview', 'participantsTab']}"/>
            <c:set var="labels"   value="${[overview, participantsTab]}"/>
        </c:otherwise>
    </c:choose>
    <c:set var="activeSection" value="${param.section != null && (showMatches || param.section ne 'matches') ? param.section : 'overview'}"/>
    <paw:navbar sections="${sections}" labels="${labels}" activeSection="${activeSection}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'overview'}">
                <div class="icon-card-container">
                    <c:set var="countText" value="${tournament.openInscriptions ? tournament.maxParticipants : participantCount}"/>
                    <c:set var="teamSizeNorm" value="${empty format or empty format.playersPerTeam ? 1 : format.playersPerTeam}"/>

                    <spring:message code="tournament.participants" var="teams">
                        <spring:argument value="${countText}"/>
                        <spring:argument value="${teamSizeNorm}"/>
                    </spring:message>
                    <c:if test="${tournament.openInscriptions}">
                        <c:set var="subtext" value="${participantCount} / "/>
                    </c:if>

                    <spring:message code="elo.${tournament.elo}" var="elo"/>
                    <spring:message code="elo.text" arguments="${elo}" var="eloText"/>

                    <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournament.region}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournament.format}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${eloText}"/>
                    <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${teams}" subtext="${subtext}"/>
                </div>
                <c:if test="${tournamentWinner != null && tournamentWinner > 0}">
                    <c:forEach var="p" items="${participants}">
                        <c:if test="${p.id == tournamentWinner}">
                            <div class="cards-container">
                                <paw:winner-card winnerName="${p.name}"/>
                            </div>
                        </c:if>
                    </c:forEach>
                </c:if>
                <c:if test="${isParticipant && !tournament.tournamentStarted || !isParticipant && tournament.openInscriptions}">
                    <div class="cards-container">
                        <c:set var="icon" value="${tournament.structure == LEAGUE ? 'grid.png' : 'bracket.png'}"/>
                        <c:choose>
                            <c:when test="${isParticipant}">
                                <c:set var="title" value="tournament.member" />
                                <c:set var="text" value="tournament.notStarted" />
                                <c:set var="butText" value="${tournament.openInscriptions ? 'tournament.leave.butText' : ''}"/>
                                <c:set var="secondary" value="true"/>
                                <c:set var="url" value="${leaveUrl}"/>
                                <c:set var="method" value="post"/>
                            </c:when>
                            <c:when test="${!isParticipant && tournament.openInscriptions}">
                                <c:choose>
                                    <c:when test="${tournament.structure == LEAGUE}">
                                        <c:set var="title" value="tournament.joinCard.title.league" />
                                        <c:set var="text" value="tournament.joinCard.text.league" />
                                    </c:when>
                                    <c:when test="${tournament.structure == ELIMINATION}">
                                        <c:set var="title" value="tournament.joinCard.title.elimination" />
                                        <c:set var="text" value="tournament.joinCard.text.elimination" />
                                    </c:when>
                                    <c:when test="${tournament.structure == HYBRID}">
                                        <c:set var="title" value="tournament.joinCard.title.hybrid" />
                                        <c:set var="text" value="tournament.joinCard.text.hybrid" />
                                    </c:when>
                                </c:choose>
                                <c:set var="butText" value="tournament.joinCard.butText"/>
                                <c:set var="secondary" value="false"/>
                                <c:choose>
                                    <c:when test="${isIndividualTournament}">
                                        <c:set var="url" value="${joinUrl}"/>
                                        <c:set var="method" value="post"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="url" value="openModal('chooseTeamModal')"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                        </c:choose>
                        <paw:button-card
                                title="${title}"
                                text="${text}"
                                butText="${butText}"
                                secondary="${secondary}"
                                icon="${pageContext.request.contextPath}/images/${icon}"
                                method="${method}"
                                onclick="${url}"
                                tournamentId="${tournament.id}"
                                texture="true"/>
                    </div>
                </c:if>
                <c:choose>
                    <c:when test="${tournament.structure == LEAGUE}">
                        <c:if test="${not empty participants}">
                            <paw:text type="title" size="l"><spring:message code="tournament.standings"/></paw:text>
                            <paw:board participants="${participants}"/>
                        </c:if>
                    </c:when>
                    <c:when test="${tournament.structure == ELIMINATION || (tournament.structure == HYBRID && !tournament.isGroupStage)}">
                        <c:if test="${!tournament.openInscriptions}">
                            <c:set var="edit" value="tournament.edit.matches"/>
                            <paw:text type="title" size="l">
                                <spring:message code="tournament.standings"/>
                            </paw:text>
                            <paw:bracket matchesByStage="${matches}" tournamentId="${tournament.id}" isCreator="${isCreator}" isEditing="${editMode}" formId="swapMembersForm"/>
                        </c:if>
                    </c:when>
                    <c:when test="${tournament.structure == HYBRID && tournament.isGroupStage}">
                        <c:set var="edit" value="tournament.edit.groups" />
                        <c:if test="${not empty participants}">
                            <c:forEach var="g" begin="1" end="${groups}" step="1">
                                <div class="board-container">
                                    <paw:text type="title" size="m">
                                        <spring:message code="tournament.groupStandings" arguments="${g}"/>
                                    </paw:text>
                                    <paw:board participants="${participants}" isEditing="${editMode}" size="l" formId="swapGroupsForm" groupNumber="${g}"/>
                                </div>
                            </c:forEach>
                        </c:if>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${user.id == tournament.creatorId && tournament.openInscriptions}">
                        <c:if test="${participantCount > 1}">
                            <div class="cards-container">
                                <form method="post" action="${pageContext.request.contextPath}/tournament/closeInscriptions">
                                    <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                    <button type="submit" class="btn"><paw:text size="l"><spring:message code="tournament.closeInscriptions"/></paw:text></button>
                                </form>
                            </div>
                        </c:if>
                    </c:when>
                    <c:when test="${user.id == tournament.creatorId && !tournament.tournamentStarted}">
                        <div class="cards-container">
                            <c:if test="${tournament.structure eq ELIMINATION or tournament.structure eq HYBRID}">
                                <c:choose>
                                    <c:when test="${not editMode}">
                                        <form method="get" action="${pageContext.request.contextPath}/tournament/${tournament.id}">
                                            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                            <input type="hidden" name="section" value="${param.section != null ? param.section : 'overview'}"/>
                                            <input type="hidden" name="edit" value="true"/>
                                            <button type="submit" class="btn secondary">
                                                <paw:text size="l"><spring:message code="${edit}"/></paw:text>
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form method="get" action="${pageContext.request.contextPath}/tournament/${tournament.id}">
                                            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                            <input type="hidden" name="section" value="${param.section != null ? param.section : 'overview'}"/>
                                            <button type="submit" class="btn empty">
                                                <paw:text size="l"><spring:message code="tournament.edit.exit"/></paw:text>
                                            </button>
                                        </form>
                                        <c:choose>
                                            <c:when test="${tournament.structure == HYBRID && tournament.isGroupStage}">
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
                            <c:if test="${!editMode}">
                                <form method="post" action="${pageContext.request.contextPath}/tournament/startTournament">
                                    <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                    <button type="submit" class="btn">
                                        <paw:text size="l"><spring:message code="tournament.startTournament"/></paw:text>
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </c:when>
                </c:choose>
                <c:if test="${(isParticipant || isCreator) && tournament.tournamentStarted && !tournament.finished}">
                    <div class="cards-container">
                        <c:if test="${tournament.serverName !=null}">
                            <div class="card texture">
                                <div class="copy-card-content-container">
                                    <paw:copy-field label="tournament.serverName" value="${tournament.serverName}" />
                                    <c:if test="${tournament.serverPassword != null}">
                                        <paw:copy-field label="tournament.serverPassword" value="${tournament.serverPassword}" />
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${tournament.discordChannel != null}">
                            <div class="discord-card texture">
                                <paw:text type="title" size="l">
                                    <spring:message code="tournament.discordChannel.title"/>
                                </paw:text>

                                <div class="discord-row">
                                    <c:url value="/images/crown.png" var="discordUrl" />
                                    <img alt="Discord" src="${discordUrl}" width="30" height="30">
                                    <a href="${tournament.discordChannel}" class="title-link" target="_blank" rel="noopener noreferrer">
                                        <paw:text size="l" weight="semi-bold">
                                            <spring:message code="tournament.discordChannel"/>
                                        </paw:text>
                                    </a>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </c:if>
            </c:when>
            <c:when test="${activeSection == 'matchesTab'}">
                <c:choose>
                    <c:when test="${empty matches}">
                        <div class="no-cards-container">
                            <paw:text size="l" weight="thin"><spring:message code="tournament.noMatches"/></paw:text>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${tournament.structure == HYBRID}">
                                <c:choose>
                                    <c:when test="${!tournament.isGroupStage}">
                                        <c:forEach var="stageEntry" items="${matches}">
                                            <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournament.structure}" totalMatches="${stageEntry.value.size()}" maxStage="${maxStage}"/>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="subActiveGroup" value="${param.group != null ? param.group : 1}"/>
                                        <paw:groups-navbar groups="${groups}" activeGroup="${subActiveGroup}" paramName="group"/>
                                        <c:forEach var="stageEntry" items="${matches}">
                                                <paw:date-matches
                                                        dateNumber="${stageEntry.key}"
                                                        matches="${stageEntry.value}"
                                                        tournamentId="${tournament.id}"
                                                        isCreator="${isCreator}"
                                                        tournamentStructure="${tournament.structure}"
                                                        groupStage="true"
                                                        groupNumber="${subActiveGroup}"
                                                        totalMatches="${stageEntry.value.size()}"/>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="stageEntry" items="${matches}">
                                     <paw:date-matches dateNumber="${stageEntry.key}" matches="${stageEntry.value}" tournamentId="${tournament.id}" isCreator="${isCreator}" tournamentStructure="${tournament.structure}" totalMatches="${stageEntry.value.size()}" maxStage="${maxStage}"/>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${activeSection == 'participantsTab'}">
                <spring:message code="tournament.participants.title" var="ptitle"/>
                <div class="users-grid-title">
                    <paw:text type="title" size="l">${ptitle}</paw:text>
                    <c:if test="${tournament.openInscriptions}">
                        <div class="chip">
                            <paw:text size="l" weight="thin">${participantCount} / </paw:text>
                            <paw:text size="l" weight="bold">${tournament.maxParticipants}</paw:text>
                        </div>
                    </c:if>
                </div>
                <paw:users-grid participants="${participants}" isIndividualTournament="${isIndividualTournament}" deleteMode="${isCreator and tournament.openInscriptions}"/>
                <c:if test="${!isParticipant && tournament.openInscriptions}">
                    <div class="cards-container">
                        <c:choose>
                            <c:when test="${isIndividualTournament}">
                                <form:form method="post"
                                           action="${joinUrl}"
                                           onsubmit="this.querySelector('button, input[type=submit]').disabled=true;">
                                    <input type="hidden" name="tournamentId" value="${tournament.id}"/>
                                    <paw:input path="" inputType="submit" label="tournament.joinCard.butText"/>
                                </form:form>
                            </c:when>
                            <c:otherwise>
                                <button type="button" class="btn"
                                        onclick="openModal('chooseTeamModal')">
                                    <paw:text size="l"><spring:message code="tournament.joinCard.butText"/></paw:text>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </c:when>
            <c:when test="${activeSection == 'rulesTab'}">
                <c:url var="rulesUrl" value="/rules/${tournament.rules.id}"/>
                <div class="rules-card-container">
                    <paw:button-card texture="true" title="tournament.rules.title" text="tournament.rules.text" onclick="window.location.href='${rulesUrl}'" butText="tournament.rules.download"/>
                </div>
            </c:when>
        </c:choose>
    </div>
    <c:url var="tournamentUrl" value="/tournament/${tournament.id}">
        <c:if test="${not empty param.section}">
            <c:param name="section" value="${param.section}"/>
        </c:if>
        <c:if test="${not empty param.group}">
            <c:param name="group" value="${param.group}"/>
        </c:if>
    </c:url>
    <paw:modal title="tournament.edit.modal.title" id="editTournamentModal" returnUrl="${tournamentUrl}">
        <form:form method="post" modelAttribute="editTournamentForm"
                   action="${tournamentUpdateUrl}"
                   enctype="multipart/form-data" cssClass="form">
            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
            <c:if test="${!tournament.finished}">
                <div class="row">
                    <paw:input path="name" label="createTournament.name" hasConstraint="true"/>
                    <c:if test="${tournament.openInscriptions}">
                        <paw:input path="maxParticipants" label="createTournament.maxParticipants" arg="${playersPerTeam}" inputType="number" hasConstraint="true"/>
                    </c:if>
                </div>
                <div class="row">
                    <c:if test="${!tournament.tournamentStarted}">
                        <paw:input path="startDate" label="createTournament.startDate" inputType="date" hasConstraint="true"/>
                    </c:if>
                    <paw:input path="endDate" label="createTournament.endDate" inputType="date" hasConstraint="true"/>
                </div>
                <div class="row">
                    <paw:input path="serverName" label="tournament.serverName" hasConstraint="true"/>
                    <paw:input path="serverPassword" label="tournament.serverPassword" hasConstraint="true"/>
                </div>
                <div class="row">
                    <paw:input path="discordChannel" label="tournament.discordChannel.edit" hasConstraint="true"/>
                </div>
                <div class="row">
                    <paw:input path="image" label="createTournament.image" fileText="input.uploadImage" inputType="file"/>
                    <paw:input path="rules" label="tournament.rules" inputType="file" fileText="input.uploadPdf" hasConstraint="true"/>
                </div>
            </c:if>
            <div class="row center">
                <paw:input path="" label="tournament.edit.saveChanges" containerType="half" inputType="submit"/>
            </div>
        </form:form>
    </paw:modal>
    <paw:modal title="tournament.contactOwner.modalTitle" id="contactOwnerModal" returnUrl="${tournamentUrl}">
        <form:form method="post" modelAttribute="contactOwnerForm"
                   action="${contactOwnerUrl}"
                   cssClass="form">
            <input type="hidden" name="creatorId" value="${creator.id}"/>
            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
            <div class="row">
                <paw:input path="subject" label="tournament.contactOwner.subject" hasConstraint="true"/>
            </div>
            <div class="row">
                <paw:input inputType="textarea" path="body" label="tournament.contactOwner.body" hasConstraint="true"/>
            </div>
            <div class="row center">
                <paw:input path="" label="tournament.contactOwner.send" containerType="half" inputType="submit"/>
            </div>
        </form:form>
    </paw:modal>
    <paw:modal title="tournament.ratings.modalTitle" id="rateTournamentModal" returnUrl="${tournamentUrl}">
        <form:form method="post" modelAttribute="rateTournamentForm"
                   action="${rateTournamentUrl}"
                   cssClass="form">
            <input type="hidden" name="creatorId" value="${creator.id}"/>
            <input type="hidden" name="tournamentId" value="${tournament.id}"/>
            <div class="row center">
                <paw:star-rating path="rating" required="true"/>
            </div>
            <div class="row center">
                <paw:input path="" label="tournament.ratings.rate" containerType="half" inputType="submit"/>
            </div>
        </form:form>
    </paw:modal>
</paw:layout>

<paw:modal title="tournament.join.chooseTeam" id="chooseTeamModal" returnUrl="${tournamentUrl}">
    <form:form method="post" modelAttribute="joinTeamForm" action="${pageContext.request.contextPath}/tournament/join/step1" cssClass="form">
        <form:hidden path="tournamentId" value="${tournament.id}"/>
        <c:choose>
            <c:when test="${userTeams.size() <= 0}">
                <div class="row center">
                    <paw:text size="l"><spring:message code="tournament.join.noTeams" arguments="${format.playersPerTeam}"/></paw:text>
                </div>
                <div class="row center">
                    <paw:button onclick="window.location.href='${pageContext.request.contextPath}/team/create'; return false;" text="team.create.pageTitle"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="teams-list-container">
                    <paw:team-list teams="${userTeams}"/>
                </div>
                <div class="join-team-error">
                    <form:errors path="teamId" cssClass="form-error" element="h1"/>
                </div>
                <div class="row center">
                    <paw:input path="" label="tournament.chooseTeam" containerType="half" inputType="submit"/>
                </div>
            </c:otherwise>
        </c:choose>
    </form:form>
</paw:modal>
<paw:modal title="tournament.join.chooseMembers" id="chooseTeamMembersModal" returnUrl="${tournamentUrl}">
    <form:form method="post" modelAttribute="joinTeamForm" action="${pageContext.request.contextPath}/tournament/join/step2" cssClass="form" data-required-members="${format.playersPerTeam}">
        <form:hidden path="tournamentId" value="${tournament.id}"/>
        <form:hidden path="teamId" value="${selectedTeamId}"/>
        <c:if test="${teamMembers.size() > format.playersPerTeam}">
            <paw:text size="l"><spring:message code="tournament.join.requiredSize" arguments="${format.playersPerTeam}"/></paw:text>
        </c:if>
        <div class="teams-list-container">
            <paw:member-list members="${teamMembers}" requiredSize="${format.playersPerTeam}"/>
        </div>
        <div class="join-team-error">
            <form:errors path="members" cssClass="form-error" element="h1"/>
        </div>
        <div class="row center">
            <paw:input path="" label="tournament.joinCard.butText" containerType="half" inputType="submit"/>
        </div>
    </form:form>
</paw:modal>
<c:url value="/tournament/setMatchResults" var="setMatchUrl"/>
<paw:modal title="tournament.setMatchResults.title" id="setMatchResultsModal" returnUrl="${tournamentUrl}">
    <form:form method="post" modelAttribute="setMatchResultsForm" action="${setMatchUrl}" cssClass="form"
               onsubmit="this.querySelectorAll('button, input[type=submit]').forEach(el => el.disabled = true);">
        <input type="hidden" id="modalMatchId" name="matchId" value=""/>
        <input type="hidden" name="tournamentId" value="${tournament.id}"/>
        <div class="row">
            <paw:input path="localScore" label="tournament.setMatchResults.localScore" hasConstraint="true" inputType="number"/>
            <paw:input path="visitorScore" label="tournament.setMatchResults.visitorScore" hasConstraint="true" inputType="number"/>
        </div>
        <div class="row center">
            <paw:input path="" label="tournament.setMatchResults.set" containerType="half" inputType="submit"/>
        </div>
    </form:form>
</paw:modal>
<c:url value="/tournament/removeParticipant" var="removeParticipantUrl"/>
<paw:modal title="tournament.removeParticipant.title" id="removeParticipantModal" returnUrl="${tournamentUrl}">
    <div class="modal-text">
        <p class="main-text m semi-bold"><spring:message code="tournament.removeParticipant.text"/></p>
        <p class="main-text m bold"><span id="removeParticipantName">—</span></p>
    </div>
    <form:form method="post" id="removeParticipantForm" action="${removeParticipantUrl}" onsubmit="this.querySelectorAll('button, input[type=submit]').forEach(el => el.disabled = true);">
        <input type="hidden" id="modalParticipantId" name="participantId" value=""/>
        <input type="hidden" name="tournamentId" value="${tournament.id}"/>
        <div class="row center">
            <paw:button text="tournament.removeParticipant.cancel" onclick="event.preventDefault(); closeModal('removeParticipantModal')" secondary="true"/>
            <paw:button text="tournament.removeParticipant.remove" onclick="document.getElementById('removeParticipantModal').requestSubmit();"/>
        </div>
    </form:form>
</paw:modal>

<script src="${pageContext.request.contextPath}/js/removeParticipantModal.js"></script>
<script src="${pageContext.request.contextPath}/js/matchResultsModal.js"></script>
<script src="${pageContext.request.contextPath}/js/swap.js"></script>
<script src="${pageContext.request.contextPath}/js/copyField.js"></script>