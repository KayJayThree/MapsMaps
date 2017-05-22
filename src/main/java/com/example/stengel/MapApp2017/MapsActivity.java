package com.example.stengel.MapApp2017;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    LatLngBounds.Builder builder;
    CameraUpdate cu;
    List<Marker> markersList = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        // Showing / hiding your current location
        //mMap.setMyLocationEnabled(true);

        // Enable / Disable zooming controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable my location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        mMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        mMap.getUiSettings().setZoomGesturesEnabled(true);



        ////    DATABASE STUFF FOR READING MAPS TABLE
        // Open database for reading cursor
        DBManager oDBManager = new DBManager(this);
        // Open database for reading

        oDBManager.open();
        Cursor userCursor = oDBManager.getAllLocations();

                //////////////// DO GOOGLE MAPS STUFF /////////////////////////////
                //string array of all cities
                if (userCursor.moveToFirst())
                    oDBManager.open();{
                    do {
                        DisplayLocationMap(userCursor);
                    }
                    while (userCursor.moveToNext());
                    }
                oDBManager.close();

        /** Go set the zoom on all markers    */

    }

    // --the current table contents are in userCursor and all locations
    // --are set to map positions as 'Markers"
    public void DisplayLocationMap(Cursor userCursor) {

        String snippet = userCursor.getString(0);
        String title =  userCursor.getString(1);
        Double latitude = Double.valueOf(userCursor.getString(2));
        Double longitude = Double.valueOf(userCursor.getString(3));


        // Adding to markerlist to zoom around markers
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        marker.showInfoWindow();
        markersList.add(marker);


        latlngs.add(new LatLng(latitude, longitude));

        for (LatLng point : latlngs) {
            options.position(point);
            options.title(title);
            options.snippet(snippet);
            mMap.addMarker(options);

            //Print to Toast each time through for testing
            ////////////////////////////////////////////////////////////////////printIT(userCursor);
        }
        setAllMarkers(markersList);
    }


    // --for testing Markers added to map, each time through a Toast msg is sent
    public void printIT(Cursor userCursor){
        Toast.makeText(this,
                "ID:  " + userCursor.getString(0) + "\n" +
                        "TITLE:  " + userCursor.getString(1) + "\n" +
                        "LATITUDE:  " + userCursor.getString(2) + "\n" +
                        "LONGITUDE:  " + userCursor.getString(3),
                Toast.LENGTH_LONG).show();
    }

    public void setAllMarkers(List<Marker> markersList){
            /** Setting teh zoom or fanning out from all markers    */
            /** create for loop for get the latLngbuilder from the marker list*/
            builder = new LatLngBounds.Builder();
            for (Marker m : markersList) {
            builder.include(m.getPosition());
            }
            /** initialize the padding for map boundary*/
            int padding = 100 ;
            /**create the bounds from latlngBuilder to set into map camera*/
            LatLngBounds bounds = builder.build();
            /**create the camera with bounds and padding to set into map*/
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
            }

}

// Need to add method to center the map here or create a
// method below and call it from here to center map
//mMap.animateCamera(); can be use ??

//LatLngBounds.Builder bounds = new LatLngBounds.Builder();
       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        mMap.moveCamera(cu);*/



