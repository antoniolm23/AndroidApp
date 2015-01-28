package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SearchBooks extends Activity implements AsyncResponse {

    private GoogleMap map;
    public final static int MINUTES = 60 * 10 * 1000;
    String locationProvider = LocationManager.GPS_PROVIDER;
    //index of the php page
    String url = "http://bookcrossing-bookcrossing.rhcloud.com/readBookPositions.php?";
    //JSONObject Json = new JSONObject();

    //Connection httpConnection = new Connection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        //map fragmaent
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();

        getCurrentLocation();
    }

    public void getCurrentLocation(){
        LocationManager locationMgr;
        locationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //double longitude;
        //double latitude;
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
                LatLng actualLatLng= new LatLng(latitude, longitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLatLng, 15));

                retrieveMarkers(longitude, latitude);
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
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINUTES, 2, listener);
        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINUTES, 2, listener);
    }

    //retrieve the books position near the actual one
    private void retrieveMarkers(double longitude, double latitude) {

        //at first set the parameters to send
        String query = "v=" +latitude + "|" + longitude;
        //String query = "v=43|10";
        //try to open a connection with the database
        Connection httpConnection = new Connection();
        httpConnection.delegate = this;
        httpConnection.execute(url+query);
        //System.err.print("Response: " + response + "\n");
    }

    @Override
    public void processFinish(String result) {
        String[] parts = result.split("\n");
        try{
            for(String s:parts) {
                //Log.d("Outputtt", s);
                JSONObject Json = new JSONObject(s);
                String title = Json.getString("title");
                String author = Json.getString("author");
                String longitude = Json.getString("longitude");
                String latitude = Json.getString("latitude");
                //Log.d("H", author + "\t" + title);
                placeMarker(title, author, longitude, latitude);
            }
        }
        catch(JSONException je){
            Log.d(je.toString(),"wrong result");
        }
    }

    //place the markers on the map
    public void placeMarker(String title, String author, String longitude, String latitude){
        double longM = Double.parseDouble(longitude);
        double latM = Double.parseDouble(latitude);

        map.addMarker(new MarkerOptions().position(new LatLng(latM, longM)).title(
                "Title: " + title + "\n" + "Author: " + author));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_books, menu);
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
}
