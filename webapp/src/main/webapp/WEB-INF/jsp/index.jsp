<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<paw:layout>

    <paw:banner size="l" image="${pageContext.request.contextPath}/images/arcane.jpg">
        <paw:text type="h1">Are you ready?</paw:text>
        <br>
        <paw:text type="h2">Let's play.</paw:text>
    </paw:banner>

    <div class="cards-container">
        <paw:button-card title="Become an Organizer" butText="Create a Tournament" onclick="openModal()"/>
        <paw:modal title="Create a Tournament">
                <form:form>
                </form:form>
        </paw:modal>

        <paw:button-card title="Test your habilities" butText="Join a Tournament" onclick=""/>
    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/modal.js"></script>
