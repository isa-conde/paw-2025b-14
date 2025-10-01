<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<paw:layout user="${user}">

    <paw:banner image="${pageContext.request.contextPath}/images/defaultBanner.jpg"/>

    <div class="profile-container">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/images/defaultPFP.jpg" alt="${user.username}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${user.username}"/></paw:text>
                <paw:text size="m"><c:out value="${user.bio}"/></paw:text>
                <p>${user.bio}</p>
            </div>
        </div>

        <!-- Sección central: carruseles -->
        <div class="profile-main">
            <paw:text size="xl"><spring:message code="profile.favouriteGames.title"/></paw:text>
            <paw:carrousel id="" elements=""  />

            <paw:text size="xl"><spring:message code="profile.lastTournaments.title"/></paw:text>
            <paw:carrousel id="" elements=""  />
        </div>
    </div>


</paw:layout>