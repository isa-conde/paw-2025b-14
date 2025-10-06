<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}" function="${openModal}">

    <c:set var="isMyTeam" value="${user.id == team.owner_id}"/>
    <c:url value="/images/pencil.png" var="pencilUrl"/>
    <c:set var="icon" value="${isMyTeam ? pencilUrl : null }"/>
    <paw:banner cornerIcon="${icon}" cornerOnClick="openModal('editProfileModal')" image="${pageContext.request.contextPath}/banner/${team.banner_id}">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/pfp/${team.pfp_id}" alt="${team.name}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${team.name}"/></paw:text>
            </div>
            <div class="organizer-container">
                <paw:text size="s"><spring:message code="team.profile.createdBy"/></paw:text>
                <paw:profileButton imageId="${owner.pfp_id}" text="${owner.username}" onclick="window.location.href='/profile/${owner.id}'" size="xs" isNotSafe="true"/>
            </div>
        </div>
    </paw:banner>


    <spring:message code="team.profile.overview" var="overview"/>
    <spring:message code="team.profile.members" var="membersTab"/>
    <c:set var="sections" value="${['overview', 'membersTab']}"/>
    <c:set var="labels"   value="${[overview, membersTab]}"/>
    <c:set var="activeSection" value="${param.section}"/>

    <paw:navbar sections="${sections}" labels="${labels}" activeSection="${activeSection}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'membersTab'}">
                <div class="users-grid-title">
                    <spring:message code="team.profile.members" var="ptitle"/>
                    <paw:text type="title" size="l">${ptitle}</paw:text>
                </div>
                <paw:members members="${members}"/>
            </c:when>
            <c:otherwise>
                <div class="profile-main">
                    <div class="carrousel-title">
                        <paw:text size="xl"><spring:message code="team.profile.activeTournaments"/></paw:text>
                    </div>
                    <c:choose>
                        <c:when test="${activeTournaments.size() == 0}">
                            <div class="no-cards-container">
                                <paw:text><spring:message code="team.profile.noActiveTournaments" arguments="${team.name}"/></paw:text>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <paw:carrousel id="activetourneys" elements="${activeTournaments}"/>
                        </c:otherwise>
                    </c:choose>
                    <div class="carrousel-title">
                        <paw:text size="xl"><spring:message code="team.profile.pastTournaments"/></paw:text>
                    </div>
                    <c:choose>
                        <c:when test="${pastTournaments.size() == 0}">
                            <div class="no-cards-container">
                                <paw:text><spring:message code="team.profile.noPastTournaments" arguments="${team.name}"/></paw:text>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <paw:carrousel id="pasttourneys" elements="${pastTournaments}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>


    </div>
</paw:layout>


<paw:modal title="team.profile.edit.title" id="editProfileModal" returnUrl="/team/profile/${team.id}">
    <form:form method="post" modelAttribute="teamForm"
               action="${pageContext.request.contextPath}/team/update"
               enctype="multipart/form-data" cssClass="form">
        <input type="hidden" name="teamId" value="${team.id}"/>
        <div class="row">
            <paw:input path="name" label="team.profile.edit.name" hasConstraint="true"/>
        </div>
        <div class="row">
            <paw:input path="profilePicture" label="team.create.teamImage" inputType="file" hasConstraint="true"/>
        </div>
        <div class="row">
            <paw:input path="bannerPicture" label="team.create.teamBanner" inputType="file"/>
        </div>
        <div class="input-container">
            <label for="memberInput" class="input-label"><spring:message code="team.create.members"/></label>
            <div class="row center member-input-container">
                <input type="text" id="memberInput" placeholder="<spring:message code="team.create.addMember.placeholder"/>" class="input" />
                <button type="button" id="addMemberBtn" class="btn submit"><spring:message code="team.create.add"/></button>
            </div>
            <form:errors path="members" cssClass="form-error" element="h1"/>
            <div id="chipContainer" class="chip-container">
                <c:forEach var="member" items="${teamForm.members}">
                    <div class="chip">
                        <c:out value="${member}"/>
                        <span class="chip-close">X</span>
                        <input type="hidden" name="members" value="${member}"/>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="row center">
            <paw:input path="" label="tournament.edit.saveChanges" containerType="half" inputType="submit"/>
        </div>
    </form:form>
</paw:modal>

<script src="${pageContext.request.contextPath}/js/teamMembers.js"></script>
