<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>   
<%@ page import="javax.servlet.http.*"%>
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
</style>

</head>
<body>

<div id="topbar">
	<div id="leftnav">
		<a href="summary.jsp">Back</a></div>

	<div id="title">
		 Pickup & Deliver</div>
</div>
<div id="content">
    <form action="describe.do" method="post">
    	<fieldset>
    	<%
		if(request.getAttribute("error")!=null){
			out.print("<div><span class='redtitle'>Fields can not be empty!</span><br/><br/></div>");
		}
		%>
		
    	<span class="graytitle">Address & Building</span>
		<ul class="pageitem">
			<li class="bigfield"><input placeholder="Enter Address" type="text" name="address" value="5000 Forbes Ave, Pittsburgh" /></li>
		</ul>
		<span class="graytitle">Detail Description (bld, fl, room)</span>
		<ul class="pageitem">
			<li class="bigfield"><input placeholder="Enter Description" type="text" name="description" /></li>
		</ul>
		<span class="graytitle">How many items to deliver?</span>
		<ul class="pageitem">
			<li class="bigfield"><input placeholder="Enter Number" type="text" name="amount" /></li>
		</ul>
		<ul class="pageitem">
			<li class="button">
			<input name="Confirm" type="submit" value="Confirm"/></li>
		</ul>
		</fieldset>
	</form>
</div>
<div id="footer">
	<a class="noeffect">App powered by Coding Tartans</a></div>

</body>

</html>
