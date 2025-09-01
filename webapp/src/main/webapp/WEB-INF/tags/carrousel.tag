<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="false" type="java.util.List" %>
<%@ attribute name="noFormat" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="isGame" value="${not empty noFormat? noFormat : false}"/>

<div class="carrousel-container">
    <button class="carrousel-arrow carrousel-arrow-left" onclick="moveCarrousel('${id}', -1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Previous" class="arrow-icon arrow-left">
    </button>
    
    <div class="carrousel" id="${id}">
        <div class="carrousel-track">
            <c:forEach var="e" items="${elements}" varStatus="status">
                <div class="carrousel-item">
                    <paw:element-card 
                        image="data:image/png;base64,${e.img}"
                        title="${isGame? e.game.name : e.name}"
                        subtitle="${isGame? '' : e.format}"/>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <button class="carrousel-arrow carrousel-arrow-right" onclick="moveCarrousel('${id}', 1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Next" class="arrow-icon arrow-right">
    </button>
</div>

<script>
    window.carrouselState = window.carrouselState || {};

    if (!carrouselState['${id}']) {
        carrouselState['${id}'] = {
            currentIndex: 0,
            itemsPerView: 3,
            totalItems: ${elements.size()}
        };
    }

    function moveCarrousel(id, direction) {
        const state = carrouselState[id];
        const maxIndex = state.totalItems;

        state.currentIndex += (direction * state.itemsPerView);
        state.currentIndex = (maxIndex + state.currentIndex) % maxIndex;
        while(state.currentIndex % state.itemsPerView !== 0){
            state.currentIndex -= direction;
        }

        const carrousel = document.getElementById(id);
        const track = carrousel.querySelector('.carrousel-track');

        const itemWidth = 380;
        const gapWidth = 20;

        track.style.transition = 'transform 0.3s ease';
        const translateX = -(state.currentIndex * (itemWidth + gapWidth));
        track.style.transform = 'translateX(' + translateX + 'px)';
    }
</script>
