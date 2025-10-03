<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url var="resendVerificationUrl" value="/verify?userId=${user.id}"/>
<spring:message code="login.title" var="pageTitle"/>

<paw:form-layout pageTitle="${pageTitle}">

    <paw:text type="title"><spring:message code="accountVerification.title"/></paw:text>
    <paw:text size="l"><spring:message code="accountVerification.instruction"/></paw:text>
    <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="accountVerification.resend" inputType="submit"/>
    </form:form>

</paw:form-layout>
