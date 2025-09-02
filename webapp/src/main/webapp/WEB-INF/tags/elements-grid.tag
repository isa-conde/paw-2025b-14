<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="elements" required="true" type="java.util.List" %>
<%@ attribute name="headerElements" required="false" type="java.util.List" %>
<%@ attribute name="isGame" required="false" rtexprvalue="true" description="game or [tournament] carrousel" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="tournamentGame" value=""/>
<c:set var="hasHeader" value="${not empty headerElements}"/>


<div class="grid">
    <c:forEach var="e" items="${elements}">
        <c:choose>
            <c:when test="${isGame}">
                    <paw:element-card
                            image="data:image/png;base64,${e.base64Img}"
                            title="${e.game.name}"
                            id="${e.game.id}"
                            isGame="true"/>
            </c:when>
            <c:otherwise>
                    <c:if test="${hasHeader}">
                        <c:forEach var="game" items="${headerElements}">
                            <c:if test="${game.id == e.tournament.game_id}">
                                <c:set var="tournamentGame" value="${game}"/>
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <paw:element-card
                            image="data:image/png;base64,${e.base64Img}"
                            title="${e.tournament.name}"
                            start_date="${e.tournament.start_date}"
                            end_date="${e.tournament.end_date}"
                            game="${hasHeader? tournamentGame.name : ''}"
                            id="${e.tournament.id}"
                            isGame="false"/>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>