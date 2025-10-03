<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="pageTitle" required="false" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <link rel="icon" type="image/x-icon" href="<c:url value="/public/favicon.ico"/>">
    <title>
        <c:choose>
            <c:when test="${not empty pageTitle}">RankUp - <c:out value="${pageTitle}"/></c:when>
            <c:otherwise>RankUp</c:otherwise>
        </c:choose>
    </title>
</head>
<body class="form-page">
    <jsp:doBody/>
</body>
</html>