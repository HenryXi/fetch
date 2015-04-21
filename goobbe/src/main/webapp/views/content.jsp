<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html itemscope="" itemtype="">
<head>
    <title>${question.t}</title>
    <link rel="icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
    <link rel="shortcut icon" href="/pic/favicon.ico" mce_href="/favicon.ico" type="image/x-icon">
<body class="question-page new-topbar">
<jsp:include page="topbar.jsp"/>
<div class="container">
<%--<div id="header">--%>
    <%--There should be logo pic--%>
<%--</div>--%>
    <script language="JavaScript">
        var url='/questions/${question.id}/${question.title4url}';
        window.history.replaceState(null, null, url);
    </script>
<div id="content" class="snippet-hidden" onmouseup="translation()">
<div id="question-header">
    <h1 itemprop="name">${question.t}</h1>
</div>
<div id="mainbar">
    <div class="question" data-questionid="" id="question">
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
            <div id="answer-${questionStatus.index}" class="answer accepted-answer" data-answerid="28208672" itemscope=""
                 itemtype="" itemprop="acceptedAnswer">
                <table>
                    <tbody>
                    <tr>
                        <td class="votecell">
                            <div class="${questionStatus.first? 'first-answer-mark':'other-answer-mark' }">
                                <%--<span itemprop="upvoteCount" class="vote-count-post ">0</span>--%>
                            </div>
                        </td>
                        <td class="answercell">
                            <div class="post-text" itemprop="text">
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
        <%--<tr id="transalateMsg" class="comment ">--%>
            <%--<td class="comment-text">--%>
                <%--<div style="display: block;" class="comment-body">--%>
                    <%--<h2><spring:message code="translation.message" text="default text" /></h2>--%>
                <%--</div>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr id="comment1" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <span><code>Comments</code> will be put here</span>
                </div>
            </td>
        </tr>
        <tr id="comment2" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                            <span class="comment-copy"><code>Comments</code> will be put here</span>
                </div>
            </td>
        </tr>
        <tr id="comment3" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <span class="comment-copy"><code>Comments</code> will be put here</span>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>
</div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>

