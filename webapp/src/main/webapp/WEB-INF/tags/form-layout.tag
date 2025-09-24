<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <link rel="icon" type="image/x-icon" href="<c:url value="/public/favicon.ico"/>">
    <title>RankUp</title>
</head>
<body class="form-page">
    <jsp:doBody/>
</body>
</html>