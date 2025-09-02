<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="type" required="false" description="Text type: title or [main-text]" %>
<%@ attribute name="size" required="false" rtexprvalue="true" description="Font size: xs, s, [m], l, xl" %>
<%@ attribute name="weight" required="false" rtexprvalue="true" description="Font weight: thin, semi-bold, [bold]"%>
<%@ attribute name="stroke" required="false" rtexprvalue="true" description="Add text stroke: true or false"%>

<c:set var="textType" value="${not empty type? type : 'main-text'}"/>
<c:set var="fontSize" value="${not empty size? size : 'm'}"/>
<c:set var="fontWeight" value="${not empty weight? weight : 'bold'}"/>
<c:set var="strokeClass" value="${stroke == 'true' ? 'text-stroke' : ''}"/>

<p class="${textType} ${fontSize} ${fontWeight} ${strokeClass}"><jsp:doBody/></p>