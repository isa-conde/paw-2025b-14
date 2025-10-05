<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/" var="homeUrl"/>
<c:url value="/verify?userId=${userId}" var="resendVerificationUrl"/>
<spring:message code="login.title" var="pageTitle"/>
<spring:message code="verification.successful.title" var="successTitle"/>
<spring:message code="verification.failed.title" var="failedTitle"/>

<c:choose>
  <c:when test="${validToken}">
    <paw:form-layout pageTitle="${pageTitle}">
      <paw:text type="title">${successTitle}</paw:text>
      <paw:text size="l"><spring:message code="verification.successful.text"/></paw:text>
      <br>
      <paw:button onclick="window.location.href='${homeUrl}'" text="verification.goToHome"/>
    </paw:form-layout>
  </c:when>
  <c:otherwise>
    <paw:form-layout pageTitle="${pageTitle}">
      <paw:text type="title">${failedTitle}</paw:text>
      <paw:text size="l"><spring:message code="verification.failed.text"/></paw:text>
      <br>
      <form:form method="post" action="${resendVerificationUrl}">
        <paw:input path="" label="verification.resend" inputType="submit"/>
      </form:form>
    </paw:form-layout>
  </c:otherwise>
</c:choose>

