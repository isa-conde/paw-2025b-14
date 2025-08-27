<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="type" required="false" description="Button type: main or profile" %>
<%@ attribute name="text" required="true" description="Button text" %>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="profileImage" required="false" description="Profile image URL for profile button" %>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="isMainButton" value="${type == 'main' || type == null}" />
<c:set var="isProfileButton" value="${type == 'profile'}" />
<c:set var="isDisabled" value="${disabled == 'true'}" />
<c:set var="hasProfileImage" value="${not empty profileImage}" />
<c:set var="profileIconClass" value="${hasProfileImage ? '' : 'default'}" />

<c:if test="${isMainButton}">
    <button class="btn btn-main" 
            onclick="${onclick}"
            <c:if test="${isDisabled}">disabled</c:if>>
        ${text}
    </button>
</c:if>

<c:if test="${isProfileButton}">
    <button class="btn btn-profile" 
            onclick="${onclick}"
            <c:if test="${isDisabled}">disabled</c:if>>
        <div class="profile-icon ${profileIconClass}">
            <c:if test="${hasProfileImage}">
                <img src="${profileImage}" alt="Profile Picture">
            </c:if>
        </div>
        ${text}
    </button>
</c:if>