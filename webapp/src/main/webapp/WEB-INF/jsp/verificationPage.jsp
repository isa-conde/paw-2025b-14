<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="resendVerificationUrl" value="/verify?userId=${user.id}"/>

<paw:form-layout title="requestVerificationPage.pageTitle">

    <paw:text type="title">
        <spring:message code="requestVerificationPage.pageHeader"/>
    </paw:text>
    <paw:text size="l">
        <spring:message code="requestVerificationPage.pageDescription"/>
    </paw:text>
    <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="requestVerificationPage.resendVerification" inputType="submit"/>
    </form:form>

</paw:form-layout>