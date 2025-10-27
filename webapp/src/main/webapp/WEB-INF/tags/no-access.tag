<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="goToIndex" value="window.location.href='${pageContext.request.contextPath}/'"/>

<div class="no-access-container">
    <paw:text type="title" size="xl" weight="bold" stroke="true"><spring:message code="noAccess.title"/></paw:text>
    <paw:text type="main-text" size="l" weight="semi-bold"><spring:message code="noAccess.subtitle"/></paw:text>
    <br>
    <paw:button onclick="${goToIndex}" text="noAccess.goHome"/>
</div>