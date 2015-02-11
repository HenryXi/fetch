<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>2</title>
    <link rel="stylesheet" type="text/css" href="css/all.css">
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
                <script language="JavaScript">

                </script>
                <c:forEach var="listValue" items="${list}">
                    ${listValue}
                </c:forEach>
                <div class="question-summary" id="question-summary-28208463">
                    <div class="summary">
                        <h3>
                            <a href="/content?id=2" class="question-hyperlink">${listValue}</a>
                        </h3>

                        <div class="excerpt">
                            I am using Mockito+TestNG framework in my Project. It is used to work fine but to mock some
                            static methods I
                            injected PowerMock Libraries in my classpath and updated the code to use PowerMock. I am ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208462">
                    <div class="summary">
                        <h3><a href="/content?id=2"
                               class="question-hyperlink">Getting the image size using the new Image() returns 0's</a>
                        </h3>

                        <div class="excerpt">
                            I'm trying to load a image from the input text path and set its size for previewing.

                            So I have this object to process the settings:

                            var Settings = {
                            // some other variables
                            ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208460">
                    <div class="summary">
                        <h3>
                            <a href="#"
                               class="question-hyperlink">SSLKeyException when connecting to server that indicates
                                'certificate chain
                                path length in the basic constraints that was exceeded'</a></h3>

                        <div class="excerpt">
                            •WebLogic server is giving a SSLKeyException: [Security:090550] error.

                            I am trying to invoke a webservice but i am getting the below exception:

                            Failed to invoke com.service:

                            ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208458">
                    <div class="summary">
                        <h3><a href="/content?id=2"
                               class="question-hyperlink">Open only non-zero text files from a folder using batch
                            script</a></h3>

                        <div class="excerpt">
                            How to open all the text files (.txt) In a directory , which have some data in it (size not
                            0 ) using batch
                            script.

                            Thanks in advance.

                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208457">
                    <div class="summary">
                        <h3><a href="/content?id=2" class="question-hyperlink">Retrieving
                            a TextBox in a GridView in ASP.NET</a></h3>

                        <div class="excerpt">
                            I have the following RowDataBound method for GridView2

                            protected void GridView2_RowDataBound(object sender, GridViewRowEventArgs e)
                            {
                            if (e.Row.RowType == ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208456">
                    <div class="summary">
                        <h3><a href="/content?id=2" class="question-hyperlink">JQUERY -
                            OnClick hide div and show another.</a></h3>

                        <div class="excerpt">
                            Trying to hide one (1) div and show another on button click. However, not getting it to act
                            like I want.

                            HTML

                            &lt;div class="license-saved"&gt;
                            &lt;b&gt;DOB: ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208454">
                    <div class="summary">
                        <h3><a href="/content?id=2" class="question-hyperlink">sgetn
                            “overwrites” a const parameter</a></h3>

                        <div class="excerpt">


                            std::vector&lt;std::bitset&lt;8&gt;&gt;&amp; vecBin


                            If vecBin has 4 elements or more, the following situation happens:


                            bool BinFinder::containsInFile(const ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208451">
                    <div class="summary">
                        <h3><a href="/content?id=2" class="question-hyperlink">How to update variables in .less file dynamically using
                            AngularJS</a></h3>

                        <div class="excerpt">
                            I am very much new to AngularJS. I want to update the .less files variable dynamically. But
                            didn't get how
                            to access this .less file using AngularJS.

                            My code:

                            style.less

                            @bg-color: #484848;
                            ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208450">
                    <div class="summary">
                        <h3><a href="/content?id=2"
                               class="question-hyperlink">input control in ireport should accept only numerical
                            values</a></h3>

                        <div class="excerpt">
                            I have added one input control Days which should accept only numerical values. When i pass
                            any alphabetical
                            values it should not accept. I have tried with text field it is working fine but will ...
                        </div>
                    </div>
                </div>
                <div class="question-summary" id="question-summary-28208447">
                    <div class="summary">
                        <h3><a href="/content?id=2" class="question-hyperlink">Three.js
                            polyhedron name or title</a></h3>

                        <div class="excerpt">
                            Here is a sample code of my page. I want to get name of object, when user clicks it. And it
                            works well with
                            cube or sphere, but fails with polyhedron.
                            You may watch console logs to see clicks ...
                        </div>
                    </div>
                </div>
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
