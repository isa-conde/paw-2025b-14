<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="userProfile" type="ar.edu.itba.paw.model.User" %>
<%@attribute name="teamProfile" type="ar.edu.itba.paw.model.Team" %>
<%@attribute name="isUser" type="java.lang.Boolean" %>
<%@attribute name="isTeam" type="java.lang.Boolean" %>
<c:choose>
    <c:when test="${isUser}">
        <c:set var="url" value="/profile/${userProfile.id}"/>
    </c:when>
    <c:when test="${isTeam}">
        <c:set var="url" value="/team/profile/${teamProfile.id}"/>
    </c:when>
</c:choose>
<div class="profile-card">
    <div onclick="window.location.href='${url}'" >
        <c:choose>
            <c:when test="${isUser}">
                <img class="profile-avatar"  src="${pageContext.request.contextPath}/pfp/${userProfile.profile_picture_id}" alt="${userProfile.username}"/>
            </c:when>
            <c:otherwise>
                <img  class="profile-avatar" src="${pageContext.request.contextPath}/pfp/${teamProfile.pfp_id}" alt="${teamProfile.name}"/>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="profile-name">
        <c:choose>
            <c:when test="${isUser}">
                <c:out value="${userProfile.username}"/>
            </c:when>
            <c:otherwise>
                <c:out value="${teamProfile.name}"/>
            </c:otherwise>
        </c:choose>
    </div>
</div>