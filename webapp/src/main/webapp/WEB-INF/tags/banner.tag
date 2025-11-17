<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="size" required="false" description="Banner height: s, [m], l or profile (specific for profile page)" %>
<%@ attribute name="image" required="true" description="Banner image URL" %>
<%@ attribute name="cornerIcon" required="false" description="Botton-right corner icon"%>
<%@ attribute name="cornerText" required="false" description="Botton-right corner text"%>
<%@ attribute name="cornerOnClick" required="false" description="Botton-right corner action"%>
<%@ attribute name="cropTop" required="false" rtexprvalue="true" description="Crop from top: true or false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="height" value="${not empty size ? size : 'm'}" />
<c:set var="imageClass" value="banner-image ${cropTop == 'true' ? 'top-cropped' : ''}" />
<c:set var="hasCorner" value="${not empty cornerIcon || not empty cornerText ? 'true' : 'false'}"/>

<div class="banner ${height}">
    <img class="${imageClass}" src="${image}" alt="Banner">
    <div class="banner-content">
        <jsp:doBody/>
    </div>
    <c:if test="${hasCorner}">
        <div class="banner-corner">
            <paw:button onclick="${cornerOnClick}" text="${cornerText}" image="${cornerIcon}" size="m" secondary="true"/>
        </div>
    </c:if>
</div>
