<%@ tag description="Icon Card Component" pageEncoding="UTF-8"%>
<%@ attribute name="icon" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div class="icon-card">
    <div class="card-icon">
        <img src="${icon}" alt="icon"/>
    </div>
    <div class="card-text">
        <c:out value="${text}"/>
    </div>
</div>
