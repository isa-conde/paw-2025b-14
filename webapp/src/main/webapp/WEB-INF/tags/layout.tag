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
                <c:if test="${isLoggedIn}">
                    <paw:profileButton text="${user.username}" onclick=""/>
                </c:if>
                <c:if test="${!isLoggedIn}">
                    <div>
                        <paw:button text="Log in" onclick="openModal('loginModal')"/>
                        <paw:button text="Register" onclick="openModal('registerModal')"/>
                    </div>
                </c:if>
            </paw:header>
            <main class="main-content">
                <jsp:doBody/>
            </main>
        </div>
    </body>
</html>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>