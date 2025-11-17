<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="loginUrl" value="/login"/>
<c:url var="registerUrl" value="/register"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>
<spring:message code="login.title" var="title"/>

<paw:form-layout pageTitle="${title}">
    <paw:text type="title"><spring:message code="login.title"/></paw:text>
    <c:if test="${invalidCredentials}">
        <span class="form-error"><spring:message code="login.invalidCredentials"/></span>
    </c:if>
    <form method="post" action="${loginUrl}" enctype="application/x-www-form-urlencoded" class="form-container">
        <div class="input-container">
            <label for="username" class="input-label"><spring:message code="login.username"/></label>
            <input type="text" id="username" name="j_username" class="input"/>
        </div>
        <div class="input-container">
            <label for="password" class="input-label"><spring:message code="login.password"/></label>
            <input type="password" id="password" name="j_password" class="input"/>
        </div>
        <div class="remember-me-container">
            <label class="custom-checkbox">
                <input type="checkbox" id="remember-me" name="j_rememberme">
                <span class="checkmark"></span>
                <span class="checkbox-label"><spring:message code="login.rememberMe"/></span>
            </label>
        </div>
        <div class="submit-container">
            <spring:message code="login.title" var="login"/>
            <input type="submit" value="${login}" class="btn submit"/>
        </div>
        <div class="link-btn-container">
            <spring:message code="login.registerNow" var="registerNow"/>
            <spring:message code="login.ForgotPassword" var="forgotPassword"/>
            <paw:link-button href="${registerUrl}" text="${registerNow}"/>
            <paw:link-button href="${forgotPasswordUrl}" text="${forgotPassword}"/>
        </div>
    </form>
</paw:form-layout>