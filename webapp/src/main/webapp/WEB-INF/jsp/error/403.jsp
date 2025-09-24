<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/verify?userId=${user.id}" var="resendVerificationUrl"/>

<paw:form-layout title="error403Page.pageTitle">
    <paw:text type="title"><spring:message code="error403Page.accessDeniedTitle"/></paw:text>
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
        </c:otherwise>
    </c:choose>
</paw:form-layout>
