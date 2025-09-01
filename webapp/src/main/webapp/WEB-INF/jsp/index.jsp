<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    
<paw:layout user="${user != null ? user : null}">

    <c:url value="/login" var="userLoginPath"/>
    <c:url value="/register" var="userRegisterPath"/>

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
        <paw:text type="title" size="xl">Are you ready?</paw:text>
        <br>
        <paw:text type="title" size="l">Let's play.</paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="openModal('createTournamentModal')" texture="true"/>
            <paw:modal id="createTournamentModal" title="Create a Tournament">
                <form:form cssClass="form">
                    <div class="row">
                        <paw:input path="name" label="Tournament Name"/>
                    </div>
                    <div class="row">
                        <paw:input path="game" label="Game" containerType="half" inputType="select"/>
                        <paw:input path="region" label="Region" containerType="half" inputType="select"/>
                    </div>
                    <div class="row">
                        <paw:input path="startDate" label="Start Date" containerType="half" inputType="date"/>
                        <paw:input path="endDate" label="End Date" containerType="half" inputType="date"/>
                    </div>
                    <div class="row">
                        <paw:input path="format" label="Format" containerType="half" inputType="select"/>
                        <paw:input path="elo" label="Skill level" containerType="half" inputType="select"/>
                    </div>
                    <div class="row center">
                        <paw:input path="maxParticipants" label="Max Participants" containerType="half" inputType="number"/>
                    </div>
                    <div class="row center">
                        <paw:input path="" label="Create" containerType="half" inputType="submit"/>
                    </div>
                </form:form>
            </paw:modal>
            <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick="" texture="true"/>
        </div>
        <c:set var="tournamentList" value="${[1,2,3,4,5,6,7]}"/>

        <div class="content-title">
            <paw:text type="title" size="l">Games</paw:text>
        </div>
        <paw:carrousel id="10" elements="${tournamentList}"/>

        <div class="content-title">
            <paw:text type="title" size="l">Open Tournaments</paw:text>
        </div>

        <div class="carrousel-title">
            <paw:text type="title" size="s">League of Legends</paw:text>
        </div>
        <paw:carrousel id="0" elements="${tournamentList}"/>
        <div class="carrousel-title">
            <paw:text type="title" size="s">League of Legends</paw:text>
        </div>
        <paw:carrousel id="1" elements="${tournamentList}"/>
        <div class="carrousel-title">
            <paw:text type="title" size="s">League of Legends</paw:text>
        </div>
        <paw:carrousel id="2" elements="${tournamentList}"/>

    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>