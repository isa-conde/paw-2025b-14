<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
    <title></title>
</head>
<body>
<div>
    <h1>Tournament: <c:out value="${tournament.name}"/>!</h1>
</div>
<div>
    <h4>Creator: <c:out value="${tournament.creatorid}"/></h4>
</div>
<div>
    <h4>Region: <c:out value="${tournament.region}"/></h4>
</div>
<div>
    <h4>Tournament Level: <c:out value="${tournament.elo}"/></h4>
</div>
<div>
    <h4>Start date: <c:out value="${tournament.startdate}"/></h4>
</div>
<div>
    <h4>End date: <c:out value="${tournament.enddate}"/></h4>
</div>
<div>
    <h4>Format: <c:out value="${tournament.format}"/></h4>
</div>
<div>
    <h4>Structure: <c:out value="${tournament.structure}"/></h4>
</div>
<div>
    <h4>Max participants: <c:out value="${tournament.max_participants}"/></h4>
</div>
</body>
</html>