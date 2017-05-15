package com.example.stengel.MapApp2017;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient m_oGoogleApiClient;
    private static final int LOCATION_PERMISSION_CODE = 42;

    private String m_sLocation = "";

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
        Toast.makeText(this, "Clicked btnAddMyPlace_OnClick & called getLastKnownLocation()", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "GOT PERMISSION!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "calling getLastLocation on FusedLocationApi()!", Toast.LENGTH_SHORT).show();

            // *** Call FusedLocationApi method to get last location.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    m_oGoogleApiClient);
        } catch (SecurityException ex) {
            Log.d("---", ex.getMessage());
            Toast.makeText(this, "ex.getMessage()", Toast.LENGTH_SHORT).show();
        }
        if (mLastLocation != null) {
            String sLocation = "lat: " + String.valueOf(mLastLocation.getLatitude()) +
                    " --- lon: " + String.valueOf(mLastLocation.getLongitude());
            Log.d("---", "Location: " + sLocation);
            Toast.makeText(this, "Location: " + sLocation, Toast.LENGTH_LONG).show();
            m_sLocation = sLocation;

            // ##### Add code here to write location to database #####



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
}
