<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="text" required="false" %>
<%@ attribute name="butText" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="cardText" value="${not empty text ? text : ''}"/>
<c:set var="buttonText" value="${not empty butText ? butText : ''}"/>

<div class="content-card">
    <div class="card-title">
        <paw:h2><c:out value="${title}"/></paw:h2>
    </div>
    <c:if test="${not empty fn:trim(cardText)}">
        <p class="card-text p-md">
            <c:out value="${text}"/>
        </p>
    </c:if>
    <c:if test="${not empty fn:trim(buttonText)}">
        <div class="card-button-container">
            <paw:button type="main" onclick="goToIndex()">${butText}</paw:button>
        </div>
    </c:if>
</div>