<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout>
  <div class="content-container">
    <div class="cards-container">
      <paw:text type="title" size="xl">Tournaments</paw:text>
    </div>
    
    <!-- FILTROS -->
    <paw:filter-pills/>
    
    <c:set var="tournamentList" value="${[1,2,3,4,5,6,7,8,9]}"/>
    
    <!-- Variable para simular filtros activos (en producción vendría del backend) -->
    <c:set var="hasActiveFilters" value="${false}"/>

    <!-- GRID DE TORNEOS (solo si hay filtros activos) -->
    <c:if test="${hasActiveFilters}">
      <div class="tournaments-grid">
        <c:forEach var="i" items="${tournamentList}">
          <paw:element-card
                  image="${pageContext.request.contextPath}/images/lol.jpg"
                  title="Torneo ${i}"
                  subtitle="Edición ${i}.0"
                  game="League of Legends"/>
        </c:forEach>
      </div>
    </c:if>

    <!-- CARROUSELES (solo si NO hay filtros activos) -->
    <c:if test="${!hasActiveFilters}">
      <div class="carrousel-title">
        <paw:text type="title" size="s">League of Legends</paw:text>
      </div>
      <paw:carrousel id="lol-tournaments" elements="${tournamentList}"/>
      
      <div class="carrousel-title">
        <paw:text type="title" size="s">Valorant</paw:text>
      </div>
      <paw:carrousel id="valorant-tournaments" elements="${tournamentList}"/>
      
      <div class="carrousel-title">
        <paw:text type="title" size="s">CS:GO</paw:text>
      </div>
      <paw:carrousel id="csgo-tournaments" elements="${tournamentList}"/>
    </c:if>
  </div>
</paw:layout>

