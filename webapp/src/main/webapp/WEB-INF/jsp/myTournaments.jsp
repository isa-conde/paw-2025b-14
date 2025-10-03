<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="myTournaments.title" var="title"/>
<paw:layout user="${user}" pageTitle="${title}">
    <paw:banner image="${pageContext.request.contextPath}/images/tree.jpg" cropTop="true">
        <div class="page-title">
            <paw:text type="title" size="xl" stroke="true">${title}</paw:text>
        </div>
    </paw:banner>
    <spring:message code="tournament.navbar.active" var="activeLabel"/>
    <spring:message code="tournament.navbar.finished" var="finishedLabel"/>
    <spring:message code="tournament.navbar.owned" var="ownedLabel"/>

    <c:set var="navbarSections" value="${['active','finished','owned']}"/>
    <c:set var="navbarLabels" value="${[activeLabel, finishedLabel, ownedLabel]}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'active'}"/>

    <paw:navbar sections="${navbarSections}" labels="${navbarLabels}" activeSection="${activeSection}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'active'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${joinedTournaments}" id="on-going-${user.id}"/>
            </c:when>
            <c:when test="${activeSection == 'finished'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${pastTournaments}" id="finished-${user.id}"/>
            </c:when>
            <c:when test="${activeSection == 'owned'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${onGoingTournaments}" id="on-going-${user.id}-creations"/>
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${finishedTournaments}" id="finished-${user.id}-creations"/>
            </c:when>
        </c:choose>
    </div>
</paw:layout>