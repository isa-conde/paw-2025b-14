<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="resendVerificationUrl" value="/verify?userId=${user.id}"/>

<paw:form-layout title="Verify account">

    <paw:text type="title">Verify your account</paw:text>
    <paw:text size="l">Please check your inbox to verify your account. If your link has expired, please resend the verification.</paw:text>
    <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="Resend Verification" inputType="submit"/>
    </form:form>

</paw:form-layout>