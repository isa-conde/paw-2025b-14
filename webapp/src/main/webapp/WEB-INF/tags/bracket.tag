<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="matchesByStage" required="true" type="java.util.Map" %>
<%@ attribute name="tournamentId" required="true" rtexprvalue="true" %>
<%@ attribute name="isCreator" required="false" rtexprvalue="true" %>
<%@ attribute name="isEditing" required="false" rtexprvalue="true" %>
<%@ attribute name="formId" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<div class="bracket-container">
    <div class="bracket">
        <c:forEach var="stageEntry" items="${matchesByStage}">
            <c:set var="stageMatches" value="${stageEntry.value}"/>
            <div class="bracket-round">
                <div class="bracket-matches">
                    <c:forEach var="match" items="${stageMatches}" varStatus="loop">
                        <div class="bracket-match round-${stageEntry.key} match-${loop.index}">
                            <div class="bracket-team ${match.winner == 1 ? 'winner' : ''}">
                                <c:set var="localName" value="${match.local == null ? 'TBD' : match.local.name}"/>
                                <span><c:out value="${localName}"/></span>
                                <c:if test="${isEditing and match.localId != null}">
                                    <input type="checkbox"
                                           class="edit-check"
                                           name="selected"
                                           value="${match.id}:${match.localId}"
                                           form="${formId}"
                                           data-kind="bracket"
                                           data-match="${match.id}"
                                           data-user="${match.localId}"/>
                                </c:if>
                            </div>
                            <div class="bracket-team ${match.winner == 2 ? 'winner' : ''}">
                                <c:set var="visitorName" value="${match.visitor == null ? 'TBD' : match.visitor.name}"/>
                                <span><c:out value="${visitorName}"/></span>
                                <c:if test="${isEditing and match.visitorId != null}">
                                    <input type="checkbox"
                                           class="edit-check"
                                           name="selected"
                                           value="${match.id}:${match.visitorId}"
                                           form="${formId}"
                                           data-kind="bracket"
                                           data-match="${match.id}"
                                           data-user="${match.visitorId}"/>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
