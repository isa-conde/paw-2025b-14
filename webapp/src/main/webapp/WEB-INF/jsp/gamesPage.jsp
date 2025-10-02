<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<paw:layout user="${user}">
    <paw:banner size="s" image="${pageContext.request.contextPath}/images/moonlight.jpg">
        <paw:text type="title" size="xl" stroke="true"><spring:message code="games.title"/></paw:text>
    </paw:banner>
    <div class="content-container">
        <paw:elements-grid elements="${games}" id="games-grid" isGame="true"/>
    </div>

    <div class="pagination-container">
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 0}">
                    <paw:paginationLink page="${currentPage - 1}" url="/gamesPage">
                        <paw:text size="l" weight="thin"><</paw:text>
                    </paw:paginationLink>
                </c:if>
                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                        </c:when>
                        <c:otherwise>
                            <paw:paginationLink page="${i}" url="/gamesPage">
                                <paw:text weight="thin" size="l">${i + 1}</paw:text>
                            </paw:paginationLink>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages - 1}">
                    <paw:paginationLink page="${currentPage + 1}" url="/gamesPage">
                        <paw:text size="l" weight="thin">></paw:text>
                    </paw:paginationLink>
                </c:if>
            </div>
        </c:if>
    </div>

</paw:layout>