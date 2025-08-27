<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <h1>Hello <c:out value="${user.username}"/>>!</h1>
    <h4>Your id is <c:out value="${user.id}"/></h4>
    <paw:button type="main" onclick="goToIndex()">Join a Tournament</paw:button>
    <paw:button type="profile" onclick="goToIndex()"><c:out value="${user.username}"/></paw:button>
    
    <script>
        function goToIndex() {
            window.location.href = '<c:url value="/index"/>';
        }
    </script>
</body>
</html>

