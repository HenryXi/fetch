<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>

        <c:choose>
            <c:when test="${totalPage==-1}">
                <title>Search result - Goobbe</title>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${currentPage==1}">
                        <title>All questions - Goobbe</title>
                    </c:when>
                    <c:otherwise>
                        <title>All questions - Page ${currentPage} - Goobbe</title>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    <link rel="stylesheet" type="text/css" href="/css/all.css">
    <link rel="icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
</head>
<body class="questions-page new-topbar">
<jsp:include page="topbar.jsp"/>
<div class="container">
    <%--<div id="header">--%>
        <%--There should be logo pic--%>
    <%--</div>--%>
    <div id="content" class="snippet-hidden">
        <div id="mainbar">
            <div class="subheader">
                <c:choose>
                    <c:when test="${totalPage==-1}">
                        <h1 id="h-all-questions">Search result(at most show 50 records)</h1>
                    </c:when>
                    <c:otherwise>
                        <h1 id="h-all-questions">All Questions</h1>
                    </c:otherwise>
                </c:choose>
            </div>
            <div id="questions">
                <c:forEach var="question" items="${questions}">
                    <div class="question-summary" id="question-summary-28208463">
                        <div class="summary">
                            <h3>
                            <c:choose>
                                <c:when test="${totalPage==-1}">
                                    <a href="/question/${question.url}/${question.title4url}" class="question-hyperlink" target="_blank">${question.t}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/questions/${question.id}/${question.title4url}" class="question-hyperlink" target="_blank">${question.t}</a>
                                </c:otherwise>
                            </c:choose>


                            </h3>

                            <div class="excerpt">
                                ${question.c}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <br class="cbt">
            <c:if test="${totalPage!=-1}">
                <div class="pager fl">
                    <c:if test="${currentPage>1}">
                        <a href="/questions?page=${currentPage-1}" rel="next" title="go to page ${currentPage-1}">
                            <span class="page-numbers next"> prev</span>
                        </a>
                    </c:if>
                    <c:if test="${currentPage!=1}">
                        <a href="/questions?page=1" title="go to page 1">
                            <span class="page-numbers">1</span>
                        </a>
                    </c:if>
                    <c:if test="${currentPage>=5}">
                        <span class="page-numbers dots">…</span>
                    </c:if>
                    <c:if test="${currentPage-2>1}">
                        <a href="/questions?page=${currentPage-2}" title="go to page ${currentPage-2}">
                            <span class="page-numbers">${currentPage-2}</span>
                        </a>
                    </c:if>
                    <c:if test="${currentPage-1>1}">
                        <a href="/questions?page=${currentPage-1}" title="go to page ${currentPage-1}">
                            <span class="page-numbers">${currentPage-1}</span>
                        </a>
                    </c:if>
                    <span class="page-numbers current">${currentPage}</span>
                    <c:if test="${currentPage+1<totalPage}">
                        <a href="/questions?page=${currentPage+1}" title="go to page 4">
                            <span class="page-numbers">${currentPage+1}</span>
                        </a>
                    </c:if>
                    <c:if test="${currentPage+2<totalPage}">
                        <a href="/questions?page=${currentPage+2}" title="go to page 5">
                            <span class="page-numbers">${currentPage+2}</span>
                        </a>
                    </c:if>
                    <c:if test="${totalPage-currentPage>3}">
                        <span class="page-numbers dots">…</span>
                    </c:if>
                    <c:if test="${currentPage!=totalPage}">
                        <a href="/questions?page=${totalPage}" title="go to page 175093">
                            <span class="page-numbers">${totalPage}</span>
                        </a>
                    </c:if>
                    <c:if test="${currentPage<totalPage}">
                        <a href="/questions?page=${currentPage+1}" rel="next" title="go to page ${currentPage+1}">
                            <span class="page-numbers next"> next</span>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
