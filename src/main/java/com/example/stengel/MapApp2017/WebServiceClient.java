/*
 * Copyright (c) 2017.
 * NOTICE:  All information contained herein is, and remains the property of Ron Stengel and its suppliers, if any.  The intellectual and technical concepts contained herein are proprietary to Ron Stengel and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Ron Stengel.
 */

package com.example.stengel.MapApp2017;

/**
 * Created by rsten on 5/16/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


// This class calls a given web service asynchronously so as not to lock up the UI thread.
public class WebServiceClient extends AsyncTask<String, Void, String> {

    public String responseJson = "";
    Context baseContext;

    // Context type passed in this constructor only so that Toast can be used later.
    public WebServiceClient(Context context){
        baseContext = context;
    }

    // This is called when "execute" is called on this object.
    protected String doInBackground(String... urls){

        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder stringBuilder = new StringBuilder();

        try{
            url = new URL(urls[0].replaceAll(" ","%20"));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return stringBuilder.toString();
    }


    // This is called when response comes back from web service.
    // Currently the code just logs the json response and Toasts it to screen.
    protected void onPostExecute(String result){

        responseJson = result;

        Log.d("###", "response from cloud push: " + responseJson);
        Toast.makeText(baseContext,
                "response from cloud push: " + responseJson, Toast.LENGTH_LONG).show();
    }
}
