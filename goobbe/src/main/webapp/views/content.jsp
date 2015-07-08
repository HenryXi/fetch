<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>${question.t}</title>
    <link rel="icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
<body class="question-page new-topbar">
<jsp:include page="topbar.jsp"/>
<!-- begin dictionary box -->
<script type="text/javascript" src="/js/dic_box.js"></script>
<div id="pl-dbox"><a class="pl-dbox-title" href="javascript:void(0);dboxCursorLoc();"><span id="pl-dbox-title-text">Dictionary</span></a>
    <div id="pl-dbox-content">
        <div id="pl-dbox-ajax-content">
            <p><input type="text" name="pl-dbox-search-field" style="width:97%;" id="pl-dbox-search-field"
                      onKeyPress="return dbxChkKy(event);" autocomplete=off/></p>

            <p id="pl-dbox-search-button"><input type="button" value=" Search! " OnClick="getdboxResults();"/></p>
        </div>
        <input id="pl-dbox-glossary" type="hidden" value="${tl}"/>
    </div>
</div>
<div class="container">
    <script language="JavaScript">
        var url='/questions/${question.id}/${question.title4url}';
        window.history.replaceState(null, null, url);
    </script>
<div id="content" class="snippet-hidden">
<div id="question-header">
    <h1 itemprop="name">${question.t}</h1>
</div>
<div id="mainbar">
    <div class="question" id="question">
        <table>
            <tbody>
            <tr>
                <td class="votecell">
                    <div class="question-mark">
                        <%--<span itemprop="upvoteCount" class="vote-count-post ">0</span>--%>
                    </div>

                </td>
                <td class="postcell">
                    <div>
                        <div class="post-text" itemprop="text">
                            ${question.c}
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <div id="questionsComments" class="comments">
                        <table>
                            <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true"
                                   data-comments-unavailable="false" data-addlink-disabled="true">
                            <c:forEach var="comment" items="${question.cs}" varStatus="questionCommentStatus">
                                <tr id="comment-${questionCommentStatus.index}" class="comment">
                                    <td>
                                        <div class="${questionCommentStatus.first? 'first-comment-mark':'other-comment-mark' }">
                                                <%--<span itemprop="upvoteCount" class="vote-count-post ">0</span>--%>
                                        </div>
                                    </td>
                                    <td class="comment-text">
                                        <div style="display: block;" class="comment-body">
                                            <span class="comment-copy">${comment.c}</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div id="answers">
        <div id="answers-header">
            <div class="subheader answers-subheader">
                <h2>
                    <c:choose>
                        <c:when test="${question.as.size()>0}">
                            Answers
                        </c:when>
                        <c:otherwise>
                            No answers
                        </c:otherwise>
                    </c:choose>
                </h2>
            </div>
        </div>
        <c:forEach var="answer" items="${question.as}" varStatus="questionStatus">
            <div id="answer-${questionStatus.index}" class="answer accepted-answer" data-answerid="28208672">
                <table>
                    <tbody>
                    <tr>
                        <td class="votecell">
                            <div class="${questionStatus.first? 'first-answer-mark':'other-answer-mark' }">
                                <%--<span itemprop="upvoteCount" class="vote-count-post ">0</span>--%>
                            </div>
                        </td>
                        <td class="answercell">
                            <div class="post-text">
                                ${answer.c}
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div id="comments" class="comments ">
                                <table>
                                    <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true"
                                           data-comments-unavailable="false" data-addlink-disabled="true">
                                    <c:forEach var="answerComment" items="${answer.cs}" varStatus="commentStatus">
                                        <tr id="comment-${commentStatus.index}" class="comment">
                                            <td>
                                                <div class="${commentStatus.first? 'first-comment-mark':'other-comment-mark' }">
                                                        <%--<span itemprop="upvoteCount" class="vote-count-post ">0</span>--%>
                                                </div>
                                            </td>
                                            <td class="comment-text">
                                                <div style="display: block;" class="comment-body">
                                                    <span class="comment-copy">${answerComment.c}</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </c:forEach>
    </div>
    <div id="relatedQuestion">
        <div id="relatedQuestion-header">
            <div class="subheader answers-subheader">
                <h2>
                    Related questions
                </h2>
            </div>
        </div>
        <c:forEach var="question" items="${relatedQuestions}" varStatus="questionStatus">
            <div class="question-summary" id="questions-summary-${questionsStatus.index}">
                <div class="related">
                    <a href="/questions/${question.id}/${question.title4url}" target="_blank">${question.t}</a>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<div id="sidebar" class="show-votes">
    <table>
        <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true" data-comments-unavailable="false" data-addlink-disabled="true">
        <tr id="transalateMsg" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <h2><spring:message code="translation.message" text="Double-click the word automatic translation!" /></h2>
                </div>
            </td>
        </tr>
        <tr id="recommendMsg" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <h2><spring:message code="recommend.message" text="Recommended books for you!" />
                        <c:choose>
                            <c:when test="${question.ts.size()>1}">
                                <a target="_blank"
                                   href="http://www.amazon.com/gp/search?ie=UTF8&camp=1789&creative=9325&index=books&keywords=${question.ts[0]}+${question.ts[1]}&linkCode=ur2&tag=goobbe-20&linkId=JKQVDSX6HBHBP5OI">Books</a>
                                <img src="http://ir-na.amazon-adsystem.com/e/ir?t=goobbe-20&l=ur2&o=1" width="1"
                                     height="1" border="0" alt=""
                                     style="border:none !important; margin:0px !important;"/>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${question.ts.size()>0}">
                                        <a target="_blank"
                                           href="http://www.amazon.com/gp/search?ie=UTF8&camp=1789&creative=9325&index=books&keywords=${question.ts[0]}&linkCode=ur2&tag=goobbe-20&linkId=JKQVDSX6HBHBP5OI">Books</a>
                                        <img src="http://ir-na.amazon-adsystem.com/e/ir?t=goobbe-20&l=ur2&o=1" width="1"
                                             height="1" border="0" alt=""
                                             style="border:none !important; margin:0px !important;"/>
                                    </c:when>
                                    <c:otherwise>
                                        <a target="_blank"
                                           href="http://www.amazon.com/gp/search?ie=UTF8&camp=1789&creative=9325&index=books&keywords=code&linkCode=ur2&tag=goobbe-20&linkId=JKQVDSX6HBHBP5OI">Books</a>
                                        <img src="http://ir-na.amazon-adsystem.com/e/ir?t=goobbe-20&l=ur2&o=1" width="1"
                                             height="1" border="0" alt=""
                                             style="border:none !important; margin:0px !important;"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                        <img src="http://ir-na.amazon-adsystem.com/e/ir?t=goobbe-20&l=ur2&o=1" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />
                    </h2>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
    <!-- Begin BidVertiser code -->
    <SCRIPT LANGUAGE="JavaScript1.1" SRC="http://bdv.bidvertiser.com/BidVertiser.dbm?pid=674767&bid=1685612" type="text/javascript"></SCRIPT>
    <!-- End BidVertiser code -->
    <!-- begin infolinks code -->
    <script type="text/javascript">
        var infolinks_pid = 2483710;
        var infolinks_wsid = 0;
    </script>
    <script type="text/javascript" src="http://resources.infolinks.com/js/infolinks_main.js"></script>
    <!-- end infolinks code -->
</div>
</div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>

