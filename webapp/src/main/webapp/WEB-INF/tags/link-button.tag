<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="href" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="target" required="false" %>

<c:set var="sizeClass" value="${empty size ? 'm' : size}" />
<c:set var="targetAttr" value="${empty target ? '_self' : target}" />

<a href="${href}"
   class="link-btn"
   target="${targetAttr}">
    <c:out value="${text}"/>
</a>

