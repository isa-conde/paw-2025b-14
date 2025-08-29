<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="h1">Are you ready?</paw:text>
        <br>
        <paw:text type="h2">Let's play.</paw:text>
    </paw:banner>

    <div class="container">
        <div class="cards-container">
            <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick=""/>
            <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick=""/>
        </div>

        <paw:text type="h2">Open Tournaments</paw:text>
        <paw:text type="h4">League of Legends</paw:text>

        <paw:profileButton text="${user.username}" onclick=""/>

        <paw:tournament-card
                image="/images/tournament.jpg"
                title="Torneo Anual"
                subtitle="Edición 2025"
                tags="${['Fútbol','Juveniles','Verano']}">

            <p>Contenido adicional aquí</p>
        </paw:tournament-card>
    </div>
</body>
</html>

