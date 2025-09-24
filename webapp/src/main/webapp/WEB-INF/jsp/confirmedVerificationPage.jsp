<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/login" var="loginUrl"/>
<c:url value="/verify?userId=${userId}" var="resendVerificationUrl"/>

<paw:form-layout title="Confirmed Verification">
  <c:choose>
    <c:when test="${validToken}">
      <paw:text type="title"><spring:message code="verification.successful.title"/></paw:text>
      <paw:text size="l"><spring:message code="verification.successful.text"/></paw:text>
      <br>
      <paw:button onclick="window.location.href='${loginUrl}'" text="verification.login"/>
    </c:when>
    <c:otherwise>
      <paw:text type="title"><spring:message code="verification.failed.title"/></paw:text>
      <paw:text size="l"><spring:message code="verification.failed.text"/></paw:text>
      <br>
      <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="verification.resend" inputType="submit"/>
      </form:form>
    </c:otherwise>
  </c:choose>
</paw:form-layout>
