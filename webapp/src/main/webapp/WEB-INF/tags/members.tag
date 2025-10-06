<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="members" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>



<div class="grid users">
    <c:forEach var="p" items="${members}">
        <c:set value="/profile/${p.id}" var="url"/>
        <paw:profileButton text="${p.username}" onclick="window.location.href='${url}'" isNotSafe="true" size="l" fill="false"/>
    </c:forEach>
</div>
