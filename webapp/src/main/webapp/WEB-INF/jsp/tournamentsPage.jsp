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
      <form:form cssClass="form" modelAttribute="filterForm" method="get">
        <div class="filter-container">
          <paw:input path="game_id" label="tournaments.game" inputType="select" items="${games}" itemValue="id" itemLabel="name" emptyOption="${allGames}" inline="true"/>
          <paw:input path="region" label="tournaments.region" inputType="select" items="${regions}" emptyOption="${allRegions}" inline="true"/>
          <paw:input path="elo" label="tournaments.skillLevel" inputType="select" items="${elos}" emptyOption="${allLevels}" inline="true"/>
          <paw:input path="genre" label="tournaments.genre" inputType="select" items="${genres}" emptyOption="${allGenres}" inline="true"/>
          <paw:input path="playersPerTeam" label="tournaments.playerAmount" inputType="select" items="${teamSizes}" emptyOption="${allSizes}" inline="true"/>
          <paw:input path="" label="tournaments.filter" inputType="submit" inline="true"/>
        </div>
      </form:form>

      <c:if test="${isFiltered}">
          <paw:elements-grid elements="${tournaments}" id="tournamets-grid" headerElements="${games}"/>
      </c:if>

      <c:if test="${!isFiltered}">
        <c:forEach var="game" items="${gameTournaments.keySet()}" varStatus="status">
            <c:if test="${not empty gameTournaments[game]}">
                <div class="carrousel-title">
                    <paw:text type="title" size="s"><c:out value="${game.name}"/></paw:text>
                </div>
                <paw:carrousel id="game-${game.id}-tournaments" elements="${gameTournaments[game]}"/>
            </c:if>
        </c:forEach>
      </c:if>
        <div class="pagination-container">
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 0}">
                        <paw:paginationLink page="${currentPage - 1}" url="/tournamentsPage">
                            <paw:text size="l" weight="thin"><</paw:text>
                        </paw:paginationLink>
                    </c:if>

                    <c:forEach begin="0" end="${totalPages - 1}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                            </c:when>
                            <c:otherwise>
                                <paw:paginationLink page="${i}" url="/tournamentsPage">
                                    <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                </paw:paginationLink>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages - 1}">
                        <paw:paginationLink page="${currentPage + 1}" url="/tournamentsPage">
                            <paw:text size="l" weight="thin">></paw:text>
                        </paw:paginationLink>
                    </c:if>
                </div>
            </c:if>
        </div>

        <div class="cards-container">
            <c:set var="createTournamentFunction" value="window.location.href='${pageContext.request.contextPath}/tournaments/new/step1'"/>
            <c:set var="createTeamFunction" value="window.location.href='${pageContext.request.contextPath}/team/create'"/>
            <paw:button-card
                    title="tournaments.create.title"
                    butText="home.createTournament.butText"
                    onclick="${createTournamentFunction}"
                    texture="true"/>
            <paw:button-card
                    title="tournaments.team.title"
                    butText="tournaments.team.butText"
                    onclick="${createTeamFunction}"
                    texture="true"/>
        </div>

    </div>
</paw:layout>


