<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="profileUrl" value="/profile/${profile.id}"/>
<c:url var="starUrl" value="/images/roundedStarOn.png"/>
<c:url var="commentUrl" value="/profile/${profile.id}/comment"/>

<paw:layout user="${user}" pageTitle="${profile.username}" function="${openModal}">

    <c:set var="isMyProfile" value="${user.id == profile.id}"/>
    <c:url value="/images/pencil.png" var="pencilUrl"/>
    <c:set var="icon" value="${isMyProfile ? pencilUrl : null }"/>
    <c:url value="/banner/${profile.bannerId}" var="bannerUrl"/>
    <paw:banner cornerIcon="${icon}" cornerOnClick="openModal('editProfileModal')" image="${bannerUrl}" size="profile">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/pfp/${profile.pfpId}" alt="${profile.username}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
                <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
            </div>
            <div class="profile-rating">
                <paw:text size="m"><spring:message code="profile.rating.organizerRating"/></paw:text>
                <div class="rating-container-profile">
                    <img src="${starUrl}" class="banner-star" alt="tournament.rating.imgLabel"/>
                    <c:if test="${userRating == null}">
                        <spring:message code="profile.rating.noRating" var="noRating"/>
                        <c:set var="userRating" value="${noRating}"/>
                    </c:if>
                    <paw:text weight="thin" size="s"> ${userRating} </paw:text>
                </div>
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

                <div class="profile-comments">
                    <paw:text type="title"><spring:message code="profile.comments.title"/></paw:text>
                    <c:if test="${not empty user && !isMyProfile}">
                        <div class="profile-comments-form">
                            <spring:message code="profile.comments.placeholder" var="commentPlaceholder"/>
                            <spring:message code="profile.comments.submit" var="commentSubmit"/>
                            <form:form method="post" modelAttribute="commentForm" action="${commentUrl}" cssClass="comment-form">
                                <form:textarea path="comment" cssClass="input textarea comment-textarea" placeholder="${commentPlaceholder}"/>
                                <form:errors path="comment" cssClass="form-error" element="span"/>
                                <button type="submit" class="btn comment-submit">${commentSubmit}</button>
                            </form:form>
                        </div>
                    </c:if>

                    <div class="profile-comments-list">
                        <c:if test="${empty comments}">
                            <div class="profile-comment-empty">
                                <paw:text weight="thin"><spring:message code="profile.comments.empty"/></paw:text>
                            </div>
                        </c:if>
                        <c:forEach var="comment" items="${comments}">
                            <div class="profile-comment">
                                <div class="profile-comment-header">
                                    <c:set var="commenterProfileUrl" value="/profile/${comment.commenter.id}"/>
                                    <a href="${commenterProfileUrl}" class="profile-comment-author"><c:out value="${comment.commenter.username}"/></a>
                                    <span class="profile-comment-date"><c:out value="${comment.formattedDate}"/></span>
                                </div>
                                <div class="profile-comment-body">
                                    <c:out value="${comment.comment}"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
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
