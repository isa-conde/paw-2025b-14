<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<paw:layout user="${user}">

    <paw:banner image="${pageContext.request.contextPath}/banner/${profile.banner_id}"/>

    <div class="profile-container">
        <div class="profile-sidebar">
            <div class="profile-picture">
                <img src="${pageContext.request.contextPath}/pfp/${profile.profile_picture_id}" alt="${profile.username}">
            </div>
            <div class="profile-info">
                <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
                <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
            </div>
        </div>

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