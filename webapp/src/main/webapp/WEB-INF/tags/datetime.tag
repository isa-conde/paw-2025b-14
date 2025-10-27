<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="date" required="true" rtexprvalue="true" type="java.time.LocalDate"%>
<%@ attribute name="size" required="false" rtexprvalue="true" description="Font size: xs, s, [m], l, xl"%>
<%@ attribute name="weight" required="false" rtexprvalue="true" description="Font weight: thin, semi-bold, [bold]"%>
<%@ attribute name="stroke" required="false" rtexprvalue="true" description="Add text stroke: true or false"%>

<c:set var="fontSize" value="${not empty size? size : 'm'}"/>
<c:set var="fontWeight" value="${not empty weight? weight : 'bold'}"/>
<c:set var="hasStroke" value="${not empty stroke ? stroke : 'false'}"/>

<c:set var="daySuffix" value=""/>
<c:choose>
    <c:when test="${date.dayOfMonth % 10 == 1}">
        <c:set var="daySuffix" value="st"/>
    </c:when>
    <c:when test="${date.dayOfMonth % 10 == 2}">
        <c:set var="daySuffix" value="nd"/>
    </c:when>
    <c:when test="${date.dayOfMonth % 10 == 3}">
        <c:set var="daySuffix" value="rd"/>
    </c:when>
    <c:otherwise>
        <c:set var="daySuffix" value="th"/>
    </c:otherwise>
</c:choose>

<c:set var="monthNames" value="Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec"/>
<c:set var="monthIndex" value="${date.monthValue - 1}"/>
<c:set var="monthName" value="${monthNames.split(',')[monthIndex]}"/>

<paw:text size="${fontSize}" weight="${fontWeight}" stroke="${hasStroke}">
    <spring:message code="month.${date.monthValue}" var="monthName"/>
    <spring:message code="date.format" arguments="${date.dayOfMonth},${monthName},${date.year}"/>
</paw:text>