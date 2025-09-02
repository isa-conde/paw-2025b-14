<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="size" required="false" description="Banner height: s, [m] or l" %>
<%@ attribute name="image" required="true" description="Banner image URL" %>
<%@ attribute name="cropTop" required="false" rtexprvalue="true" description="Crop from top: true or false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="height" value="${not empty size ? size : 'm'}" />
<c:set var="imageClass" value="banner-image ${cropTop == 'true' ? 'top-cropped' : ''}" />

<div class="banner ${height}">
    <img class="${imageClass}" src="${image}" alt="Banner">
    <div class="banner-content">
        <jsp:doBody/>
    </div>
</div>
