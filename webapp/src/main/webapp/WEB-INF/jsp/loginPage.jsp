<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="loginUrl" value="/login"/>
<c:url var="registerUrl" value="/register"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="loginPage.pageTitle">
    <paw:text type="title"><spring:message code="loginPage.pageHeader"/></paw:text>
    <c:if test="${invalidCredentials}">
        <span class="form-error"><spring:message code="loginPage.invalidCredentials"/></span>
    </c:if>
    <form method="post" action="${loginUrl}" enctype="application/x-www-form-urlencoded" class="form-container">
        <div class="input-container">
            <label for="username" class="input-label"><spring:message code="loginPage.usernameLabel"/> </label>
            <input type="text" id="username" name="j_username" class="input"/>
        </div>
        <div class="input-container">
            <label for="password" class="input-label"><spring:message code="loginPage.passwordLabel"/> </label>
            <input type="password" id="password" name="j_password" class="input"/>
        </div>
        <div class="remember-me-container">
            <label class="custom-checkbox">
                <input type="checkbox" id="remember-me" name="j_rememberme">
                <span class="checkmark"></span>
                <span class="checkbox-label"><spring:message code="loginPage.rememberMeLabel"/> </span>
            </label>
        </div>
        <div class="submit-container">
            <input type="submit" value="<spring:message code='loginPage.submitButton'/>" class="btn submit"/>
        </div>
        <div class="link-btn-container">
            <paw:link-button href="${registerUrl}" text="loginPage.registerNowButton"/>
            <paw:link-button href="${forgotPasswordUrl}" text="loginPage.forgotPasswordButton"/>
        </div>
    </form>
</paw:form-layout>