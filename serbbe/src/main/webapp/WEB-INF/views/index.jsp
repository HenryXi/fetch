<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Search Stack</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/serbbe.css"/>"/>
</head>
<body>
<div id="form_div">
    <form id="index_form" action="/s" autocomplete="off">
        <input type="text" name="q" id="search_input" autocomplete="off"/>
    </form>
</div>
</body>
</html>
