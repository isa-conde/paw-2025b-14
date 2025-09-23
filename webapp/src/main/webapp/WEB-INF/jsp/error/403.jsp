<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/verify?userId=${user.id}" var="resendVerificationUrl"/>

<paw:form-layout title="403 Forbidden">
    <paw:text type="title">Access Denied</paw:text>
    <c:choose>
        <c:when test="${!user.verified}">
            <paw:text size="l">It looks like you haven't verified your account. To proceed, please request verification below.</paw:text>
            <br>
            <form:form method="post" action="${resendVerificationUrl}">
                <paw:input path="" label="Resend Verification" inputType="submit"/>
            </form:form>
        </c:when>
        <c:otherwise>
            <paw:text size="l">banana</paw:text>
        </c:otherwise>
    </c:choose>
</paw:form-layout>
