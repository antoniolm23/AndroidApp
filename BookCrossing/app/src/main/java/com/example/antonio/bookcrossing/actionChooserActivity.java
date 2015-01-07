package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;


public class actionChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_chooser);

        //Put the selected image in the frame
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String fileUrl = sharedPref.getString("image", "");

        Bitmap yourSelectedImage = BitmapFactory.decodeFile(fileUrl);
        FrameLayout frameLayout = (FrameLayout) this.findViewById(R.id.frameLayout);

        BitmapDrawable background =
                new BitmapDrawable(getResources(), yourSelectedImage);
        frameLayout.setBackground(background);

        //retrieve the points
        String username = sharedPref.getString("username", "default");
        if(username.equals("default")){
            System.err.print("\nname not present\n");
        }
        else {
            //retrieve points and ranking
            int points = retrievePoints(username);
            int ranking = retrieveRanking(username);

            //set the points
            TextView t = (TextView) findViewById(R.id.points);
            String textPoints = t.getText().toString();
            textPoints += " " + points;
            t.setText(textPoints);

            //set the ranking
            t = (TextView) findViewById(R.id.rank);
            String textRank = t.getText().toString();
            textRank += " " + ranking;
            t.setText(textRank);
        }
    }

    //function to retrieve the points from a remote database
    private int retrievePoints(String username) {
        return 10;
    }

    //function to retrieve the rank position
    private int retrieveRanking(String username) {
        return 2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //function that launches the activity of deposit a book
    public void depositBook() {
        Intent intent = new Intent(this, DepositBook.class);
        startActivity(intent);
    }

    //function that launches the activity of searching books around
    public void searchBooks() {
        Intent intent = new Intent(this, SearchBooks.class);
        startActivity(intent);
    }

    //quit the app with a click
    public void exitApp() {

    }
}
