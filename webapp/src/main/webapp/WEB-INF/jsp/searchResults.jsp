<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<paw:layout user="${user}">
    <div class="content-container">
        <c:choose>
            <c:when test="${games.size() == 0  && tournaments.size() == 0}">
                <div class="no-cards-container">
                    <paw:text size="l" weight="thin"><spring:message code="searchPage.noResults"/></paw:text>
                </div>
            </c:when>
            <c:otherwise>
                <paw:text type="title" size="xl" stroke="true"><spring:message code="searchPage.title"/></paw:text>
                <c:if test="${tournaments.size() > 0}">
                    <div class="carrousel-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.tournament"/></paw:text>
                    </div>
                    <paw:carrousel id="tournaments" elements="${tournaments}"/>
                </c:if>
                <c:if test="${games.size() > 0}">
                    <div class="carrousel-title">
                        <paw:text type="title" size="s"><spring:message code="searchPage.games"/></paw:text>
                    </div>
                    <paw:carrousel id="games-list" elements="${games}" isGame="true"/>
                </c:if>
            </c:otherwise>
        </c:choose>

    </div>
</paw:layout>