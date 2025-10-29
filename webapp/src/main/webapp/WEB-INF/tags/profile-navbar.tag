<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="activeSection" required="true" type="java.lang.String" %>



<spring:message code="profile.navbar.overview" var="overviewLabel"/>
<spring:message code="profile.navbar.tournaments" var="tournamentsLabel"/>

<c:set var="profileId" value="${user.id}" />

<c:set var="navbarSections" value="${['overview', 'tournaments']}" />
<c:set var="navbarLabels" value="${[overviewLabel, tournamentsLabel]}" />
<c:url var="tourneysUrl" value="/profile/${profileId}/tournaments"/>
<c:url var="overviewUrl" value="/profile/${profileId}"/>

<nav class="navbar">
    <c:forEach var="section" items="${navbarSections}" varStatus="status">
        <c:set var="label" value="${navbarLabels[status.index]}" />
        <c:set var="isActive" value="${section == activeSection}" />

        <div class="navbar-section ${isActive ? 'active' : ''}"
             onclick="switchProfileNavbar('${profileId}', '${section}')">
            <paw:text>${label}</paw:text>
        </div>

        <div class="navbar-separator"></div>
    </c:forEach>
</nav>

<script src="${pageContext.request.contextPath}/js/profileNavbar.js"></script>
