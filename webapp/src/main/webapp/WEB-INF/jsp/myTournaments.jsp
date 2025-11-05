<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<paw:layout user="${user}" pageTitle="${profile.username} Tournaments">
<paw:banner image="${pageContext.request.contextPath}/banner/${profile.bannerId}">
    <div class="profile-sidebar">
        <div class="profile-picture">
            <img src="${pageContext.request.contextPath}/pfp/${profile.pfpId}" alt="${profile.username}">
        </div>
        <div class="profile-info">
            <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
            <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
        </div>
    </div>
</paw:banner>
    <c:url var="baseUrl" value="/profile/${profile.id}/tournaments"/>

    <c:url var="activeUrl" value="${baseUrl}">
        <c:param name="section" value="active"/>
        <c:param name="page1" value="0"/>
        <c:param name="page2" value="0"/>
    </c:url>

    <c:url var="finishedUrl" value="${baseUrl}">
        <c:param name="section" value="finished"/>
        <c:param name="page1" value="0"/>
        <c:param name="page2" value="0"/>
    </c:url>

    <c:url var="ownedUrl" value="${baseUrl}">
        <c:param name="section" value="owned"/>
        <c:param name="page1" value="0"/>
        <c:param name="page2" value="0"/>
    </c:url>

    <spring:message code="tournament.navbar.active" var="activeLabel"/>
    <spring:message code="tournament.navbar.finished" var="finishedLabel"/>
    <spring:message code="tournament.navbar.owned" var="ownedLabel"/>

    <c:set var="navbarSections" value="${['active','finished','owned']}"/>
    <c:set var="navbarLabels" value="${[activeLabel, finishedLabel, ownedLabel]}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'active'}"/>

    <paw:profile-navbar user="${profile}" activeSection="tournaments"/>
    <paw:navbar sections="${navbarSections}" labels="${navbarLabels}" activeSection="${activeSection}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'active'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${joinedTournaments}" id="on-going-${profile.id}"/>
                <paw:pagination currentPage="${currentPage1}" totalPages="${totalPages1}" url="/profile/${profile.id}/tournaments" pageNumber="1"/>
            </c:when>
            <c:when test="${activeSection == 'finished'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${pastTournaments}" id="finished-${profile.id}"/>
                <paw:pagination currentPage="${currentPage1}" totalPages="${totalPages1}" url="/profile/${profile.id}/tournaments" pageNumber="1"/>
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.finished.won"/></paw:text>
                </div>
                <paw:elements-grid elements="${wonTournaments}" id="won-${profile.id}"/>
                <paw:pagination currentPage="${currentPage2}" totalPages="${totalPages2}" url="/profile/${profile.id}/tournaments" pageNumber="2"/>
            </c:when>
            <c:when test="${activeSection == 'owned'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${onGoingTournaments}" id="on-going-${profile.id}-creations"/>
                <paw:pagination currentPage="${currentPage1}" totalPages="${totalPages1}" url="/profile/${profile.id}/tournaments" pageNumber="1"/>
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${finishedTournaments}" id="finished-${profile.id}-creations"/>
                <paw:pagination currentPage="${currentPage2}" totalPages="${totalPages2}" url="/profile/${profile.id}/tournaments" pageNumber="2"/>
            </c:when>
        </c:choose>
    </div>
</paw:layout>