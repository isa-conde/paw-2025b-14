<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="participants" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
    <c:when test="${participants.size() <= '0'}">
        <spring:message code="usersGrid.noParticipants" var="noParticipants"/>
        <div class="no-cards-container participants"><paw:text size="l">${noParticipants}</paw:text></div>
    </c:when>
    <c:otherwise>
        <div class="grid users">
            <c:forEach var="p" items="${participants}">
                <paw:profileButton text="${p.username}" onclick="window.location.href='/profile/${p.user_id}'" isNotSafe="true" size="l" fill="false"/>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
