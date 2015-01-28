package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DepositBook extends Activity implements AsyncResponse {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri = null;
    private boolean imageChanged = false;
    //private utilities util = new utilities(this);
    public final static int TWO_MINUTES = 60 * 2 * 1000;
    String locationProvider = LocationManager.GPS_PROVIDER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_book);
        getCurrentLocation();
    }

    public void getCurrentLocation(){
        LocationManager locationMgr;
        locationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //while waiting to get the updated location use the cached one
        Location lastKnownLocation = locationMgr.getLastKnownLocation(locationProvider);

        LocationListener listener = new LocationListener() {
            double latitude;
            double longitude;
            @Override
            public void onLocationChanged(Location location) {

                // A new location update is received.  Do something useful with it
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String toastString = "latitude: " + latitude + "\nlongitude: " + longitude;
                //Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_SHORT).show();
                //set the utils constants in order to let them available to others
                utilities.getInstance().setPosition(true);
                utilities.getInstance().setLatD(latitude);
                utilities.getInstance().setLongD(longitude);

                //once everything has been updated call the method
                setPosition();
            }
            @Override
            public void onProviderDisabled(String provider) {
                // No code here
            }

            @Override
            public void onProviderEnabled(String provider) {
                // No code here
            }

            @Override
            public void onStatusChanged(String provider, int status,Bundle extras) {
                // No code here
            }

        };

        /*
            Every two minutes update the position if it differs more than 2 m
            with respect to the previously available
        */
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 2, listener);
        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TWO_MINUTES, 2, listener);
    }

    public void setPosition() {
        TextView tv = (TextView) findViewById(R.id.Gps);
        tv.setText("latitude: " + utilities.getInstance().getLatD() +
                "\nlongitude: " + utilities.getInstance().getLongD());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deposit_book, menu);
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

    //take a picture and put it as background of imageView
    public void startCamera(View v) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }


    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "BookCrossing");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("BookCrossing", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            //System.err.print("file output: " + mediaFile.getPath().toString());
            //System.err.print("\nfile output: " + mediaFile.getAbsolutePath().toString());
        }
        else {
            return null;
        }

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.err.print(fileUri);
        imageChanged = true;
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                Bitmap yourSelectedImage = BitmapFactory.decodeFile(fileUri.getPath().toString());
                ImageView imageView = (ImageView) this.findViewById(R.id.imageView);

                BitmapDrawable background =
                        new BitmapDrawable(getResources(), yourSelectedImage);
                imageView.setBackground(background);

            }
            else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            }
            else {
                // Image capture failed, advise user
            }
        }
    }



    private void sendToDb(String author, String title, String isbn, BitmapDrawable image) {
        String url = "http://bookcrossing-bookcrossing.rhcloud.com/insertBook.php?";
        double latitude = utilities.getInstance().getLatD();
        double longitude = utilities.getInstance().getLongD();

        String username = utilities.getInstance().getUsername(this);

        Connection httpConnection = new Connection();
        httpConnection.delegate = this;
        httpConnection.execute(url + "v=" + title + "|" + author + "|" + latitude
        + "|" + longitude + "|" + isbn + "|" + username);
        Log.d("request","v="  + title + "|" + author + "|" + latitude +
                "|" + longitude + "|" + isbn + "|" + username);
    }

    @Override
    public void processFinish(String result) {
        Toast toast = Toast.makeText(this, result, Toast.LENGTH_LONG);
        toast.show();

        if(result.contains("Correct")) {
            Intent intent = new Intent(this, actionChooserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        else {
            Toast toast1 = Toast.makeText(this, "Redo request", Toast.LENGTH_SHORT);
            toast1.show();
        }
    }

    //function to store the characteristic of the book on a database
    public void depositBook(View v) {
        Toast toast;
        EditText author = (EditText) findViewById(R.id.bookAuthor);
        EditText title = (EditText) findViewById(R.id.bookTitle);
        EditText note = (EditText) findViewById(R.id.notes);

        ImageView fl = (ImageView) findViewById(R.id.imageView);
        BitmapDrawable image;
        if(imageChanged)
            image = (BitmapDrawable) fl.getBackground();
        else
            image = null;

        if(author.getText().toString().equals("")) {
            toast = Toast.makeText(getApplicationContext(), "Author missing",
                    Toast.LENGTH_LONG);
            toast.show();
        }

        if(title.getText().toString().equals("")) {
            toast = Toast.makeText(getApplicationContext(), "Title missing",
                    Toast.LENGTH_LONG);
            toast.show();
        }

        //if the connection to the server fails notify that to the user
        sendToDb(author.getText().toString(), title.getText().toString(),
                note.getText().toString(), image);

        /*
            if the connection goes OK there are some actions to perform:
            1) update the points
            2) update the book map
            3) go back to actionchooser activity
        */
    }

    //rotate the image
    public void rotateImage(View v){
        //Matrix matrix = new Matrix();
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setRotation(imageView.getRotation() + 90);
    }

    /*
        share the content to social network, what'sapp etc....
        the content shared is for now just the position of the book, title and author
     */
    public void shareContent(View v) {
        String title, author, position;
        EditText et = (EditText) findViewById(R.id.bookAuthor);
        author = et.getText().toString();
        et = (EditText) findViewById(R.id.bookTitle);
        title = et.getText().toString();
        TextView tv = (TextView) findViewById(R.id.Gps);
        position = tv.getText().toString();

        //this is a send type intent
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //prepare the text to post and pass it to the intent
        String postText = "Hi everybody, I want to let you know that I just deposited the book " +
            title + " written by " + author + ". It is at coordinates: " + position + ".\n";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share book");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, postText);

        //now it's time to start the chooser
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }
}

