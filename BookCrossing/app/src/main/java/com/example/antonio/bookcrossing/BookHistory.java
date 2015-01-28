package com.example.antonio.bookcrossing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class BookHistory extends Activity implements AsyncResponse {

    private GoogleMap map;
    public final static int MINUTES = 60 * 10 * 1000;
    String locationProvider = LocationManager.GPS_PROVIDER;
    //index of the php page
    String url = "http://bookcrossing-bookcrossing.rhcloud.com/oldPositions.php?v=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_history);
        Intent showHistory = getIntent();
        String isbn = showHistory.getStringExtra("isbn");
        Log.d("isbn", isbn);
        makeRequest(isbn);

        //draw map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapHistory);
        map = mapFragment.getMap();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_history, menu);
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

    public void makeRequest(String isbn) {
        Connection httpConnection = new Connection();
        httpConnection.delegate = this;
        httpConnection.execute(url+isbn);
        Log.d("out", "Request done" + url + isbn);
    }

    //place the markers on the map
    public void placeMarker(String title, String author, String longitude, String latitude){
        if(latitude == null || longitude == null || title == null || author == null) {
            Log.d("Error", "null value" + longitude + " " + latitude + " " + title
            + " " + author);
        }
        double longM = Double.parseDouble(longitude);
        double latM = Double.parseDouble(latitude);

        map.addMarker(new MarkerOptions().position(new LatLng(latM, longM)).title(
                "Title: " + title + "\n" + "Author: " + author));
    }

    @Override
    public void processFinish(String result) {
        Log.d("out", result);
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        String[] parts = result.split("\n");
        try{
            for(String s:parts) {
                String latLong[] = new String[4];
                //Log.d("Outputtt", s);
                JSONObject Json = new JSONObject(s);
                String title = Json.getString("title");
                String author = Json.getString("author");
                String longitude = Json.getString("longitude");
                String latitude = Json.getString("latitude");
                Log.d("H", author + "\t" + title + "\t" + latitude + "\t" + longitude);
                //after placing the markers draw line to link them
                placeMarker(title, author, longitude, latitude);
                list.add(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
            }
            drawLine(list);
        }
        catch(JSONException je){
            Log.d(je.toString(),"wrong result");
        }
    }

    /*
     * latLong[0], latLong[1] starting point
     * latLong[2], latLong[3] reaching point
     * Daw a line on the map to connect the points
     */
    public void drawLine(ArrayList<LatLng> list){
        LatLng[] array = new LatLng[list.size()];
        array = list.toArray(array);
        //now draw the line
        Polyline line;
        line = map.addPolyline(new PolylineOptions()
                .add(array)
                .width(10)
                .color(Color.BLUE).geodesic(true));
    }

}
