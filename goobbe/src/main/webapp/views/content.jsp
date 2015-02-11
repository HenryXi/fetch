<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html itemscope="" itemtype="">
<head>

    <title>${question.title}</title>
    <link rel="stylesheet" type="text/css" href="/css/all.css">
    <%--<script src="http://cdn.bootcss.com/jquery/1.9.0/jquery.js"></script>--%>
<body class="question-page new-topbar">
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
<div id="question-header">
    <h1 itemprop="name"><a href="#"
                           class="question-hyperlink">${question.title}</a></h1>
</div>
<div id="mainbar">
    <div class="question" data-questionid="" id="question">
        <table>
            <tbody>
            <tr>
                <td class="postcell">
                    <div>
                        <div class="post-text" itemprop="text">
                            ${question.content}
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div id="comments-28208462" class="comments ">
                        <table>
                            <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true"
                                   data-comments-unavailable="false" data-addlink-disabled="true">
                            <c:forEach var="comment" items="${question.comments}">
                                <tr id="comment-44781745" class="comment ">
                                    <td>
                                        <table>
                                            <tbody>
                                            <tr>
                                                <td class=" comment-score">
                                                    &nbsp;&nbsp;
                                                </td>
                                                <td>
                                                    &nbsp;
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </td>
                                    <td class="comment-text">
                                        <div style="display: block;" class="comment-body">
                                            <span class="comment-copy">${comment.content}</span>
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
                    Answers
                </h2>
            </div>
        </div>
        <c:forEach var="answer" items="${question.answers}">
            <div id="answer-28208672" class="answer accepted-answer" data-answerid="28208672" itemscope=""
                 itemtype="" itemprop="acceptedAnswer">
                <table>
                    <tbody>
                    <tr>
                        <td class="answercell">
                            <div class="post-text" itemprop="text">
                                ${answer.content}
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="1" class="comments ">
                                <table>
                                    <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true"
                                           data-comments-unavailable="false" data-addlink-disabled="true">
                                    <c:forEach var="answerComment" items="${answer.comments}">
                                        <tr id="4" class="comment ">
                                            <td class="comment-text">
                                                <div style="display: block;" class="comment-body">
                                                    <span class="comment-copy">${answerComment.content}</span>
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
</div>

<div id="sidebar" class="show-votes">
    <table>
        <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true" data-comments-unavailable="false" data-addlink-disabled="true">
        <tr id="6" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <span class="comment-copy">These three <code>alert</code>s will be executed <b>before</b> your <code>tempLogoGetWH</code> callback function, therefore valules of width and height will be undefined. Move them at the very bottom of <code>tempLogoGetWH</code> and everything will be fine.</span>
                </div>
            </td>
        </tr>
        <tr id="7" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                            <span class="comment-copy">This is happening because your image load <code>tempLogoGetWH</code>  is <b>not</b>  executed and you are trying to alert <code>height,
                                width</code> <b>before</b> it executes.</span>
                </div>
            </td>
        </tr>
        <tr id="8" class="comment ">
            <td class="comment-text">
                <div style="display: block;" class="comment-body">
                    <span class="comment-copy">That explains a lot, works now! Thank you, guys! OT: How do I set this as "asnwered" now?</span>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
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

