<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/verify?userId=${user.id}" var="resendVerificationUrl"/>
<c:url value="/" var="homeUrl"/>

<spring:message code="error403Page.pageTitle" var="pageTitle"/>
<paw:form-layout pageTitle="${pageTitle}">
    <paw:text type="title"><spring:message code="error403Page.pageTitle"/></paw:text>
    <c:choose>
        <c:when test="${!user.verified}">
            <paw:text size="l"><spring:message code="error403Page.unverifiedMessage"/></paw:text>
            <br>
            <form:form method="post" action="${resendVerificationUrl}">
                <paw:input path="" label="error403Page.resendVerificationButton" inputType="submit"/>
            </form:form>
        </c:when>
        <c:otherwise>
            <paw:text size="l"><spring:message code="error403Page.generalAccessDenied"/></paw:text>
            <br>
            <paw:button text="error404Page.goToHome" onclick="window.location.href='${homeUrl}'"/>
        </c:otherwise>
    </c:choose>
</paw:form-layout>
