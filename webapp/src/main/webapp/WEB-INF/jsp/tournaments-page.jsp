<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout user="${user}">
  <div class="content-container">
    <div class="cards-container">
      <paw:text type="title" size="xl">Tournaments</paw:text>
    </div>
    
    <!-- FILTROS -->
    <paw:filter-pills/>
    
    <!-- Variable para simular filtros activos (en producción vendría del backend) -->
    <c:set var="hasActiveFilters" value="${true}"/>

    <!-- GRID DE TORNEOS (solo si hay filtros activos) -->
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

    <!-- CARROUSELES (solo si NO hay filtros activos) -->
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

