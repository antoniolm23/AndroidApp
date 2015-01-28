package com.example.antonio.bookcrossing;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Antonio on 21/01/2015.
 */

public class Connection extends AsyncTask<String, String, String> {


    public AsyncResponse delegate = null;


    String url;
    String response;
    boolean responseSet = false;

    @Override
    protected String doInBackground(String[] params) {

        //Log.d("Connection", "Background" + params[0]);

        url = params[0].toString();

        response = getResponse(url);
        responseSet = true;

        return response;
    }

    //returns the response given the URL
    public String getResponse(String url){

        //Log.d("Connection", "getResponse");

        try{
            URL urlRequest = new URL(url);
            //Log.d("Connection", "Hello");
            HttpURLConnection con = (HttpURLConnection) urlRequest.openConnection();

            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader(is, "utf-8"), 8);
            //StringBuilder sb = new StringBuilder();
            String line = null;
            if((line = reader.readLine()) != null) {

                //line formatting according to function needs
                line = line.replace("}," , "}\n");
                line = line.replace("[", "");
                line = line.replace("]", "");

                Log.d("Replace", line);
            }

            is.close();

            //System.err.print(sb.toString());

            is.close();
            return line;

        }
        catch (MalformedURLException mue) {
            System.err.print("Url not usable\n");
        }
        catch(Exception e) {
            System.err.print("Error in opening the connection " + e.toString() + "\n ");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result){
        Log.d("Log", result);
        delegate.processFinish(result);
    }

}
