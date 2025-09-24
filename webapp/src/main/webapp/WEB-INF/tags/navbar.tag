<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="sections" required="true" type="java.util.List" description="List of section names" %>
<%@ attribute name="activeSection" required="true" rtexprvalue="true" description="Currently active section" %>
<%@ attribute name="onSectionClick" required="false" rtexprvalue="true" description="JavaScript function to call when section is clicked" %>
<%@ attribute name="labels" required="false" type="java.util.List" description="Display labels" %>

<nav class="navbar">
    <c:forEach var="section" items="${sections}" varStatus="status">
        <c:set var="label" value="${labels[status.index]}"/>
        <c:set var="isActive" value="${section == activeSection}"/>

        <div class="navbar-section ${isActive ? 'active' : ''}"
             onclick="${not empty onSectionClick ? onSectionClick : 'switchNavbarSection'}('${section}')">
            <paw:text>${label}</paw:text>
        </div>

        <div class="navbar-separator"></div>
    </c:forEach>
</nav>

<script>
function switchNavbarSection(section) {
    const currentUrl = new URL(window.location);
    currentUrl.searchParams.set('section', section);
    window.location.href = currentUrl.toString();
}
</script>