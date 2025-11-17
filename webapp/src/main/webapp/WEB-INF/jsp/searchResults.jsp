<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<spring:message code="searchPage.pageTitle" var="pageTitle"/>

<paw:layout user="${user}" pageTitle="${pageTitle}">
    <div class="content-container">
        <c:choose>
            <c:when test="${games.size() == 0  && tournaments.size() == 0 && users.size() == 0 && teams.size() == 0}">
                <div class="no-cards-container">
                    <paw:text size="l" weight="thin"><spring:message code="searchPage.noResults"/></paw:text>
                </div>
            </c:when>
            <c:otherwise>
                <paw:text type="title" size="xl" stroke="true"><spring:message code="searchPage.title"/></paw:text>
                <c:if test="${games.size() > 0}">
                    <div class="grid-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.games"/></paw:text>
                    </div>
                    <paw:elements-grid elements="${games}" id="games" isGame="${true}"/>
                    <paw:pagination currentPage="${page1}" totalPages="${page1}" url="/search" pageNumber="1"/>
                </c:if>
                <c:if test="${tournaments.size() > 0}">
                    <div class="grid-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.tournament"/></paw:text>
                    </div>
                    <paw:elements-grid elements="${tournaments}" id="tournaments"/>
                    <paw:pagination currentPage="${page2}" totalPages="${totalPages2}" url="/search" pageNumber="2"/>
                </c:if>
                <c:if test="${users.size() > 0}">
                    <div class="grid-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.users"/></paw:text>
                    </div>
                    <paw:elements-grid elements="${users}" id="users" isUser="${true}"/>
                    <paw:pagination currentPage="${page3}" totalPages="${totalPages3}" url="/search" pageNumber="3"/>
                </c:if>
                <c:if test="${teams.size() > 0}">
                    <div class="grid-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.teams"/></paw:text>
                    </div>
                    <paw:elements-grid elements="${teams}" id="teams" isTeam="${true}"/>
                    <paw:pagination currentPage="${page4}" totalPages="${totalPages4}" url="/search" pageNumber="4"/>
                </c:if>
            </c:otherwise>
        </c:choose>

    </div>
</paw:layout>