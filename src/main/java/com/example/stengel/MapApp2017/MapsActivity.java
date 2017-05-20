package com.example.stengel.MapApp2017;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        oDBManager.openRead();
        //SQLiteDatabase  db = openOrCreateDatabase("MyDB", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor userCursor = oDBManager.getAllLocations();

         //////////////// DO GOOGLE MAPS STUFF /////////////////////////////
        //string array of all cities
        if (userCursor.moveToFirst())
        {
            do {
                DisplayLocationMap(userCursor);
            } while (userCursor.moveToNext());
        }
        oDBManager.close();
    }
        public void DisplayLocationMap(Cursor userCursor)
        {
        Toast.makeText(this,
                "id: " + userCursor.getString(0) + "\n" +
                        "Title: " + userCursor.getString(1) + "\n" +
                        "Latitude: " + userCursor.getString(2) + "\n" +
                        "Longitude:  " + userCursor.getString(3),
                Toast.LENGTH_LONG).show();
    }
        }


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        /*ArrayList<MarkerData> markerArray = new ArrayList<MarkerData>();
            for(int i = 0 ; i < oDBCursor.size() ; i++ ) {

            createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude(), markersArray.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID());
        }
*/



