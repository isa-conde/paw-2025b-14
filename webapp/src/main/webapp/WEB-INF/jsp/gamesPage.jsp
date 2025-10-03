<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="games.title" var="title"/>

<paw:layout user="${user}" pageTitle="${title}">
    <paw:banner size="s" image="${pageContext.request.contextPath}/images/moonlight.jpg">
        <paw:text type="title" size="xl" stroke="true">${title}</paw:text>
    </paw:banner>
    <div class="content-container">
        <paw:elements-grid elements="${games}" id="games-grid" isGame="true"/>
    </div>
</paw:layout>
