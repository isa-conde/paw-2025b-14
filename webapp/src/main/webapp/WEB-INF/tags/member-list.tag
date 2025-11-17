<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="members" required="true" type="java.util.List" %>
<%@ attribute name="requiredSize" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="exactRequired"
       value="${requiredSize ne null and fn:length(members) == requiredSize}" />

<div class="teams-list">
    <c:choose>
        <c:when test="${exactRequired}">
            <c:forEach var="m" items="${members}">
                <input type="hidden" name="members" value="${m.id}"/>
                <label class="team-option">
                    <paw:profileButton imageId="${m.pfpId}" text="${m.username}" isNotSafe="true" size="l" fill="false" disabled="true" onclick=""/>
                </label>
            </c:forEach>
        </c:when>

        <c:otherwise>
            <c:forEach var="m" items="${members}">
                <label class="team-option">
                    <form:checkbox path="members" value="${m.id}"/>
                    <paw:profileButton imageId="${m.pfpId}" text="${m.username}" isNotSafe="true" size="l" fill="false" disabled="true" onclick=""/>
                </label>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
