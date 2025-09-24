<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/login" var="loginUrl"/>
<spring:message code='passwordReset.success.pageTitle' var="title"/>

<paw:form-layout title="${title}">
    <paw:text type="title"><spring:message code="passwordReset.success.title"/></paw:text>
    <paw:text size="l"><spring:message code="passwordReset.success.message"/></paw:text>
    <br>
    <paw:button onclick="window.location.href='${loginUrl}'" text="passwordReset.success.goToLogin"/>
</paw:form-layout>
