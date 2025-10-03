<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/logout" var="logoutUrl"/>

<button type="button" class="logout-button" onclick="openModal('logoutConfirmModal')">
    <img src="${pageContext.request.contextPath}/images/logoutButton.png" alt="Logout">
</button>

<!-- Modal de confirmación -->
<paw:modal title="logout.modal.title" id="logoutConfirmModal">
    <div class="row center">
        <paw:button text="logout.confirm" onclick="window.location.href='${logoutUrl}'"/>
        <paw:button text="logout.cancel" onclick="closeModal('logoutConfirmModal')" secondary="true"/>
    </div>
</paw:modal>