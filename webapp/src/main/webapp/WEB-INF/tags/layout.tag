<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>

<c:set var="isLoggedIn" value="${user != null}"/>
<c:url value="/logout" var="logoutUrl"/>

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
                    <paw:profileButton text="${user.username}" onclick="openModal('logoutModal')"/>
                    <paw:modal id="logoutModal" title="Logout">
                        <div class="row center">
                            <paw:button text="Log out" onclick="window.location.href='${logoutUrl}'"/>
                        </div>
                    </paw:modal>
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