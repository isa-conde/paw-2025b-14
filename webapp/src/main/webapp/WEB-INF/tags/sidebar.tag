<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<c:set var="isLoggedIn" value="${user != null}"/>

<aside class="sidebar">
    <div class="sidebar-content">
        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/home.png" 
            text="sidebar.home"
            href="${pageContext.request.contextPath}/"
            active="${pageContext.request.requestURI.endsWith('/') || pageContext.request.requestURI.endsWith('/index.jsp') ? 'true' : 'false'}"/>
        
        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/joystick.png" 
            text="sidebar.games"
            href="${pageContext.request.contextPath}/gamesPage"
            active="${pageContext.request.requestURI.contains('/gamesPage') ? 'true' : 'false'}"/>

        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/cup.png" 
            text="sidebar.tourneys"
            href="${pageContext.request.contextPath}/tournamentsPage"
            active="${pageContext.request.requestURI.contains('/tournamentsPage') ? 'true' : 'false'}"/>

        <c:set var="profilePath" value="/profile/${user.id}/tournaments"/>
        <c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri']}" />
        <c:choose>
            <c:when test="${isLoggedIn}">
                <paw:sidebar-button
                        icon="${pageContext.request.contextPath}/images/badge.png"
                        text="sidebar.myTourneys"
                        href="${pageContext.request.contextPath}/profile/${user.id}/tournaments"
                        active="${uri.contains(profilePath) ? 'true' : 'false'}"/>
            </c:when>
        </c:choose>
    </div>
</aside>
