<!DOCTYPE html>
<html>
<head>
    <title>Internal Server Error - ${host}</title>
</head>
<body style="font-family:Arial,Helvetica,sans-serif;">
<script language="JavaScript">
    var current=10;
    var timerId=window.setInterval(function(){
        if(current==1){
            clearInterval(timerId);
            window.location.replace('http://'+window.location.hostname);
        }
        current=current-1;
        document.getElementById('second').text=current;
    }, 1000);
</script>
<div style="margin: 0 auto; width: 960px;">
    <h2>We are sorry...</h2>
    <p>An error occurred when you request this page.</p>
    <p>We will solve this problem as soon as possible.</p>
    <p>This page will redirect to ${host} <a href="/">Home</a> in <a id="second">10</a> seconds</p>
</div>
</body>
</html>