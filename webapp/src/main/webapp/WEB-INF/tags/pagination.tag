<%@ tag language="java" body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="pageNumber" required="false" type="java.lang.String" %>

<div class="pagination-container">
    <c:if test="${totalPages > 1}">
        <div class="pagination">

            <c:if test="${currentPage > 0}">
                <paw:paginationLink
                        pageNumber="${pageNumber}"
                        page="${currentPage - 1}"
                        url="${url}">
                    <paw:text size="l" weight="thin"><</paw:text>
                </paw:paginationLink>
            </c:if>

            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <paw:text weight="bold" size="xl">${i + 1}</paw:text>
                    </c:when>
                    <c:otherwise>
                        <paw:paginationLink
                                pageNumber="${pageNumber}"
                                page="${i}"
                                url="${url}">
                            <paw:text weight="thin" size="l">${i + 1}</paw:text>
                        </paw:paginationLink>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages - 1}">
                <paw:paginationLink
                        pageNumber="${pageNumber}"
                        page="${currentPage + 1}"
                        url="${url}">
                    <paw:text size="l" weight="thin">></paw:text>
                </paw:paginationLink>
            </c:if>
        </div>
    </c:if>
</div>
