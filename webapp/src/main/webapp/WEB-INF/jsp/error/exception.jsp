<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/" var="homeUrl"/>

<paw:form-layout>
    <paw:text type="title"><spring:message code="${title}"/></paw:text>
    <paw:text size="l"><spring:message code="${message}"/></paw:text>
    <br>
    <paw:button text="error404Page.goToHome" onclick="window.location.href='${homeUrl}'"/>
</paw:form-layout>
