<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="forgotPasswordPage.pageTitle">
    <paw:text type="title"><spring:message code="forgotPasswordPage.pageHeader"/></paw:text>
    <paw:text><spring:message code="forgotPasswordPage.description"/></paw:text>
    <form:form cssClass="form-container" modelAttribute="emailForm" action="${forgotPasswordUrl}" method="post">
        <div>
            <paw:input path="email" label="forgotPasswordPage.emailLabel" inputType="email" hasConstraint="true"/>
        </div>
        <div>
            <paw:input path="" label="forgotPasswordPage.submitButton" inputType="submit"/>
        </div>
    </form:form>
</paw:form-layout>