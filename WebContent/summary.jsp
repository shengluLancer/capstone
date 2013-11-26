<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>   
<%@page import="javax.servlet.http.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="index,follow" name="robots" />
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<link href="pics/homescreen.gif" rel="apple-touch-icon" />
<meta content="minimum-scale=1.0, width=device-width, maximum-scale=0.6667, user-scalable=no" name="viewport" />
<link href="css/style.css" rel="stylesheet" media="screen" type="text/css" />
<script src="javascript/functions.js" type="text/javascript"></script>
<script src="javascript/jquery.js" type="text/javascript" charset="utf-8"></script>
<script src="javascript/jquery.stayInWebApp.js" type="text/javascript"></script>
<script src="javascript/stay.js" type="text/javascript"></script>

<title>Order Delivery Summary</title>
<style> 
.div-a{ float:left;width:40%} 
.div-b{ float:left;width:25%}
.div-c{ float:left;width:47%}

.div-d{ float:left;width:70%}
.div-e{ float:left;width:30%}

.div-f{ clear:both}

</style>
</head>


<body>
<%
String status = "delivered";

String message;
if(status.equals("delivered"))
	message = "Get delivered now (in 30 min)";
else
	message = "Pick up now";
%>

<div id="topbar">
	<div id="title">
		 Order Summary</div>
</div>
<div id="content">
    <form action="summary.do" method="post">
    
    	<%for (int i =0; i < 2; i++){%>
    	
    	<div class="div-d">
    	<ul class="pageitem">
		<li class="textbox"><span class="header"> <font size="5"> Little Asia </font></span>	
		<p><%=message%></p><br></li>
		<li> <div class="div-a" style="margin-left:10%;font-size:15pt"> Sushi Box</div> <div class="div-b" style="font-size:15pt">1</div>  <div class="div-b" style="font-size:15pt"> $10 </div> </li>
		<li > <div class="div-a" style="margin-left:10%;font-size:15pt">Subtotal </div><div class="div-c" style="margin-right:2%;font-size:15pt"> $10 </div> </li> 
	    </ul>
	    </div>
	    
	    <div class="div-e">
	    <%if (status.equals("pickup")){%>
	    <span class="graytitle">Deliver for others?</span>
	    <ul class="pageitem">
			<li class="radiobutton"><span class="name">Yes</span>
			<input name="delivertype" type="radio" value="forOthers" checked="yes"/></li>
			<li class="radiobutton"><span class="name">No</span>
			<input name="delivertype" type="radio" value="forSelf" /></li>
		</ul>
		<%}%>
		<%if (status.equals("delivered")){%>
		<ul class="pageitem">
			<li class="button">
			<input name="viewDetails" type="submit" value="View Details"/></li>
		</ul>
		<%}%>
		</div>
		
		<%}%>
		
		<div class="div-f">
		<ul class="pageitem">
			<li class="button">
			<input name="summarySubmit" type="submit" value="Submit"/></li>
		</ul>
		</div>
		<ul class="pageitem">
			<input type="hidden" name="status" value=<%=status%>>
		</ul>
	</form>
</div>
<div id="footer">
	<a class="noeffect">App powered by Coding Tartans</a></div>

</body>

</html>
