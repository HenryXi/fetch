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
<%--<div id="header">--%>
    <%--There should be logo pic--%>
<%--</div>--%>
    <script language="JavaScript">
        var url='/questions/${question.id}/${question.title4url}';
        window.history.replaceState(null, null, url);
        var showAd=0;
        function changeAnotherKeyword(){
            switch (showAd%3){
                case 0:
                    $('#ad1').hide();
                    $('#ad2').show();
                    $('#ad3').hide();
                    break;
                case 1:
                    $('#ad1').hide();
                    $('#ad2').hide();
                    $('#ad3').show();
                    break;
                case 2:
                    $('#ad1').show();
                    $('#ad2').hide();
                    $('#ad3').hide();
                    break;
            }
            showAd=showAd+1;
        }
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
                    <h2><spring:message code="recommend.message" text="Recommended books for you!" /><a onclick="changeAnotherKeyword()">(↔)</a></h2>
                </div>
            </td>
        </tr>
        <tr id="ad1" class="comment ">
            <td class="comment-text">
                <script charset="utf-8" type="text/javascript">
                    amzn_assoc_ad_type = "responsive_search_widget";
                    amzn_assoc_tracking_id = "goobbe-20";
                    amzn_assoc_link_id = "UYNFH7MH6HICAOSO";
                    amzn_assoc_marketplace = "amazon";
                    amzn_assoc_region = "US";
                    amzn_assoc_placement = "";
                    amzn_assoc_search_type = "search_widget";
                    amzn_assoc_width = "250";
                    amzn_assoc_height = "600";
                    amzn_assoc_default_search_category = "Books";
                    <c:choose>
                        <c:when test="${question.ts.size()>1}">
                            amzn_assoc_default_search_key = "${question.ts[0]} ${question.ts[1]}";
                        </c:when>
                        <c:otherwise>
                            amzn_assoc_default_search_key = "";
                        </c:otherwise>
                    </c:choose>
                    amzn_assoc_theme = "light";
                    amzn_assoc_bg_color = "3d495f";
                </script>
                <script src="//z-na.amazon-adsystem.com/widgets/q?ServiceVersion=20070822&Operation=GetScript&ID=OneJS&WS=1&MarketPlace=US"></script>
            </td>
        </tr>
        <tr id="ad2" class="comment" style="display: none">
            <td class="comment-text">
                <script charset="utf-8" type="text/javascript">
                    amzn_assoc_ad_type = "responsive_search_widget";
                    amzn_assoc_tracking_id = "goobbe-20";
                    amzn_assoc_link_id = "UYNFH7MH6HICAOSO";
                    amzn_assoc_marketplace = "amazon";
                    amzn_assoc_region = "US";
                    amzn_assoc_placement = "";
                    amzn_assoc_search_type = "search_widget";
                    amzn_assoc_width = "250";
                    amzn_assoc_height = "600";
                    amzn_assoc_default_search_category = "Books";
                    <c:choose>
                    <c:when test="${question.ts.size()>0}">
                    amzn_assoc_default_search_key = "${question.ts[1]}";
                    </c:when>
                    <c:otherwise>
                    amzn_assoc_default_search_key = "";
                    </c:otherwise>
                    </c:choose>
                    amzn_assoc_theme = "light";
                    amzn_assoc_bg_color = "3d495f";
                </script>
                <script src="//z-na.amazon-adsystem.com/widgets/q?ServiceVersion=20070822&Operation=GetScript&ID=OneJS&WS=1&MarketPlace=US"></script>
            </td>
        </tr>
        <tr id="ad3" class="comment" style="display: none">
            <td class="comment-text">
                <script charset="utf-8" type="text/javascript">
                    amzn_assoc_ad_type = "responsive_search_widget";
                    amzn_assoc_tracking_id = "goobbe-20";
                    amzn_assoc_link_id = "UYNFH7MH6HICAOSO";
                    amzn_assoc_marketplace = "amazon";
                    amzn_assoc_region = "US";
                    amzn_assoc_placement = "";
                    amzn_assoc_search_type = "search_widget";
                    amzn_assoc_width = "250";
                    amzn_assoc_height = "600";
                    amzn_assoc_default_search_category = "Books";
                    <c:choose>
                    <c:when test="${question.ts.size()>0}">
                    amzn_assoc_default_search_key = "${question.ts[0]}";
                    </c:when>
                    <c:otherwise>
                    amzn_assoc_default_search_key = "";
                    </c:otherwise>
                    </c:choose>
                    amzn_assoc_theme = "light";
                    amzn_assoc_bg_color = "3d495f";
                </script>
                <script src="//z-na.amazon-adsystem.com/widgets/q?ServiceVersion=20070822&Operation=GetScript&ID=OneJS&WS=1&MarketPlace=US"></script>
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

