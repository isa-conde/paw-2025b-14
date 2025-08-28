<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true"%>
<%@ attribute name="size" required="false" description="Button size: xs, s, [m], l"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="image" required="false" description="Image URL" %>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${not empty image}">
        <c:set var="btnImg" value="${image}" />
    </c:when>
    <c:otherwise>
        <c:url var="btnImg" value="/images/empty_user.png"/>
    </c:otherwise>
</c:choose>
<c:set var="btnSize" value="${not empty size ? size : 'm'}" />

<paw:button
        text="${text}"
        onclick="${onclick}"
        image="${btnImg}"
        disabled="${disabled}"
        size="${btnSize}"
/>
