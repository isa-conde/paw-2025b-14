<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<paw:layout>

    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="h1">Are you ready?</paw:text>
        <br>
        <paw:text type="h2">Let's play.</paw:text>
    </paw:banner>

        <div class="cards-container">
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="openModal()"/>
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

            <paw:button-card title="Test your abilities" butText="Join a Tournament" onclick=""/>
        </div>

</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>
