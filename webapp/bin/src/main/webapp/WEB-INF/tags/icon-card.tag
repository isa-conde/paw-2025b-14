<%@ tag description="Icon Card Component" pageEncoding="UTF-8"%>
<%@ attribute name="icon" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="icon-card">
    <div class="card-icon">
        <img src="${icon}" alt="icon"/>
    </div>
    <paw:text>
        <c:out value="${text}"/>
    </paw:text>
</div>
