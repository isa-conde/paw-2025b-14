<%@ tag language ="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="text" required="false" %>
<%@ attribute name="butText" required="false"%>
<%@ attribute name="texture" required="false"%>
<%@ attribute name="icon" required="false"%>
<%@ attribute name="onclick" required="true" description="Onclick event handler" %>
<%@ attribute name="method" required="false"%>
<%@ attribute name="tournamentId" required="false" %>

<c:set var="cardText" value="${not empty text ? text : ''}"/>
<c:set var="buttonText" value="${not empty butText ? butText : ''}"/>
<c:set var="texture" value="${not empty butText && texture ? 'texture' : ''}"/>
<c:set var="hasIcon" value="${not empty icon? 'true' : 'false'}"/>

<div class="card ${texture}">
    <div class="card-content-container">
        <div>
            <div class="card-title">
                <paw:text type="title" size="l"><c:out value="${title}"/></paw:text>
            </div>
            <c:if test="${not empty fn:trim(cardText)}">
                <div class="card-text">
                    <paw:text size="l" weight="thin"><c:out value="${text}"/></paw:text>
                </div>
            </c:if>
            <c:if test="${not empty fn:trim(buttonText)}">
                <div class="card-button-container">
                    <c:choose>
                        <c:when test="${method == 'post'}">
                            <form:form method="post" action="${onclick}">
                                <c:if test="${not empty tournamentId}">
                                    <input type="hidden" name="tournamentId" value="${tournamentId}"/>
                                </c:if>
                                <paw:input path="" inputType="submit" label="${butText}"/>
                            </form:form>
                        </c:when>
                        <c:otherwise>
                            <paw:button text="${butText}" onclick="${onclick}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
        <c:if test="${hasIcon}">
            <div class="card-icon">
                <img src="${icon}" alt="format icon"/>
            </div>
        </c:if>
    </div>
</div>