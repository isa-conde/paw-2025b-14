<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message code="createTournament.emptyOption" var="emptyOption"/>

<paw:layout user="${user != null ? user : null}" function="${openModal}">
    <c:set var="createTournamentFunction" value="window.location.href='${pageContext.request.contextPath}/tournaments/new/step1'"/>
    <c:set var="joinTournamentFunction" value="window.location.href='${pageContext.request.contextPath}/tournamentsPage'"/>
    <c:url value="/login" var="userLoginPath"/>
    <c:url value="/register" var="userRegisterPath"/>
    <c:url value="/tournaments/new/step2" var="createTournamentPath"/>

    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="title" size="xl"><spring:message code="home.welcome.title"/></paw:text>
        <br>
        <paw:text type="title" size="l"><spring:message code="home.welcome.subtitle"/></paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <paw:button-card
                    title="createTournament.title"
                    butText="createTournament.butText"
                    onclick="${createTournamentFunction}"
                    texture="true"/>
            <paw:button-card
                    title="home.joinTournament.title"
                    butText="home.joinTournament.butText"
                    onclick="${joinTournamentFunction}"
                    texture="true"/>
        </div>
        <div class="content-title">
            <c:url value="gamesPage"  var="gamespageurl"/>
            <a href="${gamespageurl}" class="title-link">
                <paw:text type="title" size="l"><spring:message code="home.games"/></paw:text>
            </a>
        </div>
        <paw:carrousel id="game-list" elements="${games}" isGame="true"/>

        <div class="content-title">
            <c:url value="tournamentsPage"  var="tourneysurl"/>
            <a href="${tourneysurl}" class="title-link">
                <paw:text type="title" size="l"><spring:message code="home.tournaments"/></paw:text>
            </a>
        </div>

        <c:forEach var="gameId" items="${gameIds}" varStatus="status">
            <c:set var="gameTournaments" value="${requestScope['tournaments' += gameId]}"/>
            <c:set var="gameObject" value="${requestScope['game' += gameId]}"/>
                <div class="carrousel-title">
                    <c:url value="tournamentsPage?gameId=${gameObject.id}"  var="gameurl"/>
                    <a href="${gameurl}" class="title-link">
                        <paw:text type="title" size="s">${gameObject.name}</paw:text>
                    </a>
                </div>
                <paw:carrousel id="game-${gameId}-tournaments" elements="${gameTournaments}"/>
        </c:forEach>
    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>