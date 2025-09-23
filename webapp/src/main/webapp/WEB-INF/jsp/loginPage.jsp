<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:url var="loginUrl" value="/login"/>
<c:url var="registerUrl" value="/register"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="Login Page">
    <paw:text type="title">Log In</paw:text>
    <form method="post" action="${loginUrl}" enctype="application/x-www-form-urlencoded" class="form-container">
        <div class="input-container">
            <label for="username" class="input-label">Username: </label>
            <input type="text" id="username" name="j_username" class="input"/>
        </div>
        <div class="input-container">
            <label for="password" class="input-label">Password: </label>
            <input type="password" id="password" name="j_password" class="input"/>
        </div>
        <div class="remember-me-container">
            <label class="custom-checkbox">
                <input type="checkbox" id="remember-me" name="j_rememberme">
                <span class="checkmark"></span>
                <span class="checkbox-label">Remember Me: </span>
            </label>
        </div>
        <div class="submit-container">
            <input type="submit" value="Log In" class="btn submit"/>
        </div>
        <div class="link-btn-container">
            <paw:link-button href="${registerUrl}" text="Register now"/>
            <paw:link-button href="${forgotPasswordUrl}" text="Forgot password?"/>
        </div>
    </form>
</paw:form-layout>