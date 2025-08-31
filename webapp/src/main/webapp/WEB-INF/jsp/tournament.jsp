<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout user="${user}">
  <paw:banner image="${pageContext.request.contextPath}/images/lol.jpg">
    <paw:text type="title">${game.name}</paw:text>
    <paw:text type="title" size="xl">${tournament.name}</paw:text>
    <paw:text>${tournament.start_date.format(formatter)} - ${tournament.end_date.format(formatter)}</paw:text>
    <br/>
    <div class="organizer-container">
      <paw:text size="s">Organized by</paw:text>
      <paw:profileButton text="${creator.username}" onclick="" size="xs" fill="false" disabled="true"/>
    </div>
  </paw:banner>
  <div class="content-container">
    <div class="icon-card-container">
      <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="${tournament.region}"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="${tournament.format}"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="${tournament.elo}"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="${tournament.max_participants} Teams"/>
    </div>
    <div class="cards-container">
      <paw:button-card
              title="Become the first in the league"
              text="Play against all your oponents to collect points and win the tournament"
              butText="Join Tournament"
              icon="${pageContext.request.contextPath}/images/grid.png"
              onclick=""
              texture="true"/>
    </div>

    <br/>
    <paw:text type="title" size="l">Standings</paw:text>
    <paw:board participants="${[1, 2, 3, 4, 5, 6]}"/>
  </div>
</paw:layout>


