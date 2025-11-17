<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="name" required="false" %>
<%@ attribute name="path" required="false" %>
<%@ attribute name="value" required="false" type="java.lang.Integer" %>
<%@ attribute name="max" required="false" type="java.lang.Integer" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="idPrefix" required="false" %>
<%@ attribute name="size" required="false" %>


<c:set var="sizeClass" value="${size == 'sm' ? 'star-rating--sm' : ''}" />
<c:set var="maxStars" value="${empty max ? 5 : max}" />
<c:set var="ratingValue" value="${empty value ? 0 : value}" />
<c:set var="inputName" value="${not empty name ? name : path}" />
<c:if test="${empty inputName}">
    <c:set var="inputName" value="rating" />
</c:if>
<c:set var="rawIdBase" value="${empty idPrefix ? inputName : idPrefix}" />
<c:set var="sanitizedIdBase" value="${fn:replace(fn:replace(rawIdBase, ' ', '-') , '.', '-')}" />
<c:if test="${empty sanitizedIdBase}">
    <c:set var="sanitizedIdBase" value="star-rating" />
</c:if>
<c:set var="isDisabled" value="${disabled == true}" />
<c:set var="isRequired" value="${required == true}" />

<div class="star-rating ${sizeClass} ${isDisabled ? 'star-rating--disabled' : ''}" role="radiogroup">
    <c:forEach var="starValue" begin="1" end="${maxStars}" varStatus="loop">
        <c:set var="currentValue" value="${maxStars - loop.count + 1}" />
        <c:set var="inputId" value="${sanitizedIdBase}-${currentValue}" />
        <input
                type="radio"
                class="star-rating__input"
                id="${inputId}"
                name="${inputName}"
                value="${currentValue}"
                <c:if test="${ratingValue == currentValue}">checked</c:if>
                <c:if test="${isDisabled}">disabled</c:if>
                <c:if test="${isRequired and currentValue == 1}">required</c:if>
        />
        <label class="star-rating__label" for="${inputId}"></label>
    </c:forEach>
</div>