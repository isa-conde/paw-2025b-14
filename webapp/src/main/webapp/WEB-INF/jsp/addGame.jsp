<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<body>
<h2>Agregar un juego</h2>
<c:url value="/game/create" var="postPath"/>
<form:form modelAttribute="gameForm" action="${postPath}" method="post" enctype="multipart/form-data">
    <div>
        <form:label path="name">Name: </form:label>
        <form:input type="text" path="name"/>
    </div>
    <div>
        <form:label path="genre">Genre: </form:label>
        <form:select path="genre" items="${genres}" />
    </div>
    <div>
        <form:label path="image">Image: </form:label>
        <form:input type="file" path="image"/>
    </div>

    <div>
        <h3>Formatos</h3>

        <!-- Formato 1 -->
        <div>
            <form:label path="formats[0].name">Nombre:</form:label>
            <form:input path="formats[0].name"/>
            <form:label path="formats[0].playersPerTeam">Jugadores por equipo:</form:label>
            <form:input type="number" path="formats[0].playersPerTeam"/>
        </div>

        <!-- Formato 2 -->
        <div>
            <form:label path="formats[1].name">Nombre:</form:label>
            <form:input path="formats[1].name"/>
            <form:label path="formats[1].playersPerTeam">Jugadores por equipo:</form:label>
            <form:input type="number" path="formats[1].playersPerTeam"/>
        </div>

        <!-- Formato 3 -->
        <div>
            <form:label path="formats[2].name">Nombre:</form:label>
            <form:input path="formats[2].name"/>
            <form:label path="formats[2].playersPerTeam">Jugadores por equipo:</form:label>
            <form:input type="number" path="formats[2].playersPerTeam"/>
        </div>

        <div>
        <input type="submit" value="Guardar!"/>
    </div>
</form:form>
</body>
</html>
