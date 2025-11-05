<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="activeSection" required="true" type="java.lang.String" %>



<spring:message code="profile.navbar.overview" var="overviewLabel"/>
<spring:message code="profile.navbar.tournaments" var="tournamentsLabel"/>
<spring:message code="profile.navbar.teams" var="teamsLabel"/>

<c:set var="profileId" value="${user.id}" />

<c:set var="navbarSections" value="${['overview', 'tournaments', 'teams']}" />
<c:set var="navbarLabels" value="${[overviewLabel, tournamentsLabel, teamsLabel]}" />
<c:url var="tourneysUrl" value="/profile/${profileId}/tournaments"/>
<c:url var="overviewUrl" value="/profile/${profileId}"/>
<c:url var="teamsUrl" value="/profile/${profileId}/teams"/>
<c:set var="navbarUrls" value="${[overviewUrl, tourneysUrl, teamsUrl]}"/>



<nav class="navbar">
    <c:forEach var="section" items="${navbarSections}" varStatus="status">
        <c:set var="label" value="${navbarLabels[status.index]}" />
        <c:set var="isActive" value="${section == activeSection}" />

        <a href="${navbarUrls[status.index]}" class="navbar-section ${isActive ? 'active' : ''}">
            <div>
                <paw:text>${label}</paw:text>
            </div>
        </a>


        <div class="navbar-separator"></div>
    </c:forEach>
</nav>