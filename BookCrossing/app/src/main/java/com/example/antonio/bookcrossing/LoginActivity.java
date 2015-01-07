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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity{

    private boolean imageChanged = false;
    private String imagePath = "";
    private static final int PICK_IMAGE = 1;
    private String selectedImagePath;
    public final static String EXTRA_MESSAGE = "com.example.antonio.bookcrossing.MESSAGE";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();
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
        /*if(name.getText().toString().equals(""))
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

        //store the input data into the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username.getText().toString());
        editor.putString("name", name.getText().toString());
        editor.putString("email", email.getText().toString());
        editor.putString("surname", surname.getText().toString());
        editor.putString("city", city.getText().toString());
        if(imageChanged) {
            editor.putString("image", imagePath);
            System.err.print("\n imageChanged: " + imagePath + "\n");
        }

        editor.commit();

        //sets the login so I don't have to redo it
        StartInterface.login = 1;

        //start an activity
        Intent intent = new Intent(this, actionChooserActivity.class);
        intent.putExtra(EXTRA_MESSAGE, username.getText().toString());
        startActivity(intent);

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
                    FrameLayout frameLayout = (FrameLayout) this.findViewById(R.id.frameLayout1);

                    BitmapDrawable background =
                            new BitmapDrawable(getResources(), yourSelectedImage);
                    frameLayout.setBackground(background);
                    imageChanged = true;
                    imagePath = filePath;
                }
        }
    }

    //check if the email is correctly inserted
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

}



