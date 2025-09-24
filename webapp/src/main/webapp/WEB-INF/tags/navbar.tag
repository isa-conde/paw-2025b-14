<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="sections" required="true" type="java.util.List" %>
<%@ attribute name="labels" required="true" type="java.util.List" %>
<%@ attribute name="activeSection" required="true" rtexprvalue="true" %>
<%@ attribute name="paramName" required="false" rtexprvalue="true" %>

<c:set var="paramName" value="${empty paramName ? 'section' : paramName}"/>

<nav class="navbar">
    <c:forEach var="section" items="${sections}" varStatus="status">
        <c:set var="label" value="${labels[status.index]}"/>
        <c:set var="isActive" value="${section == activeSection}"/>

        <div class="navbar-section ${isActive ? 'active' : ''}"
             onclick="switchNavbar('${paramName}','${section}')">
            <paw:text>${label}</paw:text>
        </div>

        <div class="navbar-separator"></div>
    </c:forEach>
</nav>

<script>
    function switchNavbar(paramName, section) {
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.set(paramName, section);
        window.location.href = currentUrl.toString();
    }
</script>