<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<c:url var="resetPasswordUrl" value="/forgotPassword/reset?token=${token}&userId=${userId}"/>
<c:url var="forgotPasswordUrl" value="/forgotPassword"/>
<spring:message code="login.title" var="pageTitle"/>

<paw:form-layout pageTitle="${pageTitle}">
    <c:choose>
        <c:when test="${validToken}">
            <paw:text type="title"><spring:message code="passwordReset.page.title"/></paw:text>
            <form:form cssClass="form-container" modelAttribute="resetPasswordForm" action="${resetPasswordUrl}" method="post">
                <div>
                    <paw:input path="newPassword" label="passwordReset.newPassword" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="confirmNewPassword" label="passwordReset.confirmNewPassword" inputType="password" hasConstraint="true"/>
                </div>
                <div>
                    <paw:input path="" label="passwordReset.submit" inputType="submit"/>
                </div>
            </form:form>
        </c:when>
        <c:otherwise>
            <paw:text type="title"><spring:message code="passwordReset.failed.title"/></paw:text>
            <paw:text size="l"><spring:message code="passwordReset.failed.message"/></paw:text>
            <br>
            <paw:button onclick="window.location.href='${forgotPasswordUrl}'" text="passwordReset.failed.resend"/>
        </c:otherwise>
    </c:choose>
</paw:form-layout>
