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
<%@ attribute name="emptyOption" required="false"%>
<%@ attribute name="inline" required="false"%>

<c:choose>
    <c:when test="${inputType != 'submit'}">
        <form:label path="${path}" class="input-label ${inline == 'true' ? 'inline-input-container' : (containerType == 'half' ? 'half-input-container' : 'input-container')}">
            <paw:text weight="3" size="l"><c:out value="${label}"/></paw:text>
            <c:choose>
                <c:when test="${inputType == 'input' || inputType == null}">
                    <form:input path="${path}" class="input"/>
                </c:when>
                <c:when test="${inputType == 'select'}">
                    <c:choose>
                        <c:when test="${itemLabel != null && itemValue != null}">
                            <form:select path="${path}" cssClass="input">
                                <c:if test="${emptyOption != null}">
                                    <form:option value="" label="${emptyOption}"/>
                                </c:if>
                                <form:options items="${items}" itemLabel="${itemLabel}" itemValue="${itemValue}"/>
                            </form:select>
                        </c:when>
                        <c:otherwise>
                            <form:select path="${path}" cssClass="input">
                                <c:if test="${emptyOption != null}">
                                    <form:option value="" label="${emptyOption}"/>
                                </c:if>
                                <form:options items="${items}"/>
                            </form:select>
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
                <c:when test="${inputType == 'hidden'}">
                    <form:hidden path="${path}" />
                </c:when>
            </c:choose>
        </form:label>
    </c:when>
    <c:otherwise>
        <div class="inline-input-container submit-container">
            <input type="submit" class="btn submit" value="${label}"/>
        </div>
    </c:otherwise>
</c:choose>





