<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="profileUrl" value="/profile/${profile.id}"/>
<c:url var="starUrl" value="/images/roundedStarOn.png"/>

<paw:layout user="${user}" pageTitle="${profile.username}" function="${openModal}">

    <c:set var="isMyProfile" value="${user.id == profile.id}"/>
    <c:url value="/images/pencil.png" var="pencilUrl"/>
    <c:set var="icon" value="${isMyProfile ? pencilUrl : null }"/>
    <c:url value="/banner/${profile.banner_id}" var="bannerUrl"/>
    <paw:banner cornerIcon="${icon}" cornerOnClick="openModal('editProfileModal')" image="${bannerUrl}" size="profile">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/pfp/${profile.pfp_id}" alt="${profile.username}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
                <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
            </div>
            <div class="rating-container-profile">
                <img src="${starUrl}" class="banner-star" alt="tournament.rating.imgLabel"/>
                <paw:text weight="thin" size="s"> ${userRating} </paw:text>
            </div>
        </div>
    </paw:banner>
    <paw:profile-navbar user="${profile}" activeSection="overview"/>
        <div class="content-container">
            <div class="profile-main">
                <div class="carrousel-title">
                    <paw:text type="title"><spring:message code="profile.favouriteGames.title"/></paw:text>
                </div>
                <c:choose>
                    <c:when test="${favouriteGames.size() > 0}">
                        <paw:carrousel id="games" elements="${favouriteGames}" isGame="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="no-cards-container">
                            <paw:text weight="thin"><spring:message code="profile.favouriteGames.empty" arguments="${profile.username}"/></paw:text>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="carrousel-title">
                    <paw:text type="title"><spring:message code="profile.upcomingTournaments.title"/></paw:text>
                </div>
                <c:choose>
                    <c:when test="${activeTournaments.size() > 0}">
                        <paw:carrousel id="activeTournaments" elements="${activeTournaments}"/>
                    </c:when>
                    <c:otherwise>
                        <div class="no-cards-container">
                            <paw:text weight="thin"><spring:message code="profile.activeTournaments.empty" arguments="${profile.username}"/></paw:text>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="carrousel-title">
                    <paw:text type="title"><spring:message code="profile.lastTournaments.title"/></paw:text>
                </div>
                <c:choose>
                    <c:when test="${lastTournaments.size() > 0}">
                        <paw:carrousel id="lastTournaments" elements="${lastTournaments}"/>
                    </c:when>
                    <c:otherwise>
                        <div class="no-cards-container">
                            <paw:text weight="thin"><spring:message code="profile.lastTournaments.empty" arguments="${profile.username}"/></paw:text>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
</paw:layout>

<paw:modal title="profile.edit.modal.title" id="editProfileModal" returnUrl="${profileUrl}">
    <form:form method="post" modelAttribute="editProfileForm"
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
            <paw:input path="profilePicture" label="profile.edit.modal.profilePicture" fileText="input.uploadImage" inputType="file" hasConstraint="true"/>
        </div>
        <div class="row">
            <paw:input path="bannerPicture" label="profile.edit.modal.bannerImage" fileText="input.uploadImage" inputType="file"/>
        </div>
        <div class="row center">
            <paw:input path="" label="tournament.edit.saveChanges" containerType="half" inputType="submit"/>
        </div>
    </form:form>
</paw:modal>
