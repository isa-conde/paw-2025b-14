<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>

<c:set var="isLoggedIn" value="${user != null}"/>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
    <body>
        <div class="container">
            <paw:sidebar/>
            <paw:header>
                <c:choose>
                    <c:when test="${isLoggedIn}">
                        <paw:profileButton text="${user.username}" onclick="" disabled="true"/>
                    </c:when>
                    <c:otherwise>
                        <paw:button text="Log in" size="m" onclick=""/>
                    </c:otherwise>
                </c:choose>
            </paw:header>
            <main class="main-content">
                <jsp:doBody/>
            </main>
        </div>
    </body>
</html>