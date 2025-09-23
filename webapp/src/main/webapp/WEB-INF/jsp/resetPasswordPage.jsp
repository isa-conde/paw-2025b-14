<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="resetPasswordUrl" value="/resetPassword?token=${token}&userId=${userId}"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>

<paw:form-layout title="Reset Password">
    <c:choose>
        <c:when test="${validToken}">
            <paw:text type="title">Reset Password</paw:text>
            <form:form cssClass="form-container" modelAttribute="resetPasswordForm" action="${resetPasswordUrl}" method="post">
                <div>
                    <paw:input path="newPassword" label="Password" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="confirmNewPassword" label="Confirm password" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="" label="Reset" inputType="submit"/>
                </div>
            </form:form>
        </c:when>
        <c:otherwise>
            <paw:text type="title">Password Reset failed</paw:text>
            <paw:text size="l">Your token doesn't exist or has expired.</paw:text>
            <br>
            <paw:button onclick="window.location.href='${forgotPasswordUrl}'" text="Resend Request"/>
        </c:otherwise>
    </c:choose>

</paw:form-layout>