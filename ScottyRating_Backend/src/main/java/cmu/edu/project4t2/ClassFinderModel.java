/**
 * author: Tiantong Li (tiantonl)
 * Last modified: April 10th, 2022
 */

package cmu.edu.project4t2;

import com.google.gson.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class ClassFinderModel {

    /*
     * Make an HTTP request to a Dog.api and fetch pictures from api.
     *
     * @param pictureURL The URL of the request
     * @return A array of string of the breed's pictures.
     */

    public JsonObject doClassSearch (String searchTag) throws UnsupportedEncodingException {
        // get the user's input that is the breed of the dog
        searchTag = URLEncoder.encode(searchTag, "UTF-8");

        // Store class's information
        // Create a URL for the page to be screen scraped

        String fceURL = "https://course.apis.scottylabs.org/fces?courseID="
                        + searchTag +
                        "&year=2021&semester=fall";
        JsonObject ratings = fetchRating(fceURL);

        return ratings;
    }

    /*
     * Make an HTTP request to ScottyLabs Course api and fetch fces from api.
     *
     * @param fceURL The URL of the request
     * @return A array of string of the rating for the course.
     */
    public JsonObject fetchRating(String fceURL) {
        JsonArray ratings = new JsonArray(3);
        JsonObject ans = new JsonObject();
        try {
            URL url = new URL(fceURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if(responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                String inline = "";

                // Read all the text returned by the server
                Scanner sc = new Scanner(url.openStream());
                // Read each line of "sc" until done, adding each to "inline"
                while(sc.hasNext()) {
                    inline += sc.nextLine();
                }

                // close the Scanner
                sc.close();

                if(inline.equals("")) {
                    System.out.println("Did not get rating from Scotty lab");
                } else {
                    JsonParser parser = new JsonParser();
                    JsonElement fceElement = parser.parse(inline);
                    JsonArray fce = fceElement.getAsJsonArray();

                    JsonObject jsonObject = (JsonObject) fce.get(0);
                    JsonArray jsonArray = (JsonArray) jsonObject.get("rating");

                    // Put each JSON picture url into a string array called urls
                    for (int i = 6; i < jsonArray.size(); i++) {
                        ratings.add(jsonArray.get(i).getAsFloat());
                    }
                    ans.add("rating", ratings);
                }
            }
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return ans;
    }
}
