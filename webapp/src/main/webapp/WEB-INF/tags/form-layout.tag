<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="${title}"/></title>
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
</head>
<body class="form-page">
    <jsp:doBody/>
</body>
</html>