<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="fontFamily" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="textFont" value="${not empty fontFamily ? fontFamily : 'montserrat'}"/>
<c:set var="classes" value="${textFont}"/>

<span class="${classes}">
    <c:out value="${text}"/>
</span>