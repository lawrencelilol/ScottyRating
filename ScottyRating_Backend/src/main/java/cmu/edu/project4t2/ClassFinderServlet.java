/**
 * author: Tiantong Li (tiantonl)
 * Last modified: April 10th, 2022
 */

package cmu.edu.project4t2;

import com.google.gson.JsonObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "findAClass", urlPatterns = "/getAClass")
public class ClassFinderServlet extends HttpServlet {
    private ClassFinderModel cfm = null; // The "business model" for this app
    private MongoDatabase logDB;
    private final String passwd = "lawrencelilol:LL2619970206ltt";

    public void init() {
        // Create Class Finder Model
        cfm = new ClassFinderModel();

        // Connect to MongoDB
        // creat the personalized connection string
        String s = "mongodb://" + passwd +"@cluster0-shard-00-02.lswpu.mongodb.net:27017," +
                " cluster0-shard-00-01.lswpu.mongodb.net:27017," +
                "cluster0-shard-00-00.lswpu.mongodb.net:27017" +
                "/AndroidDB?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
        // set up the setting to connect with MongoDB
        ConnectionString connectionString = new ConnectionString(s);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);

        // Get the android data log database from MongoDB
        logDB = mongoClient.getDatabase("AndroidDB");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject ratings;

        // Get the classID from android input
        String classID = request.getParameter("class-id");

        // check to see if the course id is correct
        if(classID == null || classID.length() == 0) {
            System.out.println("empty course id");
            return;
        } else if(classID.length() > 6 || classID.length() < 5) {
            System.out.println("Course id with invalid length");
            return;
        }
        // time the start of fetching data from Scotty Labs
        long start = System.currentTimeMillis();

        // use the Scotty Lab API to get the rating for the searched class
        ratings = cfm.doClassSearch(classID);

        // time end of fetching data from Scotty Labs
        long end = System.currentTimeMillis();

        // send the data back to android
        PrintWriter out = response.getWriter();
        out.println(ratings);
        out.flush();
        // get the latency time
        String processTime = String.valueOf(end - start);
        // store data in mongobd
        LogData(request,classID,ratings.toString(),processTime);
    }

    /**
     * Storing the log data to MongoDB
     * @param request http request
     * @param classID courseID
     * @param ratings rating
     * @param processTime time takes to fetch data from API
     */
    private void LogData(HttpServletRequest request, String classID,String ratings, String processTime) {

        // keep track useful data to put in the dashboard
        String ua = request.getHeader("User-Agent");
        // get the phone model of the user
        String model = "";

        // reference to the InterestingPicture Project in Lab3
        if(ua != null && (ua.contains("Android") || ua.contains("iPhone"))){
            if(ua.contains("Android")) {
                model = ua.substring(ua.indexOf("Android"), ua.indexOf(")"));
            } else {
                model = ua.substring(ua.indexOf("iPhone"), ua.indexOf(")"));
            }
        } else {
            model = "Desktop";
        }

        // get time when the request is received
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String receiveTime = formatter.format(date);


        // the get request to the 3rd party api
        String api = "https://course.apis.scottylabs.org/fces?courseID="
                + classID +
                "&year=2021&semester=fall";

        // import data log to MongoDB
        importData(model,classID,api,ratings,receiveTime,processTime);

    }

    // This function imports data into MongoDB
    private void importData(String device, String courseID, String api,
                            String rating, String receiveTime, String processTime) {
        Document doc = new Document();
        doc.append("UserDevice",device);
        doc.append("CourseID",courseID);
        doc.append("RatingFromScotty",rating);
        doc.append("requestAPI",api);
        doc.append("receiveTime",receiveTime);
        doc.append("processTime",processTime);

        logDB.getCollection("Log").insertOne(doc);
    }

    public void destroy() {
    }
}