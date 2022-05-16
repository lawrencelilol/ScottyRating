/**
 * author: Tiantong Li (tiantonl)
 * Last modified: April 10th, 2022
 *
 */

package edu.cmu.project4t1;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/*
 * This class provides capabilities to search for a classes rating on Heroku given a search id.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of inner class BackgroundTask that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the courseReady method to do the update.
 *
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is
 * done; it calls *back* to cr to report the results
 *
 */
public class GetClass {
    CourseRating cr = null;   // for callback
    String searchID = null;       // search course for this id
    float[] rating = null;        // returned from Web Service

    // search( )
    // Parameters:
    // String searchID: the courseID to search on API
    // Activity activity: the UI thread activity
    // CourseRating ip: the callback method's class; here, it will be cr.courseReady( )
    public void search(String searchID, Activity activity, CourseRating cr) {
        this.cr = cr;
        this.searchID = searchID;
        new BackgroundTask(activity).execute();
    }

    // This class will do the background task for application
    private class BackgroundTask {
        private Activity activity;

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute(){
            // There could be more setup here, which is why
            // startBackground is not called directly
            startBackground();
        }


        // doInBackground( ) implements whatever you need to do on
        //  the background thread.
        // Implement this method to suit your needs
        private void doInBackground() {
            rating = search(searchID);
        }

        // onPostExecute( ) will run on the UI thread after the background
        //    thread completes.
        // Implement this method to suit your needs
        public void onPostExecute() {
            cr.courseReady(rating);
        }

        /*
         * Search Web service for the searchID argument, and return float[] of ratings
         */
        private float[] search(String searchID){
            float[] res = new float[3];

            // make sure searchID contains "-"
            if(searchID.contains("-")) {
                String[] strings = searchID.split("-");
                String id = strings[0] + strings[1];
                if(!id.matches("[0-9]+")) {
                    System.out.println("Invalid input");
                    return null;
                }
            } else {
                if(!searchID.matches("[0-9]+")) {
                    System.out.println("Invalid input");
                    return null;
                }
            }

            // check for invalid id
            if(searchID.length() < 5 || searchID.length() > 6) {
                System.out.println("Course id with invalid length");
                return null;
            } else if(searchID == null || searchID.length() == 0) {
                System.out.println("empty course id");
                return null;
            }

            // check to see if the id includes '-'
            if(searchID.charAt(2) != '-') {
                String id = searchID.substring(0,2) + "-" + searchID.substring(2);
                searchID = id;
            }
            // task1
//            String webserviceURL = "https://sleepy-basin-06882.herokuapp.com/getAClass?class-id=" + searchID;

            // local
//            String webserviceURL = "http://10.0.2.2:8080/Project4T1-1.0-SNAPSHOT/getAClass?class-id=" + searchID;
            // task2
            String webserviceURL = "https://calm-fjord-82962.herokuapp.com/getAClass?class-id=" + searchID;

            URL webURL = null;
            try {
                webURL = new URL(webserviceURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // get the json from web service
            String json = getRemoteClass(webURL);
            JSONObject obj = null;
            try {
                if(json != null) {
                    obj = new JSONObject(json);
                    JSONArray ratings = (JSONArray) obj.get("rating");
                    double respectRate = ratings.getDouble(0);
                    double teachingRate = ratings.getDouble(1);
                    double courseRate = ratings.getDouble(2);

                    System.out.println(courseRate);

                    res[0] = (float) respectRate;
                    res[1] = (float) teachingRate;
                    res[2] = (float) courseRate;
                } else {
                    System.out.println("Did not get the json object");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        /*
         * Given a URL referring to an course with its courseID, return a JsonObject of course Ratings
         */
        private String getRemoteClass(final URL url) {
            try {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                InputStream inputStream = huc.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
