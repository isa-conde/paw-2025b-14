<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="disabled" type="java.lang.Boolean" required="false" %>

<c:url value="/images/refresh.png" var="refreshLogo"/>
<c:url value="/tournamentsPage" var="baseUrl"/>


<a href="${baseUrl}">
    <button type="button" class="btn submit" ${disabled ? 'disabled' : ''} ">
        <img src="${refreshLogo}" alt="Refresh">
    </button>
</a>
