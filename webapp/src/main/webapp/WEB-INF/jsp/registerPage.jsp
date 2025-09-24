<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="registerUrl" value="/register"/>
<c:url var="loginUrl" value="/login"/>

<paw:form-layout title="registerPage.pageTitle">
    <paw:text type="title">
        <spring:message code="registerPage.pageTitle"/>
    </paw:text>
    <form:form cssClass="form-container" modelAttribute="registerForm" action="${registerUrl}" method="post">
        <div>
            <paw:input path="username" label="registerPage.usernameLabel" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="email" label="registerPage.emailLabel" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="password" label="registerPage.passwordLabel" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="repeatPassword" label="registerPage.confirmPasswordLabel" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="registerPage.submitRegisterForm" inputType="submit"/>
        </div>
    </form:form>
    <div class="link-btn-container single">
        <paw:link-button href="${loginUrl}" text="registerPage.goToLogin"/>
    </div>
</paw:form-layout>