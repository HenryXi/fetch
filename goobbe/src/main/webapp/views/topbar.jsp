<div class="topbar">
    <script src="http://cdn.bootcss.com/jquery/1.10.2/jquery.js"></script>
    <script src="http://cdn.bootcss.com/jqueryui/1.10.2/jquery-ui.min.js"></script>
    <script src="/js/nprogress.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/all.css">
    <link rel="stylesheet" type="text/css" href="http://cdn.bootcss.com/jqueryui/1.10.2/css/smoothness/jquery-ui-1.10.2.custom.min.css">
    <script language="JavaScript">
        function translation(){
            var target="";
            if (window.getSelection) {  // all browsers, except IE before version 9
                var range = window.getSelection ();
                target=range.toString ();
            } else {
                if (document.selection.createRange) { // Internet Explorer
                    var range = document.selection.createRange ();
                    target=range.text;
                }
            }
            var translate= $("#translate");
            if(target!=""){
                translate.html('');
                translate.append("<p>"+target+"</p>").append("<p>"+target+"</p>").append("<p>"+target+"</p>").append("<p>"+target+"</p>").append("<p>"+target+"</p>");
                translate.dialog({
                    position:{my: "left+10 bottom-10", of: event, collision: "fit"},
                    maxWidth: 300
                });
            }else{
                translate.dialog("close");
            }
        }
        function search(){
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
                        $('#questions').append("<div class=\"question-summary\" id=\"question-summary-"+index+"\"><div class=\"summary\"><h3><a href=\"/question/"+question.url+"\" class=\"question-hyperlink\" target=\"_blank\">"+question.t+"</a></h3><div class=\"excerpt\">"+question.c+"</div></div></div>");
                    });
                }
            });
        }
    </script>
    <div id="translate" title="Basic dialog">
        <p>This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
    </div>
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