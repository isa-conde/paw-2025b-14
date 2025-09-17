<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:message code="tournaments.game.emptyOption" var="allGames"/>
<spring:message code="tournaments.region.emptyOption" var="allRegions"/>
<spring:message code="tournaments.skillLevel.emptyOption" var="allLevels"/>
<spring:message code="tournaments.genre.emptyOption" var="allGenres"/>
<spring:message code="tournaments.playerAmount.emptyOption" var="allSizes"/>

<paw:layout user="${user}" isIndex="false">
  <c:choose>
    <c:when test="${user == null}">
      <paw:no-access/>
    </c:when>
    <c:otherwise>
      <paw:banner image="${pageContext.request.contextPath}/images/tournament.jpeg">
        <div class="page-title">
          <paw:text type="title" size="xl" stroke="true"><spring:message code="tournaments.title"/></paw:text>
        </div>
      </paw:banner>
      <div class="content-container">
        <form:form cssClass="form" modelAttribute="filterForm" method="post">
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
          <c:forEach var="game" items="${games}" varStatus="status">
            <c:if test="${not empty gameTournaments[game.id]}">
              <div class="carrousel-title">
                <paw:text type="title" size="s"><c:out value="${game.name}"/></paw:text>
              </div>
              <paw:carrousel id="game-${game.id}-tournaments" elements="${gameTournaments[game.id]}"/>
            </c:if>
          </c:forEach>
        </c:if>
      </div>
    </c:otherwise>
  </c:choose>
</paw:layout>

