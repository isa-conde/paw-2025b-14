<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url var="resetPasswordUrl" value="/resetPassword?token=${token}&userId=${userId}"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="resetPasswordPage.pageTitle">
    <c:choose>
        <c:when test="${validToken}">
            <paw:text type="title"><spring:message code="resetPasswordPage.successTitle"/></paw:text>
            <form:form cssClass="form-container" modelAttribute="resetPasswordForm" action="${resetPasswordUrl}" method="post">
                <div>
                    <paw:input path="newPassword" label="resetPasswordPage.newPasswordLabel" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="confirmNewPassword" label="resetPasswordPage.confirmNewPasswordLabel" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="" label="resetPasswordPage.submitButton" inputType="submit"/>
                </div>
            </form:form>
        </c:when>
        <c:otherwise>
            <paw:text type="title"><spring:message code="resetPasswordPage.failedTitle"/></paw:text>
            <paw:text size="l"><spring:message code="resetPasswordPage.failedMessage"/></paw:text>
            <br>
            <paw:button onclick="window.location.href='${forgotPasswordUrl}'" text="resetPasswordPage.resendRequestButton"/>
        </c:otherwise>
    </c:choose>

</paw:form-layout>