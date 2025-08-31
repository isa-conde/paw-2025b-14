<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout>
  <paw:banner image="${pageContext.request.contextPath}/images/lol.jpg">
    <paw:text type="title">League of Legends</paw:text>
    <paw:text type="title" size="xl">Superstars Tournament</paw:text>
    <paw:text>Aug 25th 2025 - Aug 30th 2025</paw:text>
    <br/>
    <div class="organizer-container">
      <paw:text size="s">Organized by</paw:text>
      <paw:profileButton text="tounexd" onclick="" size="xs" fill="false" disabled="true"/>
    </div>
  </paw:banner>
  <div class="content-container">
    <div class="icon-card-container">
      <paw:icon-card icon="${pageContext.request.contextPath}/images/map.png" text="LATAM"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/team.png" text="5 vs 5"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/level.png" text="HIGH"/>
      <paw:icon-card icon="${pageContext.request.contextPath}/images/members.png" text="16 Teams"/>
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
