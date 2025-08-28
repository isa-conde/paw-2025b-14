<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="size" required="true" description="Banner height: md or lg" %>
<%@ attribute name="image" required="true" description="Banner image URL" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="height" value="${size == 'lg' ? 600 : 330}" />

<div class="banner" style="height: ${height}px;">
    <img src="${image}" alt="Banner" class="banner-image">
    <div class="banner-content" style="margin-bottom: ${height / 10}px">
        <jsp:doBody/>
    </div>
</div>
