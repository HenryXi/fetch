<!DOCTYPE html>
<html>
<head>
    <title>Page Not Found - Goobbe</title>
</head>
<body style="font-family:Arial,Helvetica,sans-serif;">
<script language="JavaScript">
    var current=10;
    var timerId=window.setInterval(function(){
        if(current==1){
            clearInterval(timerId);
            window.location.replace('http://goobbe.com');
        }
        current=current-1;
        document.getElementById('second').text=current;
    }, 1000);
</script>
<div style="margin: 0 auto; width: 960px;">
    <h2>Page Not Found(404) Error</h2>
    <p>We couldn't find the page you requested.This page will redirect to Goobbe <a href="/">Home</a> in <a id="second">10</a> seconds</p>
    <p>If you feel something is missing that should be here, <a href="mailto:goobbe@163.com">contact us</a>.</p>
</div>
</body>
</html>