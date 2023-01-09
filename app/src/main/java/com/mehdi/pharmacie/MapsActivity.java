package com.mehdi.pharmacie;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLngBounds;
import com.mehdi.pharmacie.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    double currentLat = 0, currentLong = 0;
    double currentLating = 0, currentLonging = 0;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Button btnBack;
    private Button btnChemin;
    LatLng destination;
    String apikey;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker=null;

    //PolylineOptions routeLine = new PolylineOptions().color(Color.RED);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnBack = findViewById(R.id.button1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent backIntent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(backIntent);
            }
        });
        Intent i =getIntent();
        String recycleview = i.getExtras().getString("recycleview");
        apikey=i.getExtras().getString("cle");
        String mylatlong = i.getExtras().getString("mylatlong");
        currentLat = Double.parseDouble(mylatlong.split(",",2)[0]);
        currentLong = Double.parseDouble(mylatlong.split(",",2)[1]);
        currentLating = Double.parseDouble(recycleview.split(",",2)[0]);
        currentLonging = Double.parseDouble(recycleview.split(",",2)[1]);

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
        System.out.println(currentLat+" "+currentLong);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLat,currentLong),15
        ));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.addMarker(new MarkerOptions()
                .title("MyPosition")
                .position(new LatLng(currentLat,currentLong))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.addMarker(new MarkerOptions()
                .title("Pharmacie Position")
                .position(new LatLng(currentLating,currentLonging))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                //destination=new LatLng(currentLating,currentLonging);

                //String urll = getDirectionsUrl(destination, new LatLng(currentLat,currentLong));
                //String urlll = "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=5b3ce3597851110001cf6248595501efd82046e7852e3f3df47a6951&start=-8.4964952,33.241706&end=-8.4918286,33.239654";
                String urlll = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248595501efd82046e7852e3f3df47a6951&start="+currentLonging+","+currentLating+"&end="+currentLong+","+currentLat;
                System.out.println(urlll);
                FetchUrl2 fetchUrl2 = new FetchUrl2();
                fetchUrl2.execute(urlll);
                 //System.out.println("route :"+routeLine);
                 //mMap.addPolyline(routeLine);









    }
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        /*super.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //finish();
                    Toast.makeText(MapsActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private class FetchUrl2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                System.out.println("innn");
                System.out.println(url[0]);
                HttpURLConnection connection = (HttpURLConnection) new URL(url[0]).openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
                System.out.println("response :"+response);
                // parse the JSON route data
                JsonObject json = new Gson().fromJson(response, JsonObject.class);
                System.out.println(json);
                JsonArray coordinates = json.getAsJsonArray("features")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("geometry")
                        .getAsJsonArray("coordinates");

                // create a polyline options object to store the route
                PolylineOptions routeLine = new PolylineOptions().color(Color.RED);
                System.out.println("lllll ");
                // add the route coordinates to the polyline
                for (int i = 0; i < coordinates.size(); i++) {
                    JsonArray coord = coordinates.get(i).getAsJsonArray();
                    routeLine.add(new LatLng(coord.get(1).getAsDouble(), coord.get(0).getAsDouble()));
                }
                System.out.println(coordinates);
                // add the route to the map

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(routeLine);

                        // Your code to run in GUI thread here
                    }
                });
            } catch (Exception e) {
                // handle error
                System.out.println("ddddd "+e);
            }
            return data;
        }

        //@Override
        //protected void onPostExecute(String result) {
        //    super.onPostExecute(result);

        //    ParserTasks parserTask = new ParserTasks();

        // Invokes the thread for parsing the JSON data
        //    parserTask.execute(result);

        //}


    }








    @Override
    public void onLocationChanged(Location location) {

        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(lattitude, longitude);


        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.setPosition(latLng);
            Toast.makeText(MapsActivity.this, "New location"+latLng.latitude, Toast.LENGTH_SHORT).show();
            System.out.println("jhghfjhgchj");
        }else{


        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        }

    }




}