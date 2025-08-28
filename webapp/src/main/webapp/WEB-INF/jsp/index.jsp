<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <paw:banner size="lg" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:h1>Are you ready?</paw:h1>
        <br>
        <paw:h2>Let's play.</paw:h2>
    </paw:banner>

    <div class="cards-container">
        <paw:content-card title="Become an Organizer" butText="Create a Tournament"/>
        <paw:content-card title="Test your habilities" butText="Join a Tournament"/>
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

