package com.example.stengel.MapApp2017;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient m_oGoogleApiClient;
    private static final int LOCATION_PERMISSION_CODE = 42;

    private String m_sLocation = "";
    String msg = "---De_bug: ";
    String baseURL = "http://10.0.2.2:8080/myplacescloud/MyPlacesCloudService?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of GoogleAPIClient.
        if (m_oGoogleApiClient == null) {
            m_oGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // ###################################################################################################
    // ##### Add method for add to my location button's click method and call getLastKnownLocation() #####
    // ###################################################################################################
    public void btnAddMyPlace_OnClick(View oView){


        getLastKnownLocation();


        // If the checkbox is selected we will:
        //  -- send information to remote server (Tomcat)
        if(((CheckBox)findViewById(R.id.chkCloudPush)).isChecked()){
            WebServiceClient oWSClient;
            oWSClient = new WebServiceClient(getBaseContext());

            String sWebserviceURL = baseURL +
                    "title=" + ((EditText)findViewById(R.id.txtEdName)).getText() +
                    "&lat="  + ((TextView) findViewById(R.id.txtLat)).getText() +
                    "&lon="  + ((TextView) findViewById(R.id.txtLon)).getText();

            //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //
            //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //
            Log.d(msg, "sWebserviceURL: " + sWebserviceURL.replaceAll(" ","%20"));
            Log.d(msg, "findViewById(R.id.txtLat): " + ((TextView) findViewById(R.id.txtLat)).getText());
            Log.d(msg, "findViewById(R.id.txtLon): " + ((TextView) findViewById(R.id.txtLon)).getText());
            Toast.makeText(this, sWebserviceURL, Toast.LENGTH_LONG).show();
            //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //
            //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //    //  DEBUG  //


            oWSClient.execute(sWebserviceURL);
        }

        //getLastKnownLocation();
        //Toast.makeText(this, "Called getLastKnownLocation()", Toast.LENGTH_LONG).show();




    }

    // ###################################################################################
    // ##### Add method for View Map button's click method and send to maps activity #####
    // ###################################################################################



    protected void onStart() {
        m_oGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        m_oGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.d("----","need permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
        else {
            Log.d("----","Got permission.");
            //Toast.makeText(this, "GOT PERMISSION!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();   //getting location here
            } else {
                Toast.makeText(this, "App can't function if location access permission not granted to app!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getLastKnownLocation(){

        Location mLastLocation = null;
        try {
            Log.d("---", "calling getLastLocation on FusedLocationApi.");
            //Toast.makeText(this, "calling getLastLocation on FusedLocationApi()!", Toast.LENGTH_SHORT).show();

            // *** Call FusedLocationApi method to get last location.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    m_oGoogleApiClient);
        } catch (SecurityException ex) {
            // If failure, ex (Exception) will display to Log and Toast
            Log.d("---", ex.getMessage());
            Toast.makeText(this, "ex.getMessage()", Toast.LENGTH_SHORT).show();
        }
        if (mLastLocation != null) {
            String sLocation = "lat: " + String.valueOf(mLastLocation.getLatitude()) +
                    " --- lon: " + String.valueOf(mLastLocation.getLongitude());
            Log.d("---", "Location: " + sLocation);

            Toast.makeText(this, "Location: " + sLocation, Toast.LENGTH_LONG).show();
            m_sLocation = sLocation;

            /////////////////////////////////////////////////////////////////////////// TEST LOG
            Log.d("---", "NAME1: " + ((EditText) findViewById(R.id.txtEdName)).getText());
            Log.d("---", " LAT: " + String.valueOf(mLastLocation.getLatitude()));
            Log.d("---", " LON: " + String.valueOf(mLastLocation.getLongitude()));


            // WORKING: puts current location of Latitude in textBox on screen
            TextView txtLat = (TextView) findViewById(R.id.txtLat);
            txtLat.setText(String.valueOf(mLastLocation.getLatitude()));

            // WORKING: puts current location of Longitude in textBox on screen
            TextView txtLon = (TextView) findViewById(R.id.txtLon);
            txtLon.setText(String.valueOf(mLastLocation.getLongitude()));

            /////////////////////////////////////////////////////////////////////////// TEST LOG

             // ##### Add code here to write location to database ############################
            // ##### Add code here to write location to database ############################

            // Insert information into database
            DBManager oDBManager = new DBManager(this);

            // Open database for writing
            oDBManager.open();
            // Set local variables to Title and location for inserting below
            TextView m_txtTitle = (TextView)findViewById(R.id.txtEdName);
                //TextView m_txtLat = (TextView)findViewById(R.id.txtLat);
                //TextView m_txtLon = (TextView)findViewById(R.id.txtLon);
            // call insertLocation in DBManager to put the entries in db
            oDBManager.insertLocation(m_txtTitle.getText().toString(), txtLat.getText().toString(),  txtLon.getText().toString());
            Log.d(msg, "TITLE::" + m_txtTitle.getText().toString() + "LAT::" +txtLat.getText().toString() +  "LON:" + txtLon.getText().toString());
            // Close the database after opening above
            oDBManager.close();


        }
        else{
            Log.d("---", "getLastKnownLocation: mLastLocation is null.");
            Toast.makeText(this, "getLastKnownLocation: mLastLocation is null.", Toast.LENGTH_SHORT).show();

            // Left the following here for reference to show how you could register for updates
            // to location... the inner "onLocationChanged" event below will get
            // called everytime the location changes.
            // This is also useful if the user's gps hasn't registered a last location
            // yet such as when they just turned on their phone or just enabled location.
            /*
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setNumUpdates(1);
                locationRequest.setInterval(0);
                locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        m_oGoogleApiClient, locationRequest,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                LocationServices.FusedLocationApi.removeLocationUpdates(
                                        m_oGoogleApiClient,
                                        this);

                                if (location != null) {
                                    String sLocation = "lat: " + String.valueOf(location.getLatitude()) +
                                            "lon: " + String.valueOf(location.getLongitude());
                                    Log.d("---", "Location (inner): " + sLocation);
                                    Toast.makeText(getBaseContext(), sLocation, Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Log.d("---", "location is null");
                                }
                            }
                        });
            }
            */
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "---suspendend: " + i, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "---Failed: " + connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();

    }

    ////////////////////////////////////////////////////////////////////////////////////////

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "The onPause() event");
    }

    /*// Called when the activity is no longer visible.
    // Called already this is duplicate.
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "The onStop() event");
    }*/


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }

    public void btnShowMap_OnClick(View view) {
        startActivity(new Intent("com.example.stengel.MapApp2017.MapsActivity"));
        Toast.makeText(this, "SECOND ACTIVITY STARTED",Toast.LENGTH_LONG).show();
        Log.d(msg, "SECOND ACTIVITY STARTED");
    }
    ////////////////////////////////////////////////////////////////////////////////////////
}
