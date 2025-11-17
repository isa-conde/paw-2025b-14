<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="icon" required="true" rtexprvalue="true" description="Path to the icon image" %>
<%@ attribute name="text" required="true" rtexprvalue="true" description="Text to display below the icon" %>
<%@ attribute name="href" required="true" rtexprvalue="true" description="URL to navigate to (alternative to onclick)" %>
<%@ attribute name="active" required="false" rtexprvalue="true" description="Whether this button is currently active" %>

<c:set var="isActive" value="${not empty active && active == 'true'}"/>
<c:set var="buttonClass" value="sidebar-button ${isActive ? 'active' : ''}"/>

<a href="${href}" class="${buttonClass}">
    <img src="${icon}" alt="${text}" class="sidebar-icon"/>
    <div class="sidebar-text"><spring:message code="${text}"/></div>
</a>