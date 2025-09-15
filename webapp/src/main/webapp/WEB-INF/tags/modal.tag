<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="title" required="true"%>
<%@ attribute name="id" required="true"%>

<dialog id="${id}" class="modal">
    <div class="header">
        <paw:text type="title" ><spring:message code="${title}"/></paw:text>
        <paw:iconButton icon="${pageContext.request.contextPath}/images/close.png" onclick="closeModal('${id}')"/>
    </div>
    <jsp:doBody/>
</dialog>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>
