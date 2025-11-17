<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="participants" required="true" rtexprvalue="true" type="java.util.List" %>
<%@ attribute name="isEditing" required="false" rtexprvalue="true" %>
<%@ attribute name="formId" required="false" rtexprvalue="true" %>
<%@ attribute name="size" required="false" rtexprvalue="true" description="Board size: l or [xl]" %>
<%@ attribute name="groupNumber" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="headerSize" value="${not empty size? size : 'xl'}" />

<div class="board">
    <div class="board-header">
        <div class="board-header-cell ${headerSize} participant-cell"><paw:text size="${headerSize}"><spring:message code="tournament.participants.title"/></paw:text></div>
        <div class="board-header-cell ${headerSize}"><paw:text size="${headerSize}">+/-</paw:text></div>
        <div class="board-header-cell ${headerSize}"><paw:text size="${headerSize}"><spring:message code="tournament.participants.points"/></paw:text></div>
    </div>
    
    <div class="board-body">
        <c:forEach var="participant" items="${participants}">
            <c:if test="${groupNumber == null || groupNumber == participant.groupNumber}">
                <div class="board-row">
                    <div class="board-cell participant-cell">
                        <c:if test="${isEditing}">
                            <input type="checkbox"
                                   class="edit-check"
                                   name="selected"
                                   value="${participant.id}"
                                   form="${formId}"
                                   data-kind="groups"
                                   data-group="${participant.groupNumber}"
                                   data-user="${participant.id}"/>
                        </c:if>
                        <paw:text weight="thin"><c:out value="${participant.name}"/></paw:text>
                    </div>
                    <div class="board-cell">
                        <paw:text weight="thin"><c:out value="${participant.scoreDifference}"/></paw:text>
                    </div>
                    <div class="board-cell">
                        <paw:text weight="semi-bold"><c:out value="${participant.points}"/></paw:text>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>


