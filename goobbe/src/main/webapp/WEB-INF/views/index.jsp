<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>
    <c:choose>
        <c:when test="${currentPage==1}">
            All questions - ${host}
        </c:when>
        <c:when test="${currentPage==-1}">
            ${host}
        </c:when>
        <c:otherwise>
            All questions - Page ${currentPage} - ${host}
        </c:otherwise>
    </c:choose>
    </title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/all.css"/>" />
</head>
<body class="questions-page new-topbar">
<jsp:include page="topbar.jsp"/>
<div class="container">
    <div id="content" class="snippet-hidden">
        <div id="mainbar">
            <div class="subheader">
                <h1 id="allQuestionsHeader">
                <c:choose>
                    <c:when test="${currentPage==-1}">
                        Random Questions
                    </c:when>
                    <c:otherwise>
                        All Questions
                    </c:otherwise>
                </c:choose>
                </h1>
            </div>
            <div id="questions">
                <c:forEach var="question" items="${questions}" varStatus="questionsStatus">
                    <div class="question-summary" id="questions-summary-${questionsStatus.index}">
                        <div class="summary">
                            <h3>
                                <a href="/issue/${question.id}/${question.title4url}" class="question-hyperlink">${question.t}</a>
                            </h3>
                            <div class="excerpt">
                                ${question.c}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <br class="cbt">
        </div>
    </div>
</div>
</body>
</html>
