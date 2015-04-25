<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>
    <c:choose>
        <c:when test="${currentPage==1}">
            All questions - Goobbe
        </c:when>
        <c:when test="${currentPage==-1}">
            Goobbe
        </c:when>
        <c:otherwise>
            All questions - Page ${currentPage} - Goobbe
        </c:otherwise>
    </c:choose>
    </title>
    <link rel="icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
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
                                <a href="/questions/${question.id}/${question.title4url}" class="question-hyperlink" target="_blank">${question.t}</a>
                            </h3>
                            <div class="excerpt">
                                ${question.c}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <br class="cbt">
            <c:choose>
                <c:when test="${currentPage!=-1}">
                    <div id="pagination" class="pager fl">
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
                </c:when>
                <c:otherwise>
                    <h2 class="bottom-notice" data-loc="2">Looking for more? Browse the <a href="/questions?page=1">complete
                        list of questions</a>.</h2>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
