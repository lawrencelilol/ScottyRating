/**
 * author: Tiantong Li (tiantonl)
 * Last modified: April 10th, 2022
 *
 */

package edu.cmu.project4t1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// The main activity class for the app
// it has onCr
public class CourseRating extends AppCompatActivity {
    CourseRating me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a course ratings from ScottyLabs, it
         * can callback to this object with the.  The "this" of the OnClick will be the OnClickListener, not
         * this CourseRating.
         */
        final CourseRating ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                // get user's input for the course ID
                String searchID = ((EditText)findViewById(R.id.searchID)).getText().toString();
                System.out.println("searchID = " + searchID);
                GetClass gc = new GetClass();
                gc.search(searchID, me, ma); // Done asynchronously in another thread.  It calls cr.courseReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetClass object when the picture is ready.  This allows for passing back the float[] to update the rating Bars
     */
    @SuppressLint("SetTextI18n")
    public void courseReady(float[] rating) {
        // find the views, rating bars by their id
        TextView searchView = (EditText)findViewById(R.id.searchID);
        TextView courseIDView  = (TextView) findViewById(R.id.courseID);
        RatingBar respectBar = (RatingBar) findViewById(R.id.respectRating);
        TextView respectView = (TextView) findViewById(R.id.respectView);

        RatingBar teachingBar = (RatingBar) findViewById(R.id.teachingRating);
        TextView teachingView = (TextView) findViewById(R.id.teachingView);

        RatingBar courseBar = (RatingBar) findViewById(R.id.courseRating);
        TextView courseView = (TextView) findViewById(R.id.courseView);

        // set each view and rating bars to their corresponding values
        if (rating != null) {
            String id = searchView.getText().toString();
            courseIDView.setText("Course: " + id);
            respectView.setText("Show respect to all students: " + rating[0]);
            respectBar.setRating(rating[0]);

            teachingView.setText("Overall teaching rate: " + rating[1]);
            teachingBar.setRating(rating[1]);

            courseView.setText("Overall course rate: " + rating[2]);
            courseBar.setRating(rating[2]);
        } else {
            System.out.println("No course founded");
            courseIDView.setText("No course founded");
            respectView.setText("Sorry, could not find the rating for course ");
            teachingView.setText("Sorry, could not find the rating for course ");
            courseView.setText("Sorry, could not find the rating for course ");
        }

        // reset the values
        searchView.setText("");
        respectBar.invalidate();
        courseBar.invalidate();
        teachingBar.invalidate();
    }

}
