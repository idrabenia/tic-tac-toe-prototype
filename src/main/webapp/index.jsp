<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.8.3.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd.js"></script>
    <link rel="stylesheet" type="text/css" href="styles.css" />
    <script type="text/javascript" src="application.js"></script>
    <%--
    The reason to use a JSP is that it is very easy to obtain server-side configuration
    information (such as the contextPath) and pass it to the JavaScript environment on the client.
    --%>
    <script type="text/javascript">
        var config = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
</head>
<body>

<div id="body"></div>

<%--<form action="">--%>
    <%--<div id="newsTextBox">--%>
        <%--<label>Publish your news: </label>           <br />--%>
        <%--<textarea rows="10" cols="40"></textarea>    <br />--%>
        <%--<input type="button" value="Publish" />--%>
    <%--</div>--%>
<%--</form>--%>

<div id="gameInfoBlock">
    State: <span class="state"></span> <br />
    Player1: <span class="player1"></span> <br />
    Player2: <span class="player2"></span> <br />
    Next Step Player: <span class="nextStepPlayer"></span> <br />
    Winner: <span class="winner"></span>
</div>

<div id="joinGameBlock">
    Name:
    <input value="" />
    <button>Join</button>
</div>

<table id="gameTable">
    <tr>
        <td data-number="1">&nbsp; </td>
        <td data-number="2">&nbsp; </td>
        <td data-number="3">&nbsp; </td>
    </tr>
    <tr>
        <td data-number="4">&nbsp; </td>
        <td data-number="5">&nbsp; </td>
        <td data-number="6">&nbsp; </td>
    </tr>
    <tr>
        <td data-number="7">&nbsp; </td>
        <td data-number="8">&nbsp; </td>
        <td data-number="9">&nbsp; </td>
    </tr>
</table>


</body>
</html>
