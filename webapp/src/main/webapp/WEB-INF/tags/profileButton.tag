<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true"%>
<%@ attribute name="size" required="false" description="Button size: xs, s, [m], l"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="image" required="false" description="Image URL" %>
<%@ attribute name="imageId" required="true" %>
<%@ attribute name="fill" required="false" description="Fill: [true] or false" type="java.lang.Boolean"%>
<%@ attribute name="disabled" required="false" description="Whether button is disabled" type="java.lang.Boolean"%>
<%@ attribute name="isNotSafe" required="false" type="java.lang.Boolean" %>
<%@ attribute name="rating" required="false" type="java.lang.Float"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>


<c:url var="btnImg" value="/pfp/${imageId}"/>
<c:set var="btnSize" value="${not empty size ? size : 'm'}" />

<paw:button
        text="${text}"
        onclick="${onclick}"
        image="${btnImg}"
        disabled="${disabled}"
        size="${btnSize}"
        fill="${fill}"
        isNotSafe="${isNotSafe}"
        rating="${rating}"
/>
