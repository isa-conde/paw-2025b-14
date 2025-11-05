<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="path" required="true"%>
<%@ attribute name="label" required="false"%>
<%@ attribute name="containerType" required="false"%>
<%@ attribute name="inputType" required="false"%>
<%@ attribute name="items" type="java.util.List" required="false"%>
<%@attribute name="itemMap" type="java.util.LinkedHashMap" required="false" %>
<%@ attribute name="itemValue" required="false"%>
<%@ attribute name="itemLabel" required="false"%>
<%@ attribute name="emptyOption" required="false"%>
<%@ attribute name="inline" required="false"%>
<%@ attribute name="value" required="false"%>
<%@ attribute name="secondary" required="false"%>
<%@ attribute name="arg" required="false" type="java.lang.Integer" %>
<%@ attribute name="hasConstraint" required="false" type="java.lang.Boolean"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="rating" required="false" type="java.lang.Integer"%>
<%@ attribute name="accept" required="false" type="java.lang.String" %>
<%@attribute name="fileText" required="false" type="java.lang.String" %>

<c:set var="secondaryClass" value="${not empty secondary && secondary == 'true' ? 'secondary' : ''}"/>

<c:choose>
    <c:when test="${inputType != 'submit'}">
        <form:label path="${path}" class="input-label ${inline == 'true' ? 'inline-input-container' : (containerType == 'half' ? 'half-input-container' : 'input-container')}">
            <paw:text weight="3" size="l"><spring:message code="${label}" arguments="${arg}"/></paw:text>
            <c:choose>
                <c:when test="${inputType == 'input' || inputType == null}">
                    <form:input path="${path}" class="input"/>
                </c:when>
                <c:when test="${inputType == 'select'}">
                    <c:choose>
                        <c:when test="${(itemLabel != null && itemValue != null)}">
                            <form:select path="${path}" cssClass="input">
                                <c:if test="${emptyOption != null}">
                                    <form:option value="" label="${emptyOption}"/>
                                </c:if>
                                <form:options items="${items}" itemLabel="${itemLabel}" itemValue="${itemValue}"/>
                            </form:select>
                        </c:when>
                        <c:when test="${itemMap != null}">
                            <form:select path="${path}" cssClass="input">
                                <c:if test="${emptyOption != null}">
                                    <form:option value="" label="${emptyOption}"/>
                                </c:if>
                                <form:options items="${itemMap}"/>
                            </form:select>
                        </c:when>
                        <c:otherwise>
                            <form:select path="${path}" cssClass="input">
                                <c:if test="${emptyOption != null}">
                                    <form:option value="${null}" label="${emptyOption}"/>
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
                    <form:input path="${path}"
                                type="number"
                                class="input"
                                min="0"
                                step="1"
                                inputmode="numeric"
                                onkeydown="return onlyUnsignedIntKeydown(event)"
                                onpaste="return onlyUnsignedIntPaste(event)"
                                oninput="this.value = this.value.replace(/\D+/g,'')" />
                </c:when>
                <c:when test="${inputType == 'decimalNumber'}">
                    <form:input path="${path}"
                                type="number"
                                class="input"
                                min="1"
                                step="0.5"
                                inputmode="decimal"
                                onkeydown="return onlyUnsignedDecimalKeydown(event)"
                                onpaste="return onlyUnsignedDecimalPaste(event)"
                                oninput="clampDecimalInput(this, 1, 5, 0.5)"/>
                </c:when>
                <c:when test="${inputType == 'email'}">
                    <form:input path="${path}" type="email" class="input"/>
                </c:when>
                <c:when test="${inputType == 'hidden'}">
                    <form:hidden path="${path}" value="${value}"/>
                </c:when>
                <c:when test="${inputType == 'file'}">
                    <div class="file-input-container">
                        <form:input path="${path}" type="file" class="file-input" id="file-${path}" accept="${accept}" onchange="updateFileName('file-${path}', 'file-text-${path}')"/>
                        <label for="file-${path}" class="file-input-label">
                            <img src="${pageContext.request.contextPath}/images/upload.png" alt="Upload" class="file-input-icon"/>
                            <span class="file-input-text" id="file-text-${path}"><spring:message code="${fileText}"/></span>
                        </label>
                    </div>
                </c:when>
                <c:when test="${inputType == 'password'}">
                    <form:input type="password" path="${path}" class="input"/>
                </c:when>
                <c:when test="${inputType == 'textarea'}">
                    <form:textarea path="${path}" class="input textarea"/>
                </c:when>
            </c:choose>
            <c:if test="${hasConstraint}">
                <form:errors path="${path}" cssClass="form-error" element="h1"/>
            </c:if>
        </form:label>
    </c:when>
    <c:otherwise>
        <div class="inline-input-container submit-container">
            <spring:message code="${label}" var="msg"/>
            <input type="submit" class="btn submit ${secondaryClass}" value="${msg}">
        </div>
    </c:otherwise>
</c:choose>

<script src="${pageContext.request.contextPath}/js/input.js"></script>

