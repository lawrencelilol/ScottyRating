# ScottyRating

## Problem:
Carnegie Mellon University does not have app for students to look up courses ratings with efficiency. 

## Task:
Develop App for students to search course ratings in the past and help students select courses

## Design:

![image](https://user-images.githubusercontent.com/35508198/168653518-1a766bf1-e379-485f-b44a-cea5e2c7da07.png)

## Part 1:

Moblie to Cloud Application:

<img width="1009" alt="image" src="https://user-images.githubusercontent.com/35508198/168653885-c744fb0e-bf38-4833-903c-180e6c6d1087.png">

My application takes a search string for a class from the user and uses it to fetch and display ratings of the class from Scotty Labs. (https://apis.scottylabs.org/docs)

Here is how my application meets the task requirements 

1. Implement a native Android application

The name of my native Android application project in Android Studio is: Project4Android

1.1. Has at least three different kinds of views in your Layout (TextView, EditText, ImageView, etc.) 

My application uses TextView, EditText, Button, ImageView, and RatingBar. See content_main.xml for details of how they are incorporated into the LinearLayout

Here is a screenshot of the layout before the rating has been fetched.

<img width="188" alt="image" src="https://user-images.githubusercontent.com/35508198/168654414-a887bf15-2c0a-46ec-82a2-ea0a934a5f35.png">

1.2.  Requires input from the user

Here is a screenshot of the user searching for a course with id 17-683

<img width="230" alt="image" src="https://user-images.githubusercontent.com/35508198/168654498-0696ae50-bd9b-4fa5-a149-e0ec01e81dcb.png">


1.3. Makes an HTTP request (using an appropriate HTTP method) to your web service 

My application does an HTTP GET request in GetClass.java. The HTTP request is:
"https://sleepy-basin-06882.herokuapp.com/getAClass?class-id=" + searchID;

Where search is the user’s input to search the course.

The search method makes this request of my web application, parses the returned XML to find the course URL, fetches the course rating, and returns the JSON object as a String.

1.4. Receives and parses an XML or JSON  formatted reply from your web service
An example of the JSON reply is :
{"rating":[4.95,4.94,4.86]}

1.5. Displays new information to the user
Here is the screenshot after the course rating has been returned.

<img width="216" alt="image" src="https://user-images.githubusercontent.com/35508198/168654995-5e9ead68-e398-4818-80ff-f7f20f2fe5d2.png">

2. Implement a web service, deployed to Heroku
The URL of my web service deployed to Heroku is:

sleepy-basin-06882

2.1.Implement a simple (can be a single path) API.
In my web app project:
Model: ClassFinderModel.java
View: result.jsp
Controller: ClassFinderServlet.java

2.2 Receives an HTTP request from the native Android application
ClassFinderModel.java receives the HTTP GET request with the argument “class-id”. It passes this search string onto the model with the doClassSearch function.

2.3 Executes business logic appropriate to your application

ClassFinderModel.java makes an HTTP GET request to:

“https://course.apis.scottylabs.org/fces?courseID=” + searchID + “&year=2021&semester=fall”

(We are just using FCES data from Fall 2021 for now)

It then parses the JSON response and extracts the parts it needs to respond to the Android application.

2.4.  Replies to the Android application with an XML or JSON formatted response. 

The formatted response to the mobile application in a JSON format of my own design.

{"rating":[4.95,4.94,4.86]}

It includes the rating for the searched courses:
0. Show respect for all students
1. Overall teaching rate
2. Overall course rate







