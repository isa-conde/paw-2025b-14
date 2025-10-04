<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="true" type="java.util.List" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="isGame" value="${not empty isGame? isGame : false}"/>

<div class="carrousel-container">
    <button class="carrousel-arrow carrousel-arrow-left" onclick="moveCarrousel('${id}', -1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Previous" class="arrow-icon arrow-left">
    </button>
    
    <div class="carrousel" id="${id}">
        <div class="carrousel-track">
            <c:forEach var="e" items="${elements}" varStatus="status">
                <div class="carrousel-item">
                    <paw:element-card 
                        image="${pageContext.request.contextPath}/image/${e.image_id}"
                        title="${isGame? e.name : e.name}"
                        start_date="${isGame? '' : e.start_date}"
                        end_date="${isGame? '' : e.end_date}"
                        id="${isGame? e.id : e.id}"
                        isGame="${isGame}"/>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <button class="carrousel-arrow carrousel-arrow-right" onclick="moveCarrousel('${id}', 1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Next" class="arrow-icon arrow-right">
    </button>
</div>

<script src="${pageContext.request.contextPath}/js/carrousel.js"></script>
