package com.example.antonio.bookcrossing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity implements AsyncResponse{

    private boolean imageChanged = false;
    private String imagePath = "";
    private static final int PICK_IMAGE = 1;
    private String selectedImagePath;
    public final static String EXTRA_MESSAGE = "com.example.antonio.bookcrossing.MESSAGE";
    public final static String url = "http://bookcrossing-bookcrossing.rhcloud.com/insertUsers.php?";

    //private variables to store values inserted
    private String sName = "";
    private String sUsername = "";
    private String sSurname = "";
    private String sCity = "";
    private String sEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        Log.d("login activity", "login");
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /*
     * Action to do when the user presses the image
     */
    public void chooseImage(View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /*resets the form*/
    public void cancelData(View v) {
        EditText name = (EditText) this.findViewById(R.id.name);
        EditText surname = (EditText) this.findViewById(R.id.surname);
        EditText username = (EditText) this.findViewById(R.id.username);
        EditText email = (EditText) this.findViewById(R.id.email);
        EditText city = (EditText) this.findViewById(R.id.city);
        name.setText("");
        surname.setText("");
        username.setText("");
        email.setText("");
        city.setText("");
    }

    /*
     * Send the data inserted to the form to a remote database and store them locally
     */
    public void commitData(View v){
        BitmapDrawable image = null;
        EditText name = (EditText) this.findViewById(R.id.name);
        EditText surname = (EditText) this.findViewById(R.id.surname);
        EditText username = (EditText) this.findViewById(R.id.username);
        EditText email = (EditText) this.findViewById(R.id.email);
        EditText city = (EditText) this.findViewById(R.id.city);

        /*Very simple checking fields*/
       /* if(name.getText().toString().equals(""))
            return;
        if(surname.getText().toString().equals(""))
            return;
        if(username.getText().toString().equals(""))
            return;
        if(email.getText().toString().equals("") ||
                isEmailValid(email.getText().toString()))
            return;
        if(city.getText().toString().equals(""))
            return;*/

        //sets the login so I don't have to redo it
        StartInterface.login = 1;

        Log.d("login", "connection");

        Connection c = new Connection();
        c.delegate = this;

        //preare fields to put in the request
        sName = name.getText().toString();
        sSurname = surname.getText().toString();
        sUsername = username.getText().toString();
        sCity = city.getText().toString();
        sEmail = email.getText().toString();

        //request to send to the db
        c.execute(url + "v=" + sName + "|" + sSurname + "|" + sUsername + "|"
                + sEmail + "|" + sCity);
    }

    @Override
    public void processFinish(String result) {
        //make toast with the response
        Toast.makeText(this, result, Toast.LENGTH_LONG);

        if(result.contains("Username correctly inserted")) {
            //store the input data into the shared preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", sUsername);
            editor.putString("name", sName);
            editor.putString("email", sEmail);
            editor.putString("surname", sSurname);
            editor.putString("city", sCity);
            if(imageChanged) {
                editor.putString("image", imagePath);
                System.err.print("\n imageChanged: " + imagePath + "\n");
            }

            editor.commit();

            //start activity
            Intent intent = new Intent(this, actionChooserActivity.class);
            intent.putExtra(EXTRA_MESSAGE, sUsername);
            startActivity(intent);
        }

        else {
            EditText name = (EditText) this.findViewById(R.id.name);
            EditText surname = (EditText) this.findViewById(R.id.surname);
            EditText username = (EditText) this.findViewById(R.id.username);
            EditText email = (EditText) this.findViewById(R.id.email);
            EditText city = (EditText) this.findViewById(R.id.city);
            name.setText("");
            surname.setText("");
            username.setText("");
            email.setText("");
            city.setText("");
        }
    }

    /*
     * Set the selected image as background
    */
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    System.err.print("\n\n" + filePath + "\n\n");
                    ImageView imageView = (ImageView) this.findViewById(R.id.userView);

                    BitmapDrawable background =
                            new BitmapDrawable(getResources(), yourSelectedImage);
                    imageView.setBackground(background);
                    imageChanged = true;
                    imagePath = filePath;
                }
        }
    }

    //check if the email is correctly inserted
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

}