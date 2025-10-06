<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="participants" required="true" type="java.util.List" %>
<%@ attribute name="isIndividualTournament" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="grid users">
    <c:forEach var="p" items="${participants}">
        <c:choose>
            <c:when test="${isIndividualTournament}">
                <c:set value="/profile/${p.id}" var="url"/>
            </c:when>
            <c:otherwise>
                <c:set value="/team/profile/${p.id}" var="url"/>
            </c:otherwise>
        </c:choose>
        <paw:profileButton text="${p.name}" onclick="window.location.href='${url}'" isNotSafe="true" size="l" fill="false"/>
    </c:forEach>
</div>

