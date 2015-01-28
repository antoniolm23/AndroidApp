package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class StartInterface extends Activity {
    public static int login = 0;
    public final static String EXTRA_MESSAGE = "com.example.antonio.bookcrossing.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_interface);
        Log.d("hello", "hello");
        String name = utilities.getInstance().getName(this);

        if(name == null) {
            //if(login == 0)
            System.err.print("\nNAME NOT PRESENT, DO LOGIN\n");
            startLogin();
        }
        else
            startActionChooser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_interface, menu);
        return true;
    }

    /*starts the login interface in which the user puts his personal informations*/
    public void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /*
     * function to start the chooser activity, in which the user selects the action to perform
     *
    */
    public void startActionChooser() {
        //String text = loadData();
        Intent intent = new Intent(this, actionChooserActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, text);
        startActivity(intent);
    }

    /*function used to load personal data: username, city, points etc*/
    //public String loadData(){
    //    return "hello";
    //}

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
}
