<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}">
  <div class="content-container">
    <div class="cards-container">
      <paw:text type="title" size="xl">Tournaments</paw:text>
    </div>
    <form:form cssClass="form" modelAttribute="tournamentForm" method="post">
      <div class="row">
        <paw:input path="game_id" label="Game" containerType="half" inputType="select" items="${games}" itemValue="id" itemLabel="name"/>
        <paw:input path="region" label="Region" containerType="half" inputType="select" items="${regions}"/>
        <paw:input path="" label="Filter" containerType="half" inputType="submit"/>
      </div>
    </form:form>
    
    <c:set var="hasActiveFilters" value="${true}"/>

    <c:if test="${hasActiveFilters}">
      <div class="tournaments-grid">
        <c:forEach var="tournament" items="${tournamentsGame1}">
          <paw:element-card
                  image="${pageContext.request.contextPath}/images/lol.jpg"
                  title="${tournament.name}"
                  subtitle="Format: ${tournament.format}"
                  game="${game1.name}"/>
        </c:forEach>
      </div>
    </c:if>

    <c:if test="${!hasActiveFilters}">
      <c:if test="${not empty game1}">
        <div class="carrousel-title">
          <paw:text type="title" size="s">${game1.name}</paw:text>
        </div>
        <paw:carrousel id="game1-tournaments" elements="${tournamentsGame1}"/>
      </c:if>
      
      <c:if test="${not empty game2}">
        <div class="carrousel-title">
          <paw:text type="title" size="s">${game2.name}</paw:text>
        </div>
        <paw:carrousel id="game2-tournaments" elements="${tournamentsGame2}"/>
      </c:if>
    </c:if>
  </div>
</paw:layout>

