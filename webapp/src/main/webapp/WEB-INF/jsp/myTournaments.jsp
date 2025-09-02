<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout user="${user}">
    <paw:banner image="${pageContext.request.contextPath}/images/tree.jpg" cropTop="true">
        <div class="page-title">
            <paw:text type="title" size="xl" stroke="true">My Tournaments</paw:text>
        </div>
    </paw:banner>
    <c:set var="navbarSections" value="${['Joined Tournaments', 'Created Tournaments', 'Past Tournaments']}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'Joined Tournaments'}"/>
    <paw:navbar sections="${navbarSections}" activeSection="${activeSection}"/>
    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'Joined Tournaments'}">

            </c:when>
            <c:when test="${activeSection == 'Created Tournaments'}">
                <div class="grid-title">
                    <paw:text type="title">On Going Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${onGoingTournaments}" id="on-going-${user.id}-creations"/>
                <div class="grid-title">
                    <paw:text type="title">Finished Tournaments</paw:text>
                </div>
                <paw:elements-grid elements="${finishedTournaments}" id="finished-${user.id}-creations"/>
            </c:when>
            <c:when test="${activeSection == 'Past Tournaments'}">

            </c:when>
        </c:choose>
    </div>
</paw:layout>