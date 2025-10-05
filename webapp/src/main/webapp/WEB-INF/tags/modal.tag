<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="title" required="true"%>
<%@ attribute name="id" required="true"%>
<%@ attribute name="returnUrl" required="false" type="java.lang.String" %>

<c:set var="closeArgs" value="'${id}'"/>
<c:if test="${not empty returnUrl}">
    <c:set var="closeArgs" value="${closeArgs}, '${fn:escapeXml(returnUrl)}'"/>
</c:if>

<dialog id="${id}" class="modal">
    <div class="header">
        <paw:text type="title"><spring:message code="${title}"/></paw:text>
        <paw:iconButton icon="${pageContext.request.contextPath}/images/close.png"
                        onclick="closeModal(${closeArgs})"/>
    </div>
    <jsp:doBody/>
</dialog>

<script src="<c:url value='/js/modal.js'/>"></script>