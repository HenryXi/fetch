<%--todo: translation function, using js is the best way--%>
<%--todo: similar question list in content page --%>
<%--todo: use my own proxy to get google result--%>
<div class="topbar">
    <script src="http://cdn.bootcss.com/jquery/1.9.0/jquery.js"></script>
    <div class="topbar-wrapper">
        <h1 class="logo">
            <a href="/">Goobbe</a>
        </h1>
        <div class="search-container">
            <form id="search" action="/search" method="get" autocomplete="off">
                <input name="q" type="text" placeholder="search" value="${keyword}" tabindex="1" autocomplete="off"
                       maxlength="240">
                <button type="submit" class="btn">Search</button>
            </form>
        </div>
        <%--<div class="topbar-links">--%>
            <%--<div class="links-container">--%>
                    <%--<span class="topbar-menu-links">--%>
                            <%--<a href="#" class="login-link">sign up</a>--%>
                            <%--<a href="#" class="login-link">log in</a>--%>
                    <%--</span>--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>
</div>