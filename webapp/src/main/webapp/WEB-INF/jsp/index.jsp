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
    <paw:h1 text="Header 1"/>
    <paw:h2 text="Header 2"/>
    <paw:h3 text="Header 3" fontFamily="russo_one"/>
    <paw:h4 text="Header 4"/>
    <paw:p text="Paragraph"/>
    <paw:datetime year="2025" month="08" day="20"/>
</body>
</html>

