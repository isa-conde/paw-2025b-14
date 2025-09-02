<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:url value="/tournament/join" var="joinUrl"/>

<paw:layout user="${user}">
  <paw:banner image="data:image/png;base64,${tournamentImg.base64Img}">
    <paw:text type="title" size="m" stroke="true">${game.name}</paw:text>
    <paw:text type="title" size="xl" stroke="true">${tournamentImg.tournament.name}</paw:text>
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
          <paw:button-card
                  title="Become the first in the league"
                  text="Play against all your oponents to collect points and win the tournament"
                  butText="Join Tournament"
                  icon="${pageContext.request.contextPath}/images/grid.png"
                  method="post"
                  onclick="${joinUrl}"
                  tournamentId="${tournamentImg.tournament.id}"
                  texture="true"/>
        </div>
        <br/>
        <paw:text type="title" size="l">Standings</paw:text>
        <paw:board participants="${participants}"/>
      </c:when>
      <c:when test="${activeSection == 'Matches'}">
        <c:choose>
          <c:when test="${empty matchesByDate}">
            <div class="no-cards-container">
              <paw:text size="l" weight="thin">No matches available</paw:text>
            </div>
          </c:when>
          <c:otherwise>
            <c:forEach var="dateEntry" items="${matchesByDate}">
              <paw:date-matches dateNumber="${dateEntry.key}" matches="${dateEntry.value}"/>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </c:when>
    </c:choose>
  </div>
</paw:layout>

<script>
function setWinner(matchId, winnerId) {
    alert('Setting winner for match ' + matchId + ' to player ' + winnerId);
}
</script>
