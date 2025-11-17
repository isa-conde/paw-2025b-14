<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:message code="tournaments.game.emptyOption" var="allGames"/>
<spring:message code="tournaments.region.emptyOption" var="allRegions"/>
<spring:message code="tournaments.skillLevel.emptyOption" var="allLevels"/>
<spring:message code="tournaments.genre.emptyOption" var="allGenres"/>
<spring:message code="tournaments.playerAmount.emptyOption" var="allSizes"/>
<spring:message code="tournaments.title" var="pageTitle"/>

<paw:layout user="${user}" pageTitle="${pageTitle}">
    <paw:banner image="${pageContext.request.contextPath}/images/tournament.jpeg">
      <div class="page-title">
        <paw:text type="title" size="xl" stroke="true">${pageTitle}</paw:text>
      </div>

    </paw:banner>
    <div class="content-container">
        <div class="tournament-buttons-container">
            <c:set var="createTournamentFunction" value="window.location.href='${pageContext.request.contextPath}/tournaments/new/step1'"/>
            <c:set var="createTeamFunction" value="window.location.href='${pageContext.request.contextPath}/team/create'"/>
            <paw:button onclick="${createTournamentFunction}" text="createTournament.butText" size="l"/>
            <paw:button onclick="${createTeamFunction}" text="tournaments.team.butText" size="l"/>
        </div>
      <form:form cssClass="form" modelAttribute="filterForm" method="get">
          <div class="filter-container">
              <div class="filter-container">
                  <paw:input path="gameId" label="tournaments.game" inputType="select" items="${games}" itemValue="id" itemLabel="name" emptyOption="${allGames}" inline="true"/>
                  <paw:input path="region" label="tournaments.region" inputType="select" items="${regions}" emptyOption="${allRegions}" inline="true"/>
                  <paw:input path="elo" label="tournaments.skillLevel" inputType="select" itemMap="${elos}" emptyOption="${allLevels}" inline="true"/>
                  <paw:input path="genre" label="tournaments.genre" inputType="select" items="${genres}" emptyOption="${allGenres}" inline="true"/>
              </div>
              <div class="filter-container">
                  <paw:input path="playersPerTeam" label="tournaments.playerAmount" inputType="select" items="${teamSizes}" emptyOption="${allSizes}" inline="true"/>
                  <paw:input path="startDate" label="createTournament.startDate"  inputType="date" inline="true"/>
                  <paw:input path="endDate" label="createTournament.endDate" inputType="date" inline="true"/>
                  <paw:input path="" label="tournaments.filter" inputType="submit" inline="true"/>
                  <paw:refresh-button disabled="${!isFiltered}"/>
              </div>
          </div>
      </form:form>

      <c:if test="${isFiltered}">
          <paw:elements-grid elements="${tournaments}" id="tournamets-grid" headerElements="${games}"/>
      </c:if>

      <c:if test="${!isFiltered}">
        <c:forEach var="game" items="${gameTournaments.keySet()}" varStatus="status">
            <c:if test="${not empty gameTournaments[game]}">
                <div class="carrousel-title">
                    <c:url value="tournamentsPage?gameId=${game.id}"  var="gameurl"/>
                    <a href="${gameurl}" class="title-link">
                        <paw:text type="title" size="s"><c:out value="${game.name}"/></paw:text>
                    </a>
                </div>
                <paw:carrousel id="game-${game.id}-tournaments" elements="${gameTournaments[game]}"/>
            </c:if>
        </c:forEach>
      </c:if>
        <paw:pagination currentPage="${currentPage}" totalPages="${totalPages}" url="/tournamentsPage"/>
    </div>
</paw:layout>


