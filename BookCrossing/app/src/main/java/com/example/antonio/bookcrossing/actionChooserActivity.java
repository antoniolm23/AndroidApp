package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class actionChooserActivity extends Activity implements AsyncResponse {

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
            retrieveStatus(username);
        }

    }

    //function to retrieve the points from a remote database
    private void retrieveStatus(String username) {
        String url = "http://bookcrossing-bookcrossing.rhcloud.com/readStatus.php?";
        Connection httpConnection = new Connection();
        httpConnection.delegate = this;
        httpConnection.execute(url + "v=" + username);
    }

    @Override
    /*the resultant string is in the format points: , position: */
    public void processFinish(String result){
        Log.d("hello", "process finish");
        try{
            JSONObject Json = new JSONObject(result);
            String points = Json.getString("points");
            String position = Json.getString("position");

            //now it's possible to update the form according to the gathered results
            updateStatus(points, position);
        }
        catch(JSONException je) {
            Log.d("error", je.toString());
        }
    }

    //update the form with the just received results
    private void updateStatus(String points, String position) {
        //set the points
        TextView t = (TextView) findViewById(R.id.points);
        String textPoints = t.getText().toString();
        textPoints += " " + points;
        t.setText(textPoints);

        //set the ranking
        t = (TextView) findViewById(R.id.rank);
        String textRank = t.getText().toString();
        textRank += " " + position;
        t.setText(textRank);
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
    public void depositBook(View v) {
        Intent intent = new Intent(this, DepositBook.class);
        startActivity(intent);
    }

    //function that launches the activity of searching books around
    public void searchBooks(View v) {
        Intent intent = new Intent(this, SearchBooks.class);
        startActivity(intent);
    }

    //function with which a user takes a book
    /*
    * The user inserts just the isbn of the book, the program
    * will insert the book on the database
    * */
    public void bookTaken(View v) {
        Log.d("book", "take");
        EditText isbnT = (EditText) findViewById(R.id.isbnT);
        String isbn = isbnT.getText().toString();
        bookTake bt = new bookTake(this, isbn);
        bt.doRequest();
    }

    /*
    * Function that given the isbn of the book tries to give the user
    * the history of the book
    * */
    public void bookHistory(View v) {
        EditText isbnS = (EditText) findViewById(R.id.isbnS);
        String isbn = isbnS.getText().toString();
        Intent intent = new Intent(this, BookHistory.class);
        intent.putExtra("isbn", isbn);
        startActivity(intent);
    }
}
