<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello Lucene</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/serbbe.css"/>"/>
</head>
<body>
<div id="main">
    <div>
        <form action="/s" autocomplete="off">
            <input type="text" name="q" id="search_input" value="${keyWord}" autocomplete="off"/>
        </form>
    </div>
    <div id="results">
        <c:forEach items="${questions}" var="question">
            <div class="result">
                <div><h3><a href="http://stackoverflow.com/questions/${question.id}" target="_blank">${question.title}</a></h3></div>
                <div><span>${question.content}</span></div>
            </div>
        </c:forEach>
    </div>
    <div id="suggests">
        <div class="suggest">
            <a href="#">href href href href href href href href href </a>
        </div>
    </div>
</div>
</body>
</html>
