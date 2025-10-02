<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="function" required="false" type="java.lang.String" %>
<%@ attribute name="pageTitle" required="false" %>

<c:set var="isLoggedIn" value="${user != null}"/>

<c:url value="/register" var="registerUrl"/>
<c:url value="/login" var="loginUrl"/>
<c:url value="/logout" var="logoutUrl"/>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <link rel="icon" type="image/x-icon" href="<c:url value="/public/favicon.ico"/>">
        <title><c:out value="${pageTitle != null ? pageTitle : 'RankUp'}"/></title>
    </head>
    <c:choose>
        <c:when test="${function != null}">
            <body onload="openModal(${function})">
        </c:when>
        <c:otherwise>
            <body>
        </c:otherwise>
    </c:choose>
        <div class="container">
            <paw:sidebar user="${user}"/>
            <paw:header>
                <paw:searchBar/>
                <c:choose>
                    <c:when test="${isLoggedIn}">
                        <div class="header-buttons">
                            <paw:profileButton text="${user.username}" onclick="window.location.href='/profile/${user.id}'" isNotSafe="true"/>
                            <paw:logoutButton/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div>
                            <paw:button text="layout.login" size="m" onclick="window.location.href='${loginUrl}'"/>
                            <paw:button text="layout.register" size="m" onclick="window.location.href='${registerUrl}'"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </paw:header>
            <main class="main-content">
                <jsp:doBody/>
            </main>
        </div>
    </body>
</html>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>