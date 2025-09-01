<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="path" required="true"%>
<%@ attribute name="label" required="true"%>
<%@ attribute name="containerType" required="false"%>
<%@ attribute name="inputType" required="false"%>
<%@ attribute name="items" type="java.util.List" required="false"%>
<%@ attribute name="itemValue" required="false"%>
<%@ attribute name="itemLabel" required="false"%>

<c:choose>
    <c:when test="${inputType != 'submit'}">
        <form:label path="${path}" class="input-label ${containerType == 'half' ? 'half-input-container' : 'input-container'}">
            <paw:text weight="3" size="l"><c:out value="${label}"/></paw:text>
            <c:choose>
                <c:when test="${inputType == 'input' || inputType == null}">
                    <form:input path="${path}" class="input"/>
                </c:when>
                <c:when test="${inputType == 'select'}">
                    <c:choose>
                        <c:when test="${itemLabel != null && itemValue != null}">
                            <form:select path="${path}" items="${items}" itemLabel="${itemLabel}" itemValue="${itemValue}" cssClass="input"/>
                        </c:when>
                        <c:otherwise>
                            <form:select path="${path}" items="${items}" cssClass="input"/>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${inputType == 'date'}">
                    <form:input path="${path}" type="date" class="input"/>
                </c:when>
                <c:when test="${inputType == 'number'}">
                    <form:input path="${path}" type="number" class="input"/>
                </c:when>
                <c:when test="${inputType == 'email'}">
                    <form:input path="${path}" type="email" class="input"/>
                </c:when>
            </c:choose>
        </form:label>
    </c:when>
    <c:otherwise>
        <input type="submit" class="btn submit" value="${label}"/>
    </c:otherwise>
</c:choose>





