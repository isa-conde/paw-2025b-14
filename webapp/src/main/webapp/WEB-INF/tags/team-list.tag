<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="teams" required="true" type="java.util.List" %>
<%@ attribute name="formId" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="teams-list">
    <c:forEach var="t" items="${teams}">
        <input type="checkbox"
               class="edit-check"
               name="selected"
               value="${t.id}"
               form="${formId}"/>
        <paw:profileButton text="${t.name}" onclick="" isNotSafe="true" size="l" fill="false" disabled="true"/>
    </c:forEach>
</div>