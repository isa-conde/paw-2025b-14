<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/login" var="loginUrl"/>

<paw:form-layout title="Confirmed Verification">
    <paw:text type="title">Password reset successful</paw:text>
    <paw:text size="l">You have changed your password successfully! You can now proceed with logging in.</paw:text>
    <br>
    <paw:button onclick="window.location.href='${loginUrl}'" text="Go to Login"/>
</paw:form-layout>
