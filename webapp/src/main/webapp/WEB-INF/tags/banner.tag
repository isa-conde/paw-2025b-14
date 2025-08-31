<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="size" required="false" description="Banner height: [m] or l" %>
<%@ attribute name="image" required="true" description="Banner image URL" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="height" value="${not empty size ? size : 'm'}" />

<div class="banner ${height}">
    <img class="banner-image" src="${image}" alt="Banner">
    <div class="banner-content">
        <jsp:doBody/>
    </div>
</div>
