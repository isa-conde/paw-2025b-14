<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:url var="registerUrl" value="/register"/>
<c:url var="loginUrl" value="/login"/>
<spring:message code="register.page.header" var="title"/>

<paw:form-layout pageTitle="${title}">
    <paw:text type="title">${title}</paw:text>
    <form:form cssClass="form-container" modelAttribute="registerForm" action="${registerUrl}" method="post">
        <div>
            <paw:input path="username" label="register.username" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="email" label="register.email" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="password" label="register.password" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="repeatPassword" label="register.confirmPassword" inputType="password" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="register.submit" inputType="submit"/>
        </div>
    </form:form>
    <div class="link-btn-container single">
        <spring:message code='register.alreadyHaveAccount' var="alreadyHaveAccount"/>
        <paw:link-button href="${loginUrl}" text="${alreadyHaveAccount}"/>
    </div>
</paw:form-layout>
