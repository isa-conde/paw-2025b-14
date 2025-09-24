<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="forgotPasswordUrl" value="/forgotPassword"/>
<spring:message code="forgotPassword.title" var="title"/>
<paw:form-layout title="${title}">
    <paw:text type="title">${title}</paw:text>
    <paw:text><spring:message code="forgotPassword.text"/></paw:text>
    <form:form cssClass="form-container" modelAttribute="emailForm" action="${forgotPasswordUrl}" method="post">
        <div>
            <paw:input path="email" label="forgotPassword.email" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="forgotPassword.sendRequest" inputType="submit"/>
        </div>
    </form:form>
</paw:form-layout>