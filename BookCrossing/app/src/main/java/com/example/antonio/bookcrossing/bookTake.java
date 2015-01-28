package com.example.antonio.bookcrossing;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Antonio on 27/01/2015.
 */
public class bookTake implements AsyncResponse {
    Context c;
    String isbn;
    String url = "http://bookcrossing-bookcrossing.rhcloud.com/takeBook.php?";

    public bookTake(Context c, String i) {
        this.c = c;
        this.isbn = i;
    }

    //function that generates the http request
    public void doRequest() {
        String username = utilities.getInstance().getUsername(c);
        Connection httpConnection = new Connection();
        httpConnection.delegate = this;
        httpConnection.execute(url + "v=" + isbn + "|" + username);
    }

    @Override
    public void processFinish(String result) {
        //Log.d("bt", "processFinish");
        Toast toast = Toast.makeText(c, result, Toast.LENGTH_LONG);
        toast.show();
    }
}
