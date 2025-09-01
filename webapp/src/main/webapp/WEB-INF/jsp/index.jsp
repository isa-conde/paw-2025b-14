<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<paw:layout user="${user != null ? user : null}">

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
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="openModal('createTournamentModal')" texture="true"/>
            <paw:modal id="createTournamentModal" title="Create a Tournament">
                <form:form cssClass="form" modelAttribute="tournamentForm" action="${createTournamentPath}" method="post">
                    <div class="row">
                        <paw:input path="name" label="Tournament Name"/>
                    </div>
                    <div class="row">
                        <paw:input path="gameid" label="Game" containerType="half" inputType="select" items="${games}" itemValue="id" itemLabel="name"/>
                        <paw:input path="region" label="Region" containerType="half" inputType="select" items="${regions}"/>
                    </div>
                    <div class="row">
                        <paw:input path="startdate" label="Start Date" containerType="half" inputType="date"/>
                        <paw:input path="enddate" label="End Date" containerType="half" inputType="date"/>
                    </div>
                    <div class="row">
                        <paw:input path="format" label="Format" containerType="half"/>
                        <paw:input path="elo" label="Skill level" containerType="half" inputType="select" items="${elos}"/>
                    </div>
                    <div class="row center">
                        <paw:input path="max_participants" label="Max Participants" containerType="half" inputType="number"/>
                        <paw:input path="structure" label="Structure" items="${structures}" inputType="select"/>
                    </div>
                    <div class="row center">
                        <paw:input path="" label="Create" containerType="half" inputType="submit"/>
                    </div>
                </form:form>
            </paw:modal>
            <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick="window.location.href='${pageContext.request.contextPath}/tournaments-page'" texture="true"/>
        </div>
        <div class="content-title">
            <paw:text type="title" size="l"><spring:message code="games"/></paw:text>
        </div>
        <paw:carrousel id="game-list" elements="${games}" noFormat="true"/>

        <div class="content-title">
            <paw:text type="title" size="l"><spring:message code="tournaments"/></paw:text>
        </div>

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

    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>