<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:h1>Are you ready?</paw:h1>
        <br>
        <paw:h2>Let's play.</paw:h2>
    </paw:banner>

    <div class="cards-container">
        <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick=""/>
        <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick=""/>
    </div>

    <paw:profileButton text="${user.username}" onclick=""/>

</body>
</html>

