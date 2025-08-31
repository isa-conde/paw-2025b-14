<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
<paw:layout user="${user != null ? user : null}">
    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="title" size="xl"><spring:message code="welcome.title"/></paw:text>
        <br>
        <paw:text type="title" size="l"><spring:message code="welcome.subtitle"/></paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="openModal()" texture="true"/>
            <paw:modal title="Create a Tournament">
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