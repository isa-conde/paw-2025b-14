<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}" pageTitle="${profile.username}">

    <c:set var="isMyProfile" value="${user.id == profile.id}"/>
    <c:url value="/images/pencil.png" var="pencilUrl"/>
    <c:set var="icon" value="${isMyProfile ? pencilUrl : null }"/>
    <paw:banner cornerIcon="${icon}" cornerOnClick="openModal('editProfileModal')" image="${pageContext.request.contextPath}/banner/${profile.banner_id}">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/pfp/${profile.profile_picture_id}" alt="${profile.username}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
                <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
            </div>
        </div>
    </paw:banner>

        <div class="content-container">
            <!-- Sección central: carruseles -->
            <div class="profile-main">
                <div class="carrousel-title">
                    <paw:text size="xl"><spring:message code="profile.favouriteGames.title"/></paw:text>
                </div>
                <paw:carrousel id="games" elements="${favouriteGames}" isGame="true"/>

                <div class="carrousel-title">
                    <paw:text size="xl"><spring:message code="profile.lastTournaments.title"/></paw:text>
                </div>
                <paw:carrousel id="tourneys" elements="${lastTournaments}"/>
            </div>
        </div>



</paw:layout>

<paw:modal title="profile.edit.modal.title" id="editProfileModal">
    <form:form method="post" modelAttribute="EditProfileForm"
               action="${pageContext.request.contextPath}/profile/update"
               enctype="multipart/form-data" cssClass="form">
        <input type="hidden" name="userId" value="${profile.id}"/>
        <div class="row">
            <paw:input path="username" label="profile.edit.modal.username" hasConstraint="true" value="${profile.username}"/>
        </div>
        <div class="row">
            <paw:input path="bio" label="profile.edit.modal.bio" hasConstraint="true" value="${profile.bio}"/>
        </div>
        <div class="row">
            <paw:input path="profilePicture" label="profile.edit.modal.profilePicture" inputType="file" hasConstraint="true"/>
        </div>
        <div class="row">
            <paw:input path="bannerPicture" label="home.createTournament.image" inputType="file"/>
        </div>
        <div class="row center">
            <paw:input path="" label="tournament.edit.saveChanges" containerType="half" inputType="submit"/>
        </div>
    </form:form>
</paw:modal>
