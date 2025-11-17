<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchId" required="true" rtexprvalue="true" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="localPlayer" required="true" rtexprvalue="true" type="ar.edu.itba.paw.model.Participant" %>
<%@ attribute name="visitorPlayer" required="true" rtexprvalue="true" type="ar.edu.itba.paw.model.Participant" %>
<%@ attribute name="localScore" required="true" rtexprvalue="true" %>
<%@ attribute name="visitorScore" required="true" rtexprvalue="true" %>
<%@ attribute name="winner" required="false" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:set var="localName" value="${localPlayer == null ? 'TBD' : localPlayer.name}"/>
<c:set var="visitorName" value="${visitorPlayer == null ? 'TBD' : visitorPlayer.name}"/>
<c:set var="localPfpDir" value="${localPlayer == null ? '/images/empty_user.png' : 'pfp/${localPlayer.pfpId}'}"/>
<c:set var="visitorPfpDir" value="${visitorPlayer == null ? '/images/empty_user.png' : 'pfp/${visitorPlayer.pfpId}'}"/>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="localPfp" value="${localPlayer == null ? contextPath.concat('/images/empty_user.png') : contextPath.concat('/pfp/').concat(localPlayer.pfpId)}"/>
<c:set var="visitorPfp" value="${visitorPlayer == null ? contextPath.concat('/images/empty_user.png') : contextPath.concat('/pfp/').concat(visitorPlayer.pfpId)}"/>

<div class="match-card">
    <div class="player local-player ${winner == 1 ? 'winner' : ''}">
        <img src="${localPfp}" alt="Local Player" class="player-avatar"/>
        <div class="player-name">
            <paw:text size="s" weight="semi-bold"><c:out value="${localName}"/></paw:text>
        </div>
    </div>

    <div class="score score-local">
        <paw:text size="m" weight="semi-bold"><c:out value="${empty localScore ? '-' : localScore}"/></paw:text>
    </div>

    <div class="score score-visitor">
        <paw:text size="m" weight="semi-bold"><c:out value="${empty visitorScore ? '-' : visitorScore}"/></paw:text>
    </div>

    <div class="player visitor-player ${winner == 2 ? 'winner' : ''}">
        <div class="player-name">
            <paw:text size="s" weight="semi-bold"><c:out value="${visitorName}"/></paw:text>
        </div>
        <img src="${visitorPfp}" alt="Visitor Player" class="player-avatar"/>
    </div>
</div>
