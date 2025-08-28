<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="text" required="false" %>
<%@ attribute name="butText" required="false"%>
<%@ attribute name="grain" required="false"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="cardText" value="${not empty text ? text : ''}"/>
<c:set var="buttonText" value="${not empty butText ? butText : ''}"/>
<c:set var="grain" value="${not empty butText && grain ? 'grain' : ''}"/>

<div class="card ${grain}">
    <div class="card-title">
        <paw:text type="h2"><c:out value="${title}"/></paw:text>
    </div>
    <c:if test="${not empty fn:trim(cardText)}">
        <div class="card-text">
            <paw:text size="lg" weight="2"><c:out value="${text}"/></paw:text>
        </div>
    </c:if>
    <c:if test="${not empty fn:trim(buttonText)}">
        <div class="card-button-container">
            <paw:button text="${butText}" onclick="${onclick}"/>
        </div>
    </c:if>
</div>