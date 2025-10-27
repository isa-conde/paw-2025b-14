<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/" var="homeUrl"/>
<spring:message code="login.title" var="pageTitle"/>

<paw:form-layout pageTitle="${pageTitle}">
    <paw:text type="title"><spring:message code="passwordReset.success.title"/></paw:text>
    <paw:text size="l"><spring:message code="passwordReset.success.message"/></paw:text>
    <br>
    <paw:button onclick="window.location.href='${homeUrl}'" text="verification.goToHome"/>
</paw:form-layout>
