<%--
* Author: Lawrence Li
* Last Modified: April 6, 2022
*
*
*
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Project 4 Task 2</title>
</head>
<body>
<h1><%= "Welcome to CMU Course Rating Finder" %>
</h1>
<h3>
    Created By Lawrence Li
</h3>

<h2>
    Courses Finder
</h2>

<h3>
    Choose a course
</h3>

<%--This is the form to send the users input to ClassFinder Servlet--%>
<form method="GET" action="getAClass">
    <p>
        <input type = "text" name = "class-id" placeholder="type the class id"/>
    </p>
    <%-- Submit button to send users' input to ClassFinder Servlet --%>
    <a href="getAClass">
        <button class ="submit">Submit</button>
    </a>
</form>

<a href="goDashboard">
    <button class ="dashboard">Go to Log Dashboard</button>
</a>

</body>
</html>