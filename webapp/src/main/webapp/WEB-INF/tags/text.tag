<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="type" required="false"%>
<%@ attribute name="size" required="false" rtexprvalue="true" %>
<%@ attribute name="weight" required="false" rtexprvalue="true" %>

<c:set var="fontSize" value="${size == 'xs' ? 'var(--text-14)' :
                               size == 's' ? 'var(--text-18)' :
                               size == 'md' ? 'var(--text-20)' :
                               size == 'l' ? 'var(--text-24)' :
                               size == 'xl' ? 'var(--text-32)' : 'var(--text-20)'}" />

<c:set var="fontWeight" value="${weight == '1' ? '400' :
                                weight == '2' ? '500' :
                                weight == '3' ? '600' :
                                weight == '4' ? '700' : '700'}" />

<c:set var="textType" value="${type == 'h1' ? 'h1' :
                                type == 'h2' ? 'h2' :
                                type == 'h3' ? 'h3' :
                                type == 'h4' ? 'h4' :
                                type == 'h5' ? 'h5' :
                                type == 'p' ? 'p' : 'p'}"/>

<c:choose>
    <c:when test="${textType == 'h1'}">
        <h1 class="title" style="font-size: var(--text-64);">
            <jsp:doBody/>
        </h1>
    </c:when>
    <c:when test="${textType == 'h2'}">
        <h2 class="title" style="font-size: var(--text-40);">
            <jsp:doBody/>
        </h2>
    </c:when>
    <c:when test="${textType == 'h3'}">
        <h3 class="title" style="font-size: var(--text-36);">
            <jsp:doBody/>
        </h3>
    </c:when>
    <c:when test="${textType == 'h4'}">
        <h4 class="title" style="font-size: var(--text-24);">
            <jsp:doBody/>
        </h4>
    </c:when>
    <c:when test="${textType == 'h5'}">
        <h5 class="title" style="font-size: var(--text-20);">
            <jsp:doBody/>
        </h5>
    </c:when>
    <c:when test="${textType == 'p'}">
        <p style="font-size: ${fontSize}; font-weight: ${fontWeight}; color: var(--primary-text); font-family: var(--font-main);">
            <jsp:doBody/>
        </p>
    </c:when>
</c:choose>