<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<body>
<h2>Crear un Torneo!</h2>
<c:url value="/tournament/create" var="postPath"/>
<form:form modelAttribute="tournamentForm" action="${postPath}" method="post">
    <div>
        <form:label path="name">Name: </form:label>
        <form:input type="text" path="name"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="name">Creator ID: </form:label>
        <form:input type="number" path="creatorid"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="gameid">Game: </form:label>
        <form:select path="gameid" items="${games}" itemValue="id" itemLabel="name"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="region">Region: </form:label>
        <form:select path="region" items="${regions}" />
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="elo">Tournament Level: </form:label>
        <form:select path="elo" items="${elos}" />
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="startdate">Start Date: </form:label>
        <form:input type="date" path="startdate"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="enddate">End Date: </form:label>
        <form:input type="date" path="enddate"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="format">Format: </form:label>
        <form:input type="text" path="format"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="structure">Tournament Structure: </form:label>
        <form:select path="structure" items="${structures}" />
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <form:label path="max_participants">Amount of participants: </form:label>
        <form:input type="number" path="max_participants"/>
		<form:errors path="name" cssClass="formError"/>
    </div>
    <div>
        <input type="submit" value="Crear!"/>
    </div>
</form:form>
</body>
</html>