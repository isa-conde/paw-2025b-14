<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="false"%>
<%@ attribute name="size" required="false" description="Button size: xs, s, m, [l]"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="image" required="false" description="Image URL" %>
<%@ attribute name="fill" required="false" description="Fill: [true] or false" type="java.lang.Boolean"%>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ attribute name="isNotSafe" required="false" type="java.lang.Boolean" %>
<%@ attribute name="secondary" required="false"%>
<%@ attribute name="rating" required="false" type="java.lang.Float"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="hasImage" value="${not empty image ? 'true' : 'false'}" />
<c:set var="hasText" value="${not empty text ? 'true' : 'false'}" />
<c:set var="btnSize" value="${not empty size? size : 'l'}" />
<c:set var="isDisabled" value="${disabled == 'true'}" />
<c:set var="imgClass" value="${(hasImage ? 'image' : '')}${!hasText ? ' no-text' : ''}" />
<c:set var="emptyClass" value="${fill == 'false'? 'empty' : ''}" />
<c:set var="secondaryClass" value="${secondary == 'true'? 'secondary' : ''}"/>

<c:url var="starUrl" value="/images/roundedStarOn.png"/>

<button class="btn ${imgClass} ${emptyClass} ${secondaryClass}"
        onclick="${onclick}"
        <c:if test="${isDisabled}">disabled</c:if>>
    <c:if test="${hasImage}">
        <img class="button-image ${btnSize}" src="${image}" alt="Button image">
    </c:if>
    <c:if test="${hasText}">
        <paw:text size="${btnSize}">
            <c:choose>
                <c:when test="${isNotSafe}">
                    <c:out value="${text}"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="${text}"/>
                </c:otherwise>
            </c:choose>
        </paw:text>
    </c:if>
    <c:if test="${rating != null && rating != 0}">
        <div class="rating-container">
            <img src="${starUrl}" class="banner-star" alt="tournament.rating.imgLabel"/>
            <paw:text weight="thin" size="s"> ${rating} </paw:text>
        </div>
    </c:if>
</button>