<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<paw:layout user="${user}" isIndex="false">
    <c:choose>
        <c:when test="${user == null}">
            <paw:no-access/>
        </c:when>
        <c:otherwise>
            <paw:banner size="s" image="${pageContext.request.contextPath}/images/moonlight.jpg">
                <paw:text type="title" size="xl" stroke="true">Games</paw:text>
            </paw:banner>
            <div class="content-container">

                <c:choose>
                    <c:when test="${empty games}">
                        <div class="no-cards-container">
                            <paw:text size="l" weight="thin">(No games available)</paw:text>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <paw:elements-grid elements="${games}" id="games-grid" isGame="true"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</paw:layout>
