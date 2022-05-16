<%@ page import="org.bson.Document" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Comparator" %>

<%--
* Author: Lawrence Li
* Last Modified: April 9th, 2022
*
* It contains the title and information of the log Android App
*
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
<link href="/twitter-bootstrap/twitter-bootstrap-v2/docs/assets/css/bootstrap.css" rel="stylesheet">
<head>
    <title>Course Rating Log</title>
    <style>
        table {
            border-collapse: collapse;
            border-spacing:3px;
            text-align: center;
        }
        tr {
            border: solid;
            border-width: 1px 0;
        }
        td {
            border-right: solid 1px;
            border-left: solid 1px;
        }
        th {
            border-right: solid 1px;
            border-left: solid 1px;
        }
    </style>
</head>
<body>
<%List<Document> logs = (List<Document>) request.getAttribute("Logs");%>
<%HashMap<String,Integer> countCourse =  new HashMap<>();%>
<%HashMap<String,Integer> countDevice =  new HashMap<>();%>
<%int totalLatency = 0;%>
<%int count = 0;%>

<h1><%= "Welcome to CMU Course Rating Data Log!" %></h1>
<h3>
    Created By Lawrence Li
</h3>

<div>
    <h2>
        Data Log Dashboard
    </h2>
    <h3>
        Log from MongoDB
    </h3>
    <table class = "table">
        <thead>
        <tr>
            <th>#</th>
            <th>User Device</th>
            <th>Search CourseID</th>
            <th>Response to User</th>
            <th>API Request</th>
            <th>Request Time</th>
            <th>API ProcessTime(ms)</th>
        </tr>
        </thead>
        <tbody>
        <% for (Document log: logs) { %>
        <% count++;
        String courseID = log.getString("CourseID");
        String device = log.getString("UserDevice");
        totalLatency += Integer.parseInt(log.getString("processTime"));

        // append data to the countCourse Map
            if(courseID.contains("-")) {
                String id = courseID.substring(0,2) + courseID.substring(3);
                courseID = id;
            }
            if(!countCourse.containsKey(courseID)) {
                countCourse.put(courseID,1);
            } else {
                countCourse.put(courseID,countCourse.get(courseID)+1);
            }

            // append data to the countDevice Map
            if(!countDevice.containsKey(courseID)) {
                countDevice.put(device,1);
            } else {
                countDevice.put(device,countDevice.get(device)+1);
            }

            // sort countCourse Map
            countCourse.entrySet()
                    .stream()
                    .limit(3)
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> countCourse.put(x.getKey(), x.getValue()));

            // sort countDevice Map
            countDevice.entrySet()
                    .stream()
                    .limit(2)
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> countDevice.put(x.getKey(), x.getValue()));
        %>
        <tr>
            <td><%=count %></td>
            <td><%=log.getString("UserDevice")%></td>
            <td><%=log.getString("CourseID")%> </td>
            <td><%=log.getString("RatingFromScotty")%> </td>
            <td><%=log.getString("requestAPI")%> </td>
            <td><%=log.getString("receiveTime")%> </td>
            <td><%=log.getString("processTime")%> </td>
            <% } %>
        </tr>
        </tbody>
    </table>
    <h3>Operation Analytics</h3>
        <p>The average latency of search a course: <%=totalLatency/count%></p>
        <p>Top 2 devices makes requests</p>
        <ul>
            <%for(String device: countDevice.keySet()) {%>
            <li><%=device%></li>
            <%}%>
        </ul>
        <p>Top 3 courses searched the most</p>
        <ul>
            <%for(String courseID: countCourse.keySet()) {%>
            <li><%=courseID%></li>
            <%}%>
        </ul>
</div>


</br>
<%--continue to choose another breed--%>
<a href="index.jsp">
    <button class ="submit">Continue</button>
</a>


</body>

</html>
