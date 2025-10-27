<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="teams" required="true" type="java.util.List" %>
<%@ attribute name="formId" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="teams-list">
    <c:forEach var="t" items="${teams}">
        <label class="team-option">
            <form:radiobutton path="teamId" value="${t.id}"/>
            <paw:profileButton text="${t.name}" imageId="${t.pfp_id}" onclick="" isNotSafe="true" size="l" fill="false" disabled="true"/>
        </label>
    </c:forEach>
</div>