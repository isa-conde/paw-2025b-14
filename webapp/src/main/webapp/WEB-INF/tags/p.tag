<%@ tag language ="java" pageEncoding="UTF-8" %>
<%@ attribute name="size" required="false" rtexprvalue="true" %>
<%@ attribute name="weight" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="fontSize" value="${size == 'xs' ? 'var(--text-14)' : 
                               size == 's' ? 'var(--text-18)' : 
                               size == 'm' ? 'var(--text-20)' :
                               size == 'l' ? 'var(--text-24)' : 
                               size == 'xl' ? 'var(--text-32)' : 'var(--text-20)'}" />

<c:set var="fontWeight" value="${weight == '1' ? '400' : 
                                weight == '2' ? '500' : 
                                weight == '3' ? '600' : 
                                weight == '4' ? '700' : '700'}" />

<p style="font-size: ${fontSize}; font-weight: ${fontWeight}; color: var(--primary-text); font-family: var(--font-main);">
    <jsp:doBody/>
</p>
