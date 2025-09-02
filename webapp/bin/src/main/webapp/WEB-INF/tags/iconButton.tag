<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="icon" required="true" %>
<%@ attribute name="onclick" required="false" %>

<button class="icon-btn" onclick="${onclick}">
    <img src="${icon}" alt="close modal"/>
</button>

