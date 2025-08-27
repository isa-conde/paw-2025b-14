<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <title></title>
</head>
<body>
    <div>
        <h1>Game: <c:out value="${game.name}"/>!</h1>
    </div>
    <div>
        <h4>Genre: <c:out value="${game.genre}"/></h4>
    </div>
</body>
</html>