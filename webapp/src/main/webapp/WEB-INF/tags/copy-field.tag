<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="label" required="false" %>

<div class="copy-field-container">
    <c:if test="${not empty label}">
        <spring:message code="${label}" var="title"/>
        <paw:text type="title" size="s"><c:out value="${title}"/></paw:text>
    </c:if>
    <div class="copy-field-box" data-value="${fn:escapeXml(value)}">
        <span class="copy-field-text">${fn:escapeXml(value)}</span>

        <button type="button" class="copy-btn">
            <img src="${pageContext.request.contextPath}/images/copy.png"
                 alt="Copy" class="copy-icon" />
        </button>
    </div>
</div>