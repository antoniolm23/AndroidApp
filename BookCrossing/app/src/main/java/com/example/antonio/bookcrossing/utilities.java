package com.example.antonio.bookcrossing;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Antonio on 10/01/2015.
 * Singleton Class used to store all variables and functions that will be needed in the whole app
 */
public class utilities {

    boolean positionSet = false;
    public double longD;
    public double latD;

    public int points;
    public int position;

    private static utilities mInstance = null;

    public static utilities getInstance() {
        if(mInstance == null)
            mInstance = new utilities();
        return mInstance;
    }

    //TODO BOTH FUNCTION OF UPDATE
    public boolean updateBookMap(String s){
        return true;
    }

    public boolean updatePoints(String s) {
        return true;
    }

    public double getLongD() {
        return this.longD;
    }
    public double getLatD() {
        return  this.latD;
    }

    public void setLatD(double l) {
        latD = l;
    }

    public void setLongD(double l){
        longD = l;
    }

    public boolean getPositionSet() {
        return this.positionSet;
    }

    public void setPosition(boolean b) {
        positionSet = true;
    }

    public String getUsername(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String username = sharedPref.getString("username", "");
        if(username == "") {
            Log.d("error", "no username");
            return "";
        }
        return username;
    }
    public String getName(Context c) {
        Log.d("name", "getting name" );
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String name = sharedPref.getString("name", "");
        if(name == "") {
            Log.d("error", "no name");
            return null;
        }
        return name;
    }
    public String getSurname(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String surname = sharedPref.getString("surname", "");
        if(surname == "") {
            Log.d("error", "no surname");
            return null;
        }
        return surname;
    }
}
