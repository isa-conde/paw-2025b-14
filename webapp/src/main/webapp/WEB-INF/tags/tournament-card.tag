<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="image" required="true" rtexprvalue="true" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="subtitle" required="false" rtexprvalue="true" %>

<div class="tournament-card">
    <div class="tournament-card-bg">
        <img src="${image}" alt="Tournament background" class="tournament-bg-image">
    </div>
    
    <div class="tournament-card-content">
        <div class="tournament-info">
            <paw:h5>${title}</paw:h5>
            <c:if test="${not empty subtitle}">
                <paw:p size="xs" weight="1">${subtitle}</paw:p>
            </c:if>
        </div>
        
        <div class="tournament-bottom">
            <jsp:doBody/>
        </div>
    </div>
</div>
