<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="Forgot Password">
    <paw:text type="title">Forgot Password</paw:text>
    <paw:text>Please enter the email associated to your account.</paw:text>
    <form:form cssClass="form-container" modelAttribute="emailForm" action="${forgotPasswordUrl}" method="post">
        <div>
            <paw:input path="email" label="Email" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="Send request" inputType="submit"/>
        </div>
    </form:form>
</paw:form-layout>