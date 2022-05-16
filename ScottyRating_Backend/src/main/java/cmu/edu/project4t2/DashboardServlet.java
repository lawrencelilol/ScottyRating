/**
 * author: Tiantong Li (tiantonl)
 * Last modified: April 10th, 2022
 */

package cmu.edu.project4t2;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet to change the view from index to dashboard
 */
@WebServlet(name = "dashboard", urlPatterns = "/goDashboard")
public class DashboardServlet extends HttpServlet {
    private MongoDatabase logDB;

    public void init() {
        // Connect to MongoDB
        // creat the personalized connection string
        String s = "mongodb://lawrencelilol:LL2619970206ltt@cluster0-shard-00-02.lswpu.mongodb.net:27017," +
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

    // Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // set the nextView as result
        String nextView = "result.jsp";;

        // put logs in the request
        List<Document> logs = retrieveData();
        request.setAttribute("Logs",logs);

        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }

    // This function retrieves data into MongoDB
    private List<Document> retrieveData() {
        MongoCollection<Document> logCollection = logDB.getCollection("Log");
        List<Document> logs = new ArrayList<>();
        logCollection.find().into(logs);
        return logs;
    }

}
