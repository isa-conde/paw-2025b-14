<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="type" required="false" description="Button type: main or profile" %>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="profileImage" required="false" description="Profile image URL for profile button" %>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="btnType" value="${type == 'main' || type == null ? 'main' : 'profile'}" />
<c:set var="isDisabled" value="${disabled == 'true'}" />
<c:set var="hasProfileImage" value="${not empty profileImage ? 'true' : 'false'}" />
<c:set var="profileIconClass" value="${empty profileImage ? 'default' : ''}" />
<c:set var="classes" value="btn btn-${btnType}" />

<button class="${classes}"
        onclick="${onclick}"
        <c:if test="${isDisabled}">disabled</c:if>>
        <c:if test="${type == 'profile'}">
            <div class="profile-icon ${profileIconClass}">
                <c:if test="${hasProfileImage}">
                    <img src="${profileImage}" alt="Profile Picture">
                </c:if>
            </div>
        </c:if>
    <jsp:doBody/>
</button>