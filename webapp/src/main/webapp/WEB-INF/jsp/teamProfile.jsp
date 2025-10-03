<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}">

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
                <paw:profileButton text="${owner.username}" onclick="window.location.href='/profile/${owner.id}'" size="xs" isNotSafe="true"/>
            </div>
        </div>
    </paw:banner>

    <div class="content-container">
        <!-- Sección central: carruseles -->
        <div class="profile-main">
            <div class="carrousel-title">
                <paw:text size="xl"><spring:message code="team.profile.activeTournaments"/></paw:text>
            </div>
            <paw:carrousel id="tourneys" elements="${activeTournaments}"/>
            <div class="carrousel-title">
                <paw:text size="xl"><spring:message code="team.profile.pastTournaments"/></paw:text>
            </div>
            <paw:carrousel id="tourneys" elements="${pastTournaments}"/>
        </div>

    </div>



</paw:layout>
