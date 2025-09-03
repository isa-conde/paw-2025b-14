<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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

<paw:layout user="${user != null ? user : null}" isIndex="true">

    <c:url value="/login" var="userLoginPath"/>
    <c:url value="/register" var="userRegisterPath"/>
    <c:url value="/tournament/create" var="createTournamentPath"/>

    <paw:modal id="loginModal" title="Log in">
        <form:form cssClass="form" modelAttribute="loginForm" action="${userLoginPath}" method="post">
            <div class="row">
                <paw:input path="username" label="Username"/>
            </div>
            <div class="row">
                <paw:input path="email" label="Email" inputType="email"/>
            </div>
            <div class="row center">
                <paw:input path="" label="Log in" containerType="half" inputType="submit"/>
            </div>
        </form:form>
    </paw:modal>
    <paw:modal id="registerModal" title="Register">
        <form:form cssClass="form" modelAttribute="registerForm" action="${userRegisterPath}" method="post">
            <div class="row">
                <paw:input path="username" label="Username"/>
            </div>
            <div class="row">
                <paw:input path="email" label="Email" inputType="email"/>
            </div>
            <div class="row center">
                <paw:input path="" label="Register" containerType="half" inputType="submit"/>
            </div>
        </form:form>
    </paw:modal>

    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="title" size="xl"><spring:message code="welcome.title"/></paw:text>
        <br>
        <paw:text type="title" size="l"><spring:message code="welcome.subtitle"/></paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <c:choose>
                <c:when test="${user != null}">
                    <paw:button-card
                            title="Become an Organizer"
                            butText="Create a Tournament"
                            onclick="openModal('createTournamentModal')"
                            texture="true"/>
                </c:when>
                <c:otherwise>
                    <paw:button-card
                            title="Become an Organizer"
                            butText="Create a Tournament"
                            onclick="openModal('loginModal')"
                            texture="true"/>
                </c:otherwise>
            </c:choose>
            <paw:modal id="createTournamentModal" title="Create a Tournament">
                <form:form cssClass="form" modelAttribute="tournamentForm" action="${createTournamentPath}" method="post" enctype="multipart/form-data">
                    <div class="row">
                        <paw:input path="name" label="Tournament Name"/>
                    </div>
                    <div class="row">
                        <paw:input path="game_id" label="Game" containerType="half" inputType="select" items="${games}" itemValue="game.id" itemLabel="game.name"/>
                        <paw:input path="region" label="Region" containerType="half" inputType="select" items="${regions}"/>
                    </div>
                    <div class="row">
                        <paw:input path="start_date" label="Start Date" containerType="half" inputType="date"/>
                        <paw:input path="end_date" label="End Date" containerType="half" inputType="date"/>
                    </div>
                    <div class="row center">
                        <paw:input path="max_participants" label="Max Participants" containerType="half" inputType="number"/>
                        <paw:input path="structure" label="Structure" containerType="half" inputType="select" items="${structures}"/>
                    </div>
                    <div class="row">
                        <paw:input path="format" label="Format" containerType="half"/>
                        <paw:input path="elo" label="Skill level" containerType="half" inputType="select" items="${elos}"/>
                    </div>
                    <div class="row">
                        <paw:input path="image" label="Image" inputType="file"/>
                    </div>
                    <div class="row center">
                        <paw:input path="" label="Create" containerType="half" inputType="submit"/>
                    </div>
                </form:form>
            </paw:modal>
            <paw:button-card title="Test your abilities" butText="Join a Tournament" onclick="${joinTournamentFunction}" texture="true"/>
        </div>
        <div class="content-title">
            <paw:text type="title" size="l"><spring:message code="games"/></paw:text>
        </div>
        <paw:carrousel id="game-list" elements="${games}" isGame="true"/>

        <div class="content-title">
            <paw:text type="title" size="l"><spring:message code="tournaments"/></paw:text>
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