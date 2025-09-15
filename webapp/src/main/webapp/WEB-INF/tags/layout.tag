<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="user" required="true" type="ar.edu.itba.paw.model.User" %>
<%@ attribute name="function" required="false" type="java.lang.String" %>
<%@ attribute name="isIndex" required="true"%>

<c:set var="isLoggedIn" value="${user != null}"/>
<c:url value="/logout" var="logoutUrl"/>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
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
                <c:choose>
                    <c:when test="${isLoggedIn}">
                        <paw:profileButton text="${user.username}" onclick="openModal('logoutModal')" isNotSafe="true"/>
                        <paw:modal id="logoutModal" title="layout.logout">
                            <div class="row center">
                                <paw:button text="layout.logout" onclick="window.location.href='${logoutUrl}'"/>
                            </div>
                        </paw:modal>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${isIndex == 'true'}">
                            <div>
                                <paw:button text="layout.login" size="m" onclick="openModal('loginModal')"/>
                                <paw:button text="layout.register" size="m" onclick="openModal('registerModal')"/>
                            </div>
                        </c:if>
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