<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message code="team.create.pageTitle" var="pageTitle"/>

<paw:layout user="${user}" pageTitle="${pageTitle}">
    <div class="tournament-form-page">
        <div class="tournament-form-page__image">
            <img src="${pageContext.request.contextPath}/images/teamCreationImage.jpg" alt="Team Image" />
        </div>
        <div class="tournament-form-page__form">
            <form:form cssClass="form center" modelAttribute="teamForm" action="${pageContext.request.contextPath}/team/create" method="post" enctype="multipart/form-data" id="teamForm">
                <div>
                    <paw:text type="title" size="xl"><spring:message code="team.create.title"/></paw:text>
                    <div class="row center">
                        <paw:input path="name" label="team.create.name" hasConstraint="true"/>
                    </div>
                    <div class="row center">
                        <paw:input path="pfp" label="team.create.teamImage" inputType="file" hasConstraint="true"/>
                    </div>
                    <div class="row center">
                        <paw:input path="banner" label="team.create.teamBanner" inputType="file" hasConstraint="true"/>
                    </div>
                    <div class="row center">
                        <label for="memberInput" class="input-label"><spring:message code="team.create.members"/></label>
                        <div class="row center member-input-container">
                            <input type="text" id="memberInput" placeholder="<spring:message code="team.create.addMember.placeholder"/>" class="input" />
                            <button type="button" id="addMemberBtn" class="btn submit"><spring:message code="team.create.add"/></button>
                        </div>
                        <form:errors path="members" cssClass="form-error" element="h1"/>
                        <div id="chipContainer" class="chip-container">
                            <c:forEach var="member" items="${teamForm.members}">
                                <div class="chip">
                                    <c:out value="${member}"/>
                                    <span class="chip-close">X</span>
                                    <input type="hidden" name="members" value="${member}"/>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                        <div class="row center">
                        <paw:input path="" label="team.create.create" containerType="half" inputType="submit"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</paw:layout>

<script src="${pageContext.request.contextPath}/js/teamMembers.js"></script>

