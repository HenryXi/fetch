<div class="topbar">
    <script src="http://cdn.bootcss.com/jquery/1.10.2/jquery.js"></script>
    <script src="/js/nprogress.js"></script>
    <link id="css" rel="stylesheet" type="text/css" href="/css/all.css">
    <script language="JavaScript">
        function search(){
            var q=$('#search').val().trim();
            if(q.length==0){
                return;
            }
            if($.isNumeric(q)){
                window.location.href="/question/"+q;
                return;
            }
            NProgress.start();
            var questions=$('#questions');
            if(questions.length==0){
                $('#sidebar').remove();
                $('#question-header').remove();
                $('#mainbar').html("<div class=\"subheader\"><h1 id=\"allQuestionsHeader\">Search result</h1></div><div id=\"questions\"></div>");
            }
            $('#search').blur();
            $('#pagination').remove();
            var q=$('#search').val().trim();
            document.title=q+"_Goobbe search result";
            questions.html('');
            $.ajax({
                url: "/search?q="+encodeURIComponent(q),
                success: function(data){
                    NProgress.done();
                    $('#allQuestionsHeader').html("Search result");
                    jQuery.parseJSON(data).forEach(function(question,index){
                        $('#questions').append("<div class=\"question-summary\" id=\"question-summary-"+index+"\"><div class=\"summary\"><h3><a href=\"/question/"+question.url+"\" class=\"question-hyperlink\">"+question.t+"</a></h3><div class=\"excerpt\">"+question.c+"</div></div></div>");
                    });
                }
            });
        }
    </script>
    <div class="topbar-wrapper">
        <h1 class="logo">
            <a href="/">Goobbe</a>
        </h1>
        <div class="search-container">
            <input id="search" name="q" type="text" placeholder="Tab focus here, Enter search you want" tabindex="1" autocomplete="off"
                   maxlength="240" onkeydown="if (event.keyCode == 13) search()">
            <button type="button" class="btn" onclick="search()">Search</button>
        </div>
    </div>
</div>