<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="registerUrl" value="/register"/>
<c:url var="loginUrl" value="/login"/>

<paw:form-layout title="Register Page">
    <paw:text type="title">Register</paw:text>
    <form:form cssClass="form-container" modelAttribute="registerForm" action="${registerUrl}" method="post">
        <div>
            <paw:input path="username" label="Username" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="email" label="Email" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="password" label="Password" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="repeatPassword" label="Confirm password" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="Register" inputType="submit"/>
        </div>
    </form:form>
    <div class="link-btn-container single">
        <paw:link-button href="${loginUrl}" text="Already have an account?"/>
    </div>
</paw:form-layout>