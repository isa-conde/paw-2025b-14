<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<paw:layout user="${user}" pageTitle="${profile.username} Tournaments">
<paw:banner image="${pageContext.request.contextPath}/banner/${profile.banner_id}">
    <div class="profile-sidebar">
        <div class="profile-picture">
            <img src="${pageContext.request.contextPath}/pfp/${profile.pfp_id}" alt="${profile.username}">
        </div>
        <div class="profile-info">
            <paw:text size="xl"><c:out value="${profile.username}"/></paw:text>
            <paw:text size="m"><c:out value="${profile.bio}"/></paw:text>
        </div>
    </div>
</paw:banner>
    <spring:message code="tournament.navbar.active" var="activeLabel"/>
    <spring:message code="tournament.navbar.finished" var="finishedLabel"/>
    <spring:message code="tournament.navbar.owned" var="ownedLabel"/>

    <c:set var="navbarSections" value="${['active','finished','owned']}"/>
    <c:set var="navbarLabels" value="${[activeLabel, finishedLabel, ownedLabel]}"/>
    <c:set var="activeSection" value="${param.section != null ? param.section : 'active'}"/>

    <paw:navbar sections="${navbarSections}" labels="${navbarLabels}" activeSection="${activeSection}"/>

    <div class="content-container">
        <c:choose>
            <c:when test="${activeSection == 'active'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${joinedTournaments}" id="on-going-${profile.id}"/>
                <div class="pagination-container">
                    <c:if test="${totalPages1 > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage1 > 0}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 - 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin"><</paw:text>
                                </paw:paginationLink>
                            </c:if>

                            <c:forEach begin="0" end="${totalPages1 - 1}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage1}">
                                        <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                                    </c:when>
                                    <c:otherwise>
                                        <paw:paginationLink pageNumber="1" page="${i}" url="/profile/${profile.id}/tournaments">
                                            <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                        </paw:paginationLink>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${currentPage1 < totalPages1 - 1}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 + 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin">></paw:text>
                                </paw:paginationLink>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:when>
            <c:when test="${activeSection == 'finished'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${pastTournaments}" id="finished-${profile.id}"/>
                <div class="pagination-container">
                    <c:if test="${totalPages1 > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage1 > 0}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 - 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin"><</paw:text>
                                </paw:paginationLink>
                            </c:if>

                            <c:forEach begin="0" end="${totalPages1 - 1}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage1}">
                                        <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                                    </c:when>
                                    <c:otherwise>
                                        <paw:paginationLink pageNumber="1" page="${i}" url="/profile/${profile.id}/tournaments">
                                            <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                        </paw:paginationLink>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${currentPage1 < totalPages1 - 1}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 + 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin">></paw:text>
                                </paw:paginationLink>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:when>
            <c:when test="${activeSection == 'owned'}">
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.active"/></paw:text>
                </div>
                <paw:elements-grid elements="${onGoingTournaments}" id="on-going-${profile.id}-creations"/>
                <div class="pagination-container">
                    <c:if test="${totalPages1 > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage1 > 0}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 - 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin"><</paw:text>
                                </paw:paginationLink>
                            </c:if>

                            <c:forEach begin="0" end="${totalPages1 - 1}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage1}">
                                        <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                                    </c:when>
                                    <c:otherwise>
                                        <paw:paginationLink pageNumber="1" page="${i}" url="/profile/${profile.id}/tournaments">
                                            <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                        </paw:paginationLink>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${currentPage1 < totalPages1 - 1}">
                                <paw:paginationLink pageNumber="1" page="${currentPage1 + 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin">></paw:text>
                                </paw:paginationLink>
                            </c:if>
                        </div>
                    </c:if>
                </div>
                <div class="grid-title">
                    <paw:text type="title"><spring:message code="tournaments.owned.finished"/></paw:text>
                </div>
                <paw:elements-grid elements="${finishedTournaments}" id="finished-${profile.id}-creations"/>
                <div class="pagination-container">
                    <c:if test="${totalPages2 > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage2 > 0}">
                                <paw:paginationLink pageNumber="2" page="${currentPage2 - 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin"><</paw:text>
                                </paw:paginationLink>
                            </c:if>

                            <c:forEach begin="0" end="${totalPages2 - 1}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage2}">
                                        <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                                    </c:when>
                                    <c:otherwise>
                                        <paw:paginationLink pageNumber="2" page="${i}" url="/profile/${profile.id}/tournaments">
                                            <paw:text weight="thin" size="l">${i + 1}</paw:text>
                                        </paw:paginationLink>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${currentPage2 < totalPages2 - 1}">
                                <paw:paginationLink pageNumber="2" page="${currentPage2 + 1}" url="/profile/${profile.id}/tournaments">
                                    <paw:text size="l" weight="thin">></paw:text>
                                </paw:paginationLink>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:when>
        </c:choose>
    </div>
</paw:layout>