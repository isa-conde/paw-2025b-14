<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="participants" required="true" type="java.util.List" %>
<%@ attribute name="isIndividualTournament" required="true" type="java.lang.Boolean" %>
<%@ attribute name="deleteMode" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="hasBin" value="${not empty deleteMode ? deleteMode : 'false'}" />

<div class="grid users">
    <c:url value="/images/bin.png" var="binUrl"/>
    <c:forEach var="p" items="${participants}">
        <c:choose>
            <c:when test="${isIndividualTournament}">
                <c:url value="/profile/${p.user.id}" var="url"/>
            </c:when>
            <c:otherwise>
                <c:url value="/team/profile/${p.team.id}" var="url"/>
            </c:otherwise>
        </c:choose>
        <div class="users-grid-item">
            <paw:profileButton text="${p.name}" imageId="${p.pfpId}" onclick="window.location.href='${url}'" isNotSafe="true" size="l" fill="false"/>
            <c:if test="${hasBin}">
                <img
                        src="${binUrl}"
                        alt="bin"
                        class="bin-icon"
                        data-id="${p.id}"
                        data-name="${p.name}"
                        onclick="openRemoveParticipantModal(this.dataset.id, this.dataset.name)"
                />
            </c:if>
        </div>
    </c:forEach>
</div>

