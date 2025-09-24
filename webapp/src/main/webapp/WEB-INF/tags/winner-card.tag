<%@ tag language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="winnerName" required="true" %>
<%@ attribute name="method" required="false"%>

<div class="card texture">
    <div class="card-content-container">
        <div class="card-title">
            <spring:message code="tournament.winner" var="winner" arguments="${winnerName}"/>
            <paw:text type="title" size="l"><c:out value="${winner}"/></paw:text>
            <img src="${pageContext.request.contextPath}/images/medal.png" alt="winner"/>
        </div>
    </div>
</div>