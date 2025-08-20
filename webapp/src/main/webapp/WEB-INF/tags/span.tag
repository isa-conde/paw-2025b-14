<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="fontFamily" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="textFont" value="${not empty fontFamily ? fontFamily : 'montserrat'}"/>
<c:set var="textType" value="${type}"/>
<c:set var="classes" value="${textFont}"/>

<h1 class="${classes}">
    <c:out value="${text}"/>
</h1>

<h2 class="${classes}">
    <c:out value="${text}"/>
</h2>

<h3 class="${classes}">
    <c:out value="${text}"/>
</h3>

<h4 class="${classes}">
    <c:out value="${text}"/>
</h4>

<p class="${classes}">
    <c:out value="${text}"/>
</p>

<span class="${classes}">
    <c:out value="${text}"/>
</span>