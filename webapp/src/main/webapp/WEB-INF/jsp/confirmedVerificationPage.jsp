<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/login" var="loginUrl"/>
<c:url value="/verify?userId=${userId}" var="resendVerificationUrl"/>

<paw:form-layout title="Confirmed Verification">
  <c:choose>
    <c:when test="${validToken}">
      <paw:text type="title">Verification successful</paw:text>
      <paw:text size="l">Your account has been correctly verified! You can now proceed with logging in.</paw:text>
      <br>
      <paw:button onclick="window.location.href='${loginUrl}'" text="Go to Login"/>
    </c:when>
    <c:otherwise>
      <paw:text type="title">Verification failed</paw:text>
      <paw:text size="l">Your token doesn't exist or has expired.</paw:text>
      <br>
      <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="Resend Verification" inputType="submit"/>
      </form:form>
    </c:otherwise>
  </c:choose>
</paw:form-layout>
