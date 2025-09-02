<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="participants" required="true" rtexprvalue="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="board">
    <div class="board-header">
        <div class="board-header-cell participant-cell"><paw:text size="xl">Participants</paw:text></div>
        <div class="board-header-cell"><paw:text size="xl">Points</paw:text></div>
    </div>
    
    <div class="board-body">
        <c:forEach var="participant" items="${participants}">
            <div class="board-row">
                <div class="board-cell participant-cell">
                    <paw:text weight="thin"><c:out value="${participant.username}"/></paw:text>
                </div>
                <div class="board-cell">
                    <paw:text weight="semi-bold"><c:out value="${participant.points}"/></paw:text>
                </div>
            </div>
        </c:forEach>
    </div>
</div>


