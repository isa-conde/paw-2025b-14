<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true"%>
<%@ attribute name="size" required="false" description="Button size: xs, s, m, [l]"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="image" required="false" description="Image URL" %>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="hasImage" value="${not empty image ? 'true' : 'false'}" />
<c:set var="btnSize" value="${not empty size? size : 'l'}" />
<c:set var="isDisabled" value="${disabled == 'true'}" />
<c:set var="btnClass" value="${hasImage? 'btn image' : 'btn'}" />

<button class="${btnClass}"
        onclick="${onclick}"
        <c:if test="${isDisabled}">disabled</c:if>>
    <c:if test="${hasImage}">
        <img class="button-image ${btnSize}" src="${image}" alt="Profile Picture">
    </c:if>
    <paw:text size="${btnSize}"><c:out value="${text}"/></paw:text>
</button>