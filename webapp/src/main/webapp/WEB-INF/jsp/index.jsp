<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <paw:banner size="lg" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="h1">Are you ready?</paw:text>
        <br>
        <paw:text type="h2">Let's play.</paw:text>
    </paw:banner>

    <div class="cards-container">
        <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick=""/>
        <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick=""/>
    </div>

    <paw:button type="profile" onclick="goToIndex()"><c:out value="${user.username}"/></paw:button>
    
    <script>
        function goToIndex() {
            window.location.href = '<c:url value="/"/>';
        }
        
        function goToQuickMatch() {
            window.location.href = '<c:url value="/"/>';
        }
        
        function goToTournament() {
            window.location.href = '<c:url value="/tournament"/>';
        }
    </script>
</body>
</html>

