<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="user" scope="request" type="ar.edu.itba.paw.model.User"/>

<html>
    <head>
        <link rel="stylesheet" href="<c:url value='/css/components.css'/>">
        <title></title>
    </head>
    <body>
        <div class="container">
            <paw:sidebar/>
            <paw:header>
                <paw:profileButton text="${user.username}" onclick="" disabled="true"/>
            </paw:header>
            <main class="main-content">
                <jsp:doBody/>
            </main>
        </div>
    </body>
</html>