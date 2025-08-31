<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="path" required="true"%>
<%@ attribute name="label" required="true"%>
<%@ attribute name="containerType" required="false"%>
<%@ attribute name="inputType" required="false"%>

<c:choose>
    <c:when test="${inputType != 'submit'}">
        <label class="input-label ${containerType == 'half' ? 'half-input-container' : 'input-container'}">
            <paw:text weight="3" size="l"><c:out value="${label}"/></paw:text>
            <c:choose>
                <c:when test="${inputType == 'input' || inputType == null}">
                    <input class="input"/>
                </c:when>
                <c:when test="${inputType == 'select'}">
                    <select class="input">
                        <jsp:doBody/>
                    </select>
                </c:when>
                <c:when test="${inputType == 'date'}">
                    <input type="date" class="input"/>
                </c:when>
                <c:when test="${inputType == 'number'}">
                    <input type="number" class="input"/>
                </c:when>
            </c:choose>
        </label>
    </c:when>
    <c:otherwise>
        <input type="submit" class="btn submit" value="${label}"/>
    </c:otherwise>
</c:choose>





