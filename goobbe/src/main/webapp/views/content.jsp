<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html itemscope="" itemtype="">
<head>

    <title>6</title>
    <link rel="stylesheet" type="text/css" href="../css/all.css">
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
                           class="question-hyperlink">Getting the image size using the new Image() returns 0's</a></h1>
</div>
<div id="mainbar">
    <div class="question" data-questionid="" id="question">
        <table>
            <tbody>
            <tr>
                <td class="postcell">
                    <div>
                        <div class="post-text" itemprop="text">
                            <p>I'm trying to load a image from the input text path and set its size for previewing.</p>
                            <p>So I have this object to process the settings:</p>
<pre><code>var Settings = {
    // some other variables
    settLogoLeftVisibility: "true",
    settLogoLeftPath: "OS_logo.jpg",
    settLogoRightVisibility: "true",
    settLogoRightPath: "OS_logo.jpg",
    // some more variables
    load: function() {
    // load the settings from localStorage, or load the defaults
    // set the image path and resize it
    var tempLogoLeftWidth;
    var tempLogoLeftHeight;

    function tempLogoGetWH() {
    tempLogoLeftWidth = this.width;
    tempLogoLeftHeight = this.height;
    return true;
    }
    tempLogoLeft = new Image();
    tempLogoLeft.onload = tempLogoGetWH;

    if (localStorage.settLogoLeftPath) {
    tempLogoLeft.src = "../logos/" + localStorage.settLogoLeftPath;
    }
    else {
    tempLogoLeft.src = "../logos/" + this.settLogoLeftPath;
    }
    alert(tempLogoLeft.src);
    alert(tempLogoLeftWidth);
    alert(tempLogoLeftHeight);
    }
    }
</code></pre>
                            <p>Thing is, it alerts the correct path, but both tempLogoLeftWidth and tempLogoLeftHeight are alerted
                                as undefined, so I cant process them further and set the sizes I need.</p>
                            <p>Edit: I call the Settings.load(); at the very end of my settings.html page, like this:</p>
<pre><code>// some tables, inputs and buttons
    &lt;td&gt;Left logo preview&lt;br&gt;
    &lt;img class="settingsLogo" id="settingsLogoLeft"&gt;&lt;/td&gt;
    &lt;td&gt;&lt;input type="checkbox" id="settLogoLeftVisibility" value="true" onChange="Settings.preview();"&gt;
    Enable left logo&lt;br&gt;
    &lt;input type="text" id="settLogoLeftPath" size="20" onChange="Settings.preview();"&gt; File name (logos/x.yyy)&lt;br&gt;
    &lt;script&gt;
    Settings.load();
    Settings.preview();
    &lt;/script&gt;
    &lt;/body&gt;
</code></pre>
                            <p>There are no errors in the Chrome JS Console.</p>
                            <p>Any ideas?
                                Thank you very much for your time!</p>
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
                            <tr id="comment-44781745" class="comment ">
                                <td class="comment-text">
                                    <div style="display: block;" class="comment-body">
                                        <span class="comment-copy">These three <code>alert</code>s will be executed <b>before</b> your <code>tempLogoGetWH</code> callback function, therefore valules of width and height will be undefined. Move them at the very bottom of <code>tempLogoGetWH</code> and everything will be fine.</span>
                                    </div>
                                </td>
                            </tr>
                            <tr id="comment-44781846" class="comment ">
                                <td class="comment-text">
                                    <div style="display: block;" class="comment-body">
                            <span class="comment-copy">This is happening because your image load <code>tempLogoGetWH</code>  is <b>not</b>  executed and you are trying to alert <code>height,
                                width</code> <b>before</b> it executes.</span>
                                    </div>
                                </td>
                            </tr>
                            <tr id="comment-44782031" class="comment ">
                                <td class="comment-text">
                                    <div style="display: block;" class="comment-body">
                                        <span class="comment-copy">That explains a lot, works now! Thank you, guys! OT: How do I set this as "asnwered" now?</span>
                                    </div>
                                </td>
                            </tr>
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
        <div id="answer-28208672" class="answer accepted-answer" data-answerid="28208672" itemscope=""
             itemtype="" itemprop="acceptedAnswer">
            <table>
                <tbody>
                <tr>
                    <td class="answercell">
                        <div class="post-text" itemprop="text">
                            <p>This works ok</p>
                            <div class="snippet" data-lang="js" data-hide="false">
                                <div class="snippet-code">
<pre class="snippet-code-js lang-js prettyprint-override"><code>var Settings = {
    // some other variables
    settLogoLeftVisibility: "true",
    settLogoLeftPath: "OS_logo.jpg",
    settLogoRightVisibility: "true",
    settLogoRightPath: "OS_logo.jpg",
    // some more variables
    load: function() {
    // load the settings from localStorage, or load the defaults
    // set the image path and resize it
    var tempLogoLeftWidth;
    var tempLogoLeftHeight;

    function tempLogoGetWH() {
    tempLogoLeftWidth = this.width;
    tempLogoLeftHeight = this.height;
    alert(tempLogoLeft.src);
    alert(tempLogoLeftWidth);
    alert(tempLogoLeftHeight);
    return true;
    }
    tempLogoLeft = new Image();
    tempLogoLeft.onload = tempLogoGetWH;
    //this is not important I just put anything to make it work the key is where alerts are
    if (localStorage.settLogoLeftPath) {
    tempLogoLeft.src = 'http://cdn.sstatic.net/img/favicons-sprite16.png?v=b8d3dc7bcd05e3ba58eb1089d4838b9c';
    }
    else {
    tempLogoLeft.src = "http://cdn.sstatic.net/img/favicons-sprite16.png?v=b8d3dc7bcd05e3ba58eb1089d4838b9c";
    }

    }
    }</code></pre>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div id="1" class="comments ">
                            <table>
                                <tbody data-remaining-comments-count="0" data-canpost="false" data-cansee="true"
                                       data-comments-unavailable="false" data-addlink-disabled="true">
                                <tr id="4" class="comment ">
                                    <td class="comment-text">
                                        <div style="display: block;" class="comment-body">
                                            <span class="comment-copy">These three <code>alert</code>s will be executed <b>before</b> your <code>tempLogoGetWH</code> callback function, therefore valules of width and height will be undefined. Move them at the very bottom of <code>tempLogoGetWH</code> and everything will be fine.</span>
                                        </div>
                                    </td>
                                </tr>
                                <tr id="2" class="comment ">
                                    <td class="comment-text">
                                        <div style="display: block;" class="comment-body">
                            <span class="comment-copy">This is happening because your image load <code>tempLogoGetWH</code>  is <b>not</b>  executed and you are trying to alert <code>height,
                                width</code> <b>before</b> it executes.</span>
                                        </div>
                                    </td>
                                </tr>
                                <tr id="3" class="comment ">
                                    <td class="comment-text">
                                        <div style="display: block;" class="comment-body">
                                            <span class="comment-copy">That explains a lot, works now! Thank you, guys! OT: How do I set this as "asnwered" now?</span>
                                        </div>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
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

