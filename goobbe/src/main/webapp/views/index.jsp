<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>2</title>
    <link rel="stylesheet" type="text/css" href="/css/all.css">">
</head>
<body class="questions-page new-topbar">
<div class="topbar">
    <div class="topbar-wrapper">
        <div class="topbar-links">

            <div class="links-container">
                    <span class="topbar-menu-links">
                            <a href="#" class="login-link">sign up</a>
                            <a href="#" class="login-link">log in</a>
                    </span>
            </div>

            <div class="search-container">
                <form id="search" action="#" method="get" autocomplete="off">
                    <input name="q" type="text" placeholder="search" value="" tabindex="1" autocomplete="off"
                           maxlength="240">
                </form>
            </div>

        </div>
    </div>
</div>

<div class="container">
    <div id="header">
        There should be logo pic
    </div>
    <div id="content" class="snippet-hidden">
        <div id="mainbar">
            <div class="subheader">
                <h1 id="h-all-questions">All Questions</h1>
            </div>
            <div id="questions">
                <c:forEach var="question" items="${questions}">
                    <div class="question-summary" id="question-summary-28208463">
                        <div class="summary">
                            <h3>
                                <a href="/questions/${question.id}/${question.title4url}" class="question-hyperlink">${question.title}</a>
                            </h3>

                            <div class="excerpt">
                                ${question.content}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <br class="cbt">

            <div class="page-sizer fr">
                <a href="#" title="show 15 items per page" class="page-numbers ">15</a>
                <a href="#" title="show 30 items per page" class="page-numbers ">30</a>
                <a href="#" title="show 50 items per page" class="page-numbers current">50</a>
                <span class="page-numbers desc">per page</span>
            </div>
            <div class="pager fl">


                <span class="page-numbers current">1</span> <a href="#" title="go to page 2"> <span
                    class="page-numbers">2</span> </a>
                <a href="#" title="go to page 3"> <span class="page-numbers">3</span> </a>
                <a href="#" title="go to page 4"> <span class="page-numbers">4</span> </a>
                <a href="#" title="go to page 5"> <span class="page-numbers">5</span> </a>
                <span class="page-numbers dots">…</span> <a href="#" title="go to page 175093">
                <span class="page-numbers">175093</span> </a>
                <a href="#" rel="next" title="go to page 2"> <span
                        class="page-numbers next"> next</span> </a>

            </div>
        </div>
    </div>
</div>
<div id="footer" class="categories">
    <div class="footerwrap">
        <div id="copyright">
            © 2015
        </div>
    </div>
</div>
</body>
</html>
