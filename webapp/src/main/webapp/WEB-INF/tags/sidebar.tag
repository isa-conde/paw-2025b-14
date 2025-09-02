<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<c:set var="isLoggedIn" value="${user != null}"/>

<aside class="sidebar">
    <div class="sidebar-content">
        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/home.png" 
            text="Home" 
            href="${pageContext.request.contextPath}/"
            active="${pageContext.request.requestURI.endsWith('/') || pageContext.request.requestURI.endsWith('/index.jsp') ? 'true' : 'false'}"/>
        
        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/joystick.png" 
            text="Games" 
            href="${pageContext.request.contextPath}/gamesPage"
            active="${pageContext.request.requestURI.contains('/gamesPage') ? 'true' : 'false'}"/>

        <paw:sidebar-button 
            icon="${pageContext.request.contextPath}/images/cup.png" 
            text="Tourneys" 
            href="${pageContext.request.contextPath}/tournamentsPage"
            active="${pageContext.request.requestURI.contains('/tournamentsPage') ? 'true' : 'false'}"/>

        <c:choose>
            <c:when test="${isLoggedIn}">
                <paw:sidebar-button
                        icon="${pageContext.request.contextPath}/images/badge.png"
                        text="My Tourneys"
                        href="${pageContext.request.contextPath}/myTournaments"
                        active="${pageContext.request.requestURI.contains('/myTournaments') ? 'true' : 'false'}"/>
            </c:when>
        </c:choose>

        
    </div>
</aside>
