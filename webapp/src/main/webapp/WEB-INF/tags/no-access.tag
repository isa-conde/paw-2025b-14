<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="goToIndex" value="window.location.href='${pageContext.request.contextPath}/'"/>

<div class="no-access-container">
    <paw:text type="title" size="xl" weight="bold" stroke="true">Access Denied</paw:text>
    <paw:text type="main-text" size="l" weight="semi-bold">To view tournaments and games, please log in or register.</paw:text>
    <br>
    <paw:button onclick="${goToIndex}" text="Go to Home"/>
</div>