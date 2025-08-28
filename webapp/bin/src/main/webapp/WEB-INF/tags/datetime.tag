<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="fontFamily" required="false"%>
<%@ attribute name="size" required="false"%>
<%@ attribute name="year" required="true"%>
<%@ attribute name="month" required="true"%>
<%@ attribute name="day" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="textFont" value="${not empty fontFamily ? fontFamily : 'montserrat'}"/>
<c:set var="textSize" value="${not empty size ? size : 'md'}"/>
<c:set var="classes" value="${textFont} p-${textSize}"/>

<p class="${classes}">
    <c:out value="${day}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>
</p>