<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<header class="header">
    <a href="${pageContext.request.contextPath}/" class="logo-link">
        <img class="logo" src="${pageContext.request.contextPath}/images/crown.png" alt="logo"/>
    </a>
    <jsp:doBody/>
</header>