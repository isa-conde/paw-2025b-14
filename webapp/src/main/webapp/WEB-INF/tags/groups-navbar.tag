<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="groups" required="true" type="java.lang.Integer" %>
<%@ attribute name="activeGroup" required="true" type="java.lang.Integer" %>
<%@ attribute name="paramName" required="false" type="java.lang.String"  %>

<c:set var="paramName" value="${empty paramName ? 'section' : paramName}"/>

<nav class="navbar">
    <c:forEach var="g" begin="1" end="${groups}" step="1" varStatus="status">
        <c:set var="isActive" value="${g == activeGroup}" />

        <div class="navbar-section ${isActive ? 'active' : ''}"
             onclick="switchNavbar('${paramName}','${g}')">
            <paw:text>
                <spring:message code="tournament.group" arguments="${g}" />
            </paw:text>
        </div>

        <div class="navbar-separator"></div>
    </c:forEach>
</nav>