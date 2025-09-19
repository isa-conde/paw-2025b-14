<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<paw:layout user="${user}" isIndex="false">
    <c:choose>
        <c:when test="${user == null}">
            <paw:no-access/>
        </c:when>
        <c:otherwise>
            <div class="content-container">
                <paw:text type="title" size="xl" stroke="true"><spring:message code="searchPage.title"/></paw:text>
                <div class="carrousel-title">
                    <paw:text type="title" size="s"><spring:message code="searchPage.tournament"/></paw:text>
                </div>
                <paw:carrousel id="tournaments" elements="${tournaments}"/>

                <div class="carrousel-title">
                    <paw:text type="title" size="s"><spring:message code="searchPage.games"/></paw:text>
                </div>
                <paw:carrousel id="games-list" elements="${games}" isGame="true"/>
            </div>
        </c:otherwise>
    </c:choose>
</paw:layout>