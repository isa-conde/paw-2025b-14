<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<paw:layout user="${user}">
    <paw:banner image="data:image/png;base64,${game.base64Img}">
        <div class="page-title">
            <paw:text type="title" size="m" stroke="true">Tournaments</paw:text>
            <paw:text type="title" size="xl" stroke="true"><c:out value="${game.game.name}"/></paw:text>
        </div>
    </paw:banner>
    <div class="content-container">
        <form:form cssClass="form" modelAttribute="filterForm" method="post">
            <div class="filter-container">
                <paw:input path="region" label="Region" inputType="select" items="${regions}" emptyOption="All Regions" inline="true"/>
                <paw:input path="elo" label="Level" inputType="select" items="${elos}" emptyOption="All Levels" inline="true"/>
                <paw:input path="" label="Filter" inputType="submit" inline="true"/>
            </div>
        </form:form>

        <paw:elements-grid elements="${tournaments}" id="tournaments-grid"/>
    </div>
</paw:layout>
