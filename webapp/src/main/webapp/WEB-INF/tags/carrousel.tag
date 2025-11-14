<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="elements" required="true" type="java.util.List" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="isUserProfile" type="java.lang.Boolean" required="false" %>
<%@ attribute name="isTeamProfile" type="java.lang.Boolean" required="false" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>

<c:set var="isGame" value="${not empty isGame? isGame : false}"/>
<c:set var="isUserProfile" value="${not empty isUserProfile? isUserProfile : false}"/>
<c:set var="isTeamProfile" value="${not empty isTeamProfile? isTeamProfile : false}"/>


<div class="carrousel-container">
    <button class="carrousel-arrow carrousel-arrow-left" onclick="moveCarrousel('${id}', -1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Previous" class="arrow-icon arrow-left">
    </button>

    <div class="carrousel" id="${id}">
        <div class="carrousel-track">
            <c:forEach var="e" items="${elements}" varStatus="status">
                <c:choose>
                        <c:when test="${isUserProfile eq true}">
                            <paw:profile-card userProfile="${e}" isUser="true"/>
                        </c:when>
                        <c:when test="${isTeamProfile eq true}">
                            <paw:profile-card teamProfile="${e}" isTeam="true"/>
                        </c:when>
                        <c:otherwise>
                            <div class="carrousel-item">
                            <paw:element-card
                                    image="${pageContext.request.contextPath}/image/${e.imageId}"
                                    title="${isGame? e.name : e.name}"
                                    started="${e.tournamentStarted}"
                                    finished="${e.isFinished}"
                                    id="${isGame? e.id : e.id}"
                                    isGame="${isGame}"/>
                            </div>
                        </c:otherwise>
                    </c:choose>
            </c:forEach>
        </div>
    </div>
    
    <button class="carrousel-arrow carrousel-arrow-right" onclick="moveCarrousel('${id}', 1)">
        <img src="${pageContext.request.contextPath}/images/arrow.png" alt="Next" class="arrow-icon arrow-right">
    </button>
</div>

<script src="${pageContext.request.contextPath}/js/carrousel.js"></script>
