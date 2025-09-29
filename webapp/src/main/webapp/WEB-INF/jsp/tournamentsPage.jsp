<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:message code="tournaments.game.emptyOption" var="allGames"/>
<spring:message code="tournaments.region.emptyOption" var="allRegions"/>
<spring:message code="tournaments.skillLevel.emptyOption" var="allLevels"/>
<spring:message code="tournaments.genre.emptyOption" var="allGenres"/>
<spring:message code="tournaments.playerAmount.emptyOption" var="allSizes"/>

<paw:layout user="${user}">
    <paw:banner image="${pageContext.request.contextPath}/images/tournament.jpeg">
      <div class="page-title">
        <paw:text type="title" size="xl" stroke="true"><spring:message code="tournaments.title"/></paw:text>
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
    <c:forEach var="game" items="${games}" varStatus="status">
        <c:if test="${not empty gameTournaments[game.id]}">
            <div class="carrousel-title">
                <paw:text type="title" size="s"><c:out value="${game.name}"/></paw:text>
            </div>
            <paw:carrousel id="game-${game.id}-tournaments" elements="${gameTournaments[game.id]}"/>
        </c:if>
    </c:forEach>
  </c:if>
        <div class="pagination-container">
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 0}">
                        <c:url var="pageUrl" value="/tournamentsPage">
                            <c:param name="page" value="${i}"/>
                            <c:forEach var="p" items="${paramValues}">
                                <c:forEach var="v" items="${p.value}">
                                    <c:if test="${p.key ne 'page'}">
                                        <c:param name="${p.key}" value="${v}"/>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                        </c:url>
                        <a href="${pageUrl}" class="title-link">
                            <paw:text size="l" weight="thin"><</paw:text>
                        </a>
                    </c:if>

                    <c:forEach begin="0" end="${totalPages - 1}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                            </c:when>
                            <c:otherwise>
                                <c:url var="pageUrl" value="/tournamentsPage">
                                    <c:param name="page" value="${i}"/>
                                    <c:forEach var="p" items="${paramValues}">
                                        <c:forEach var="v" items="${p.value}">
                                            <c:if test="${p.key ne 'page'}">
                                                <c:param name="${p.key}" value="${v}"/>
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:url>
                                <a href="${pageUrl}" class="title-link">
                                    <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages - 1}">
                        <c:url var="pageUrl" value="/tournamentsPage">
                            <c:param name="page" value="${i}"/>
                            <c:forEach var="p" items="${paramValues}">
                                <c:forEach var="v" items="${p.value}">
                                    <c:if test="${p.key ne 'page'}">
                                        <c:param name="${p.key}" value="${v}"/>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                        </c:url>
                        <a href="${pageUrl}" class="title-link">
                            <paw:text size="l" weight="thin">></paw:text>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </div>

    </div>
</paw:layout>

<c:url var="pageUrl" value="/tournamentsPage">
    <c:param name="page" value="${currentPage + 1}"/>
    <c:if test="${not empty param.game_id}">
        <c:param name="game_id" value="${param.game_id}"/>
    </c:if>
    <c:if test="${not empty param.region}">
        <c:param name="region" value="${param.region}"/>
    </c:if>
    <c:if test="${not empty param.elo}">
        <c:param name="elo" value="${param.elo}"/>
    </c:if>
    <c:if test="${not empty param.genre}">
        <c:param name="genre" value="${param.genre}"/>
    </c:if>
</c:url>

