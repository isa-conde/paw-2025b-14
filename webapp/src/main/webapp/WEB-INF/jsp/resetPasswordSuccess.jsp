<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/login" var="loginUrl"/>

<paw:form-layout title="resetPasswordSuccessPage.pageTitle">
    <paw:text type="title"><spring:message code="resetPasswordSuccessPage.successTitle"/></paw:text>
    <paw:text size="l"><spring:message code="resetPasswordSuccessPage.successMessage"/></paw:text>
    <br>
    <paw:button onclick="window.location.href='${loginUrl}'" text="resetPasswordSuccessPage.goToLoginButton"/>
</paw:form-layout>
