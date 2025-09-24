<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message code="home.createTournament.emptyOption" var="emptyOption"/>

<paw:layout user="${user != null ? user : null}" function="${openModal}" isIndex="true">
<c:choose>
    <c:when test="${not empty user}">
        <c:set var="createTournamentFunction" value="openModal('createTournamentModal')"/>
        <c:set var="joinTournamentFunction" value="window.location.href='${pageContext.request.contextPath}/tournamentsPage'"/>
    </c:when>
    <c:otherwise>
        <c:set var="createTournamentFunction" value="openModal('loginModal')"/>
        <c:set var="joinTournamentFunction" value="openModal('loginModal')"/>
    </c:otherwise>
</c:choose>
    <c:url value="/login" var="userLoginPath"/>
    <c:url value="/register" var="userRegisterPath"/>
    <c:url value="/tournament/create" var="createTournamentPath"/>

    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="title" size="xl"><spring:message code="home.welcome.title"/></paw:text>
        <br>
        <paw:text type="title" size="l"><spring:message code="home.welcome.subtitle"/></paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <c:choose>
                <c:when test="${user != null}">
                    <paw:button-card
                            title="home.createTournament.title"
                            butText="home.createTournament.butText"
                            onclick="openModal('createTournamentModal')"
                            texture="true"/>
                </c:when>
                <c:otherwise>
                    <paw:button-card
                            title="home.createTournament.title"
                            butText="home.createTournament.butText"
                            onclick="openModal('loginModal')"
                            texture="true"/>
                </c:otherwise>
            </c:choose>
            <paw:modal id="createTournamentModal" title="home.createTournament.title">
                <form:form cssClass="form" modelAttribute="tournamentForm" action="${createTournamentPath}" method="post" enctype="multipart/form-data">
                    <div class="row">
                        <paw:input path="name" label="home.createTournament.name" hasConstraint="true"/>
                    </div>
                    <div class="row">
                        <paw:input path="game_id" label="home.createTournament.game" containerType="half" inputType="select" items="${games}" itemValue="game.id" itemLabel="game.name" hasConstraint="true" emptyOption="${emptyOption}"/>
                        <paw:input path="region" label="home.createTournament.region" containerType="half" inputType="select" items="${regions}" hasConstraint="true" emptyOption="${emptyOption}"/>
                    </div>
                    <div class="row">
                        <paw:input path="start_date" label="home.createTournament.startDate" containerType="half" inputType="date" hasConstraint="true"/>
                        <paw:input path="end_date" label="home.createTournament.endDate" containerType="half" inputType="date" hasConstraint="true"/>
                    </div>
                    <div class="row center">
                        <paw:input path="max_participants" label="home.createTournament.maxParticipants" containerType="half" inputType="number" hasConstraint="true"/>
                        <paw:input path="structure" label="home.createTournament.structure" containerType="half" inputType="select" items="${structures}" emptyOption="${emptyOption}"/>
                    </div>
                    <div class="row">
                        <paw:input path="format" label="home.createTournament.format" containerType="half" hasConstraint="true"/>
                        <paw:input path="elo" label="home.createTournament.skillLevel" containerType="half" inputType="select" items="${elos}" hasConstraint="true" emptyOption="${emptyOption}"/>
                    </div>
                    <div class="row">
                        <paw:input path="image" label="home.createTournament.image" inputType="file" hasConstraint="true"/>
                    </div>
                    <div class="row center">
                        <paw:input path="" label="home.createTournament.create" containerType="half" inputType="submit"/>
                    </div>
                </form:form>
            </paw:modal>
            <paw:button-card title="home.joinTournament.title" butText="home.joinTournament.butText" onclick="${joinTournamentFunction}" texture="true"/>
        </div>
        <div class="content-title">
            <a href="${pageContext.request.contextPath}/gamesPage" class="title-link">
                <paw:text type="title" size="l"><spring:message code="home.games"/></paw:text>
            </a>
        </div>
        <paw:carrousel id="game-list" elements="${games}" isGame="true"/>

        <div class="content-title">
            <a href="${pageContext.request.contextPath}/tournamentsPage" class="title-link">
                <paw:text type="title" size="l"><spring:message code="home.tournaments"/></paw:text>
            </a>
        </div>

        <c:forEach var="game" items="${games}" varStatus="status">
            <c:set var="gameId" value="${game.game.id}"/>
            <c:set var="gameTournaments" value="${requestScope['tournaments' += gameId]}"/>
            <c:set var="gameObject" value="${requestScope['game' += gameId]}"/>

            <c:if test="${not empty gameObject and not empty gameTournaments}">
                <div class="carrousel-title">
                    <paw:text type="title" size="s">${gameObject.game.name}</paw:text>
                </div>
                <paw:carrousel id="game-${gameId}-tournaments" elements="${gameTournaments}"/>
            </c:if>
        </c:forEach>

    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>