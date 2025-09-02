<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ attribute name="image" required="true" rtexprvalue="true" %>
<%@ attribute name="game" required="false" rtexprvalue="true" %>
<%@ attribute name="title" required="true" rtexprvalue="true" %>
<%@ attribute name="subtitle" required="false" rtexprvalue="true" %>
<%@ attribute name="tags" required="false" rtexprvalue="true" type="java.util.List" %>

<div class="element-card">
    <img src="${image}" alt="Tournament background" class="element-card-image">
    <div class="element-card-content game">
        <paw:text type="title" size="xs"><c:out value="${game}"/></paw:text>
    </div>
    <div class="element-card-content">
        <paw:text type="title" size="s"><c:out value="${title}"/></paw:text>
        <paw:text size="s" weight="thin"><c:out value="${subtitle}"/></paw:text>
    </div>
</div>
