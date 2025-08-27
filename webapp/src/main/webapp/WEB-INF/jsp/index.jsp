<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <h1>Hello <c:out value="${user.username}"/>!</h1>
    <h4>Your id is <c:out value="${user.id}"/></h4>
</body>
</html>

