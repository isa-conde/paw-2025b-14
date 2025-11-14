<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message code="createTournament.emptyOption" var="emptyOption"/>
<spring:message code="createTournament.pageTitle" var="pageTitle"/>


<paw:layout user="${user}" pageTitle="${pageTitle}">
    <div class="tournament-form-page">
        <div class="tournament-form-page__image">
            <img src="${pageContext.request.contextPath}/images/tournament.jpeg" alt="Tournament Image" />
        </div>

        <div class="tournament-form-page__form">
            <c:choose>
                <c:when test="${step == 1}">
                    <form:form cssClass="form center" modelAttribute="tournamentForm" action="${pageContext.request.contextPath}/tournaments/new/step1" method="post" enctype="multipart/form-data">
                        <div>
                            <paw:text type="title" size="xl"><spring:message code="createTournament.Step1"/></paw:text>
                            <div class="row">
                                <paw:input path="name" label="createTournament.name" hasConstraint="true"/>
                            </div>
                            <div class="row">
                                <paw:input path="region" label="createTournament.region" inputType="select" items="${regions}" hasConstraint="true" emptyOption="${emptyOption}" itemValue=""/>
                                <paw:input path="gameId" label="createTournament.game"  inputType="select" items="${games}" itemValue="id" itemLabel="name" hasConstraint="true" emptyOption="${emptyOption}"/>
                            </div>
                            <div class="row">
                                <paw:input path="structure" label="createTournament.structure" inputType="select" items="${structures}" emptyOption="${emptyOption}" hasConstraint="true"/>
                            </div>
                            <div class="row">
                                <paw:input path="startDate" label="createTournament.startDate"  inputType="date" hasConstraint="true"/>
                                <paw:input path="endDate" label="createTournament.endDate" inputType="date" hasConstraint="true"/>
                            </div>
                            <div class="row center">
                                <paw:input path="" label="createTournament.next" containerType="half" inputType="submit"/>
                            </div>
                        </div>
                    </form:form>
                </c:when>
                <c:when test="${step == 2}">
                    <form:form cssClass="form" modelAttribute="tournamentForm" action="${pageContext.request.contextPath}/tournaments/new/step2" method="post" enctype="multipart/form-data" onsubmit="this.querySelector('button, input[type=submit]').disabled=true;" >
                        <div>
                            <paw:text type="title" size="xl"><spring:message code="createTournament.Step2"/></paw:text>
                            <div class="row">
                                <paw:input path="maxParticipants" label="createTournament.maxParticipants" inputType="number" hasConstraint="true" arg="${playersPerTeamMax}"/>
                                <paw:input path="formatId" label="createTournament.format" hasConstraint="true" inputType="select" items="${formats}" itemLabel="name" itemValue="id"/>
                            </div>
                            <div class="row">
                                <paw:input path="elo" label="createTournament.skillLevel" inputType="select" itemMap="${elos}" hasConstraint="true" emptyOption="${emptyOption}" itemValue="a"/>
                            </div>
                            <div class="row">
                                <paw:input path="image" label="createTournament.image" inputType="file" hasConstraint="true" accept="image/*" fileText="input.uploadImage"/>
                            </div>
                            <div class="row center">
                                <paw:input path="" label="createTournament.next" containerType="half" inputType="submit"/>
                            </div>
                        </div>
                    </form:form>
                </c:when>
                <c:when test="${step == 3}">
                    <form:form cssClass="form" modelAttribute="tournamentForm" action="${pageContext.request.contextPath}/tournaments/new/step3" method="post" enctype="multipart/form-data" onsubmit="this.querySelector('button, input[type=submit]').disabled=true;" >
                        <div>
                            <paw:text type="title" size="xl"><spring:message code="createTournament.Step3"/></paw:text>
                            <div class="row">
                                <paw:input path="serverName" label="createTournament.serverName" hasConstraint="true"/>
                            </div>
                            <div class="row">
                                <paw:input path="serverPassword" label="createTournament.serverPassword" hasConstraint="true"/>
                            </div>
                            <div class="row">
                                <paw:input path="discordChannel" label="createTournament.discordChannel" hasConstraint="true"/>
                            </div>
                            <div class="row">
                                <paw:input path="rules" label="createTournament.rules" inputType="file" accept=".pdf" hasConstraint="true" fileText="input.uploadPdf"/>
                            </div>
                            <div class="row center">
                                <paw:input path="" label="createTournament.next" containerType="half" inputType="submit"/>
                            </div>
                        </div>
                    </form:form>
                </c:when>
            </c:choose>
        </div>
    </div>
</paw:layout>

