<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="false" type="java.util.List" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="carrousel-container">
    <button class="carrousel-arrow carrousel-arrow-left" onclick="moveCarrousel('${id}', -1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Previous" class="arrow-icon arrow-left">
    </button>

    <div class="carrousel" id="${id}" data-items-per-view="3">
        <div class="carrousel-track">
            <c:forEach var="e" items="${elements}">
                <div class="carrousel-item">
                    <paw:element-card image="${pageContext.request.contextPath}/images/lol.jpg"
                                      title="Cusardo Tournament" subtitle="Cusi 2025"/>
                </div>
            </c:forEach>
        </div>
    </div>

    <button class="carrousel-arrow carrousel-arrow-right" onclick="moveCarrousel('${id}', 1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Next" class="arrow-icon arrow-right">
    </button>
</div>

<script src="${pageContext.request.contextPath}/js/carrousel.js"></script>