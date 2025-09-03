<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout user="${user}">
    <paw:banner image="${pageContext.request.contextPath}/images/tree.jpg" cropTop="true">
        <div class="page-title">
            <paw:text type="title" size="xl" stroke="true">My Tournaments</paw:text>
        </div>
    </paw:banner>
    <c:set var="navbarSections" value="${['Active', 'Finished', 'Owned']}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'Active'}"/>
    <paw:navbar sections="${navbarSections}" activeSection="${activeSection}"/>
    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'Active'}">
                <div class="grid-title">
                    <paw:text type="title">Active Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${joinedTournaments}" id="on-going-${user.id}"/>
            </c:when>
            <c:when test="${activeSection == 'Finished'}">
                <div class="grid-title">
                    <paw:text type="title">Finished Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${pastTournaments}" id="finished-${user.id}"/>
            </c:when>
            <c:when test="${activeSection == 'Owned'}">
                <div class="grid-title">
                    <paw:text type="title">Your Active Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${onGoingTournaments}" id="on-going-${user.id}-creations"/>
                <div class="grid-title">
                    <paw:text type="title">Your Finished Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${finishedTournaments}" id="finished-${user.id}-creations"/>
            </c:when>
        </c:choose>
    </div>
</paw:layout>