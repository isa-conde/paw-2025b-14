<%@ tag description="Icon Card Component" pageEncoding="UTF-8"%>
<%@ attribute name="icon" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="subtext" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="icon-card">
    <div class="card-icon">
        <img src="${icon}" alt="icon"/>
    </div>
    <div class="inline-text-container">
        <c:if test="${subtext != null}">
            <div class="subtext">
                <paw:text weight="thin"><c:out value="${subtext}"/></paw:text>
            </div>
        </c:if>
        <paw:text>
            <c:out value="${text}"/>
        </paw:text>
    </div>
</div>
