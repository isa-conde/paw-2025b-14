<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<body>
<h2>Añadir un juego</h2>
<c:url value="/game/create" var="postPath"/>
<form:form modelAttribute="gameForm" action="${postPath}" method="post">
    <div>
        <form:label path="name">Name: </form:label>
        <form:input type="text" path="name"/>
    </div>
    <div>
        <form:label path="genre">Genre: </form:label>
        <form:select path="genre" items="${genres}" />
    </div>
    <div>
        <input type="submit" value="Guardar!"/>
    </div>
</form:form>
</body>
</html>