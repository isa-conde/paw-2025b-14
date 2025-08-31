<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout>
    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="title" size="xl">Are you ready?</paw:text>
        <br>
        <paw:text type="title" size="l">Let's play.</paw:text>
    </paw:banner>
    <div class="content-container">
        <div class="cards-container">
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="" texture="true"/>
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