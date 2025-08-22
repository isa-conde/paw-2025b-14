<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
<body>
    <paw:button text="Guardar" size="lg"/>
    <paw:button text="Mandar"/>
    <paw:button text="Borrar"  size="sm" disabled="true"/>
    <paw:h1 text="Header 1" fontFamily="russo_one"/>
    <paw:h2 text="Header 2"/>
    <paw:h3 text="Header 3"/>
    <paw:h4 text="Header 4"/>
    <paw:p text="Paragraph"/>
    <paw:datetime year="2025" month="08" day="20"/>
    <paw:icon-card icon="images/Logo_River_Plate.png" text="Dashboard"/>
    <paw:content-card title="This is my title" text="This is my text" butText="This is my button"/>
</body>
</html>

