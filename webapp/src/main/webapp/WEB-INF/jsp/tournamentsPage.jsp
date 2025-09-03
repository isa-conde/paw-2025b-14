<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}" isIndex="false">
  <c:choose>
    <c:when test="${user == null}">
      <paw:no-access/>
    </c:when>
    <c:otherwise>
      <paw:banner image="${pageContext.request.contextPath}/images/tournament.jpeg">
        <div class="page-title">
          <paw:text type="title" size="xl" stroke="true">Tournaments</paw:text>
        </div>
      </paw:banner>
      <div class="content-container">
        <form:form cssClass="form" modelAttribute="filterForm" method="post">
          <div class="filter-container">
            <paw:input path="game_id" label="Game" inputType="select" items="${games}" itemValue="id" itemLabel="name" emptyOption="All Games" inline="true"/>
            <paw:input path="region" label="Region" inputType="select" items="${regions}" emptyOption="All Regions" inline="true"/>
            <paw:input path="elo" label="Level" inputType="select" items="${elos}" emptyOption="All Levels" inline="true"/>
            <paw:input path="" label="Filter" inputType="submit" inline="true"/>
          </div>
        </form:form>


        <c:if test="${isFiltered}">
          <c:choose>
            <c:when test="${tournaments.size() <= '0'}">
              <div class="no-cards-container"><paw:text size="l" weight="thin">(No tournaments)</paw:text></div>
            </c:when>
            <c:otherwise>
              <paw:elements-grid elements="${tournaments}" id="tournamets-grid" headerElements="${games}"/>
            </c:otherwise>
          </c:choose>
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

