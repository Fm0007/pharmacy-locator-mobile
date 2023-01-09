package com.mehdi.pharmacie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements PharmacieAdapter.OnItemClickListener {

    public static final String SPEC_JSON = "https://ensaj.aei.social/Archives/response_1671478979303.json";
    double currentLat = 0, currentLong = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView emptyStateTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PharmacieAdapter adapter;
    private ArrayList<Pharmacie> pharmaciesList;
    private RecyclerView rvPharmacies;
    private RequestQueue mRequestQueue;

    String apikey="AIzaSyDj4MYPVlscrsLNaJpMFR7YMgp4pPjP_Jk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyStateTextView = findViewById(R.id.empty_view);





        contentSetter();

        Log.e("RequestQueue", "" + mRequestQueue);


        // *** Connectivity Checker ***
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            emptyStateTextView.setText("");
            Log.e("JSON 1", " ----------- ");
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCurrentLocation();
            }



        } else {

            emptyStateTextView.setText(R.string.no_internet_connection);
            System.out.println("no connection internet");
        }


        //Swipe to Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Toast.makeText(MainActivity.this, "Inside the SwipeRefresher",Toast.LENGTH_SHORT).show();


                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {


                    Log.e("Swipe to Refresh", "Connection available");

                    emptyStateTextView.setText("");
                    pharmaciesList.clear();

                    Log.e("JSON 2", " ********************* ");
                    //parseJSON();// -------------------------------------------------

                } else {

                    //Toast.makeText(MainActivity.this,"NOT CONNECTED SWIPE",Toast.LENGTH_SHORT).show();
                }

                pharmaciesList = new ArrayList<>();


                //mRequestQueue = Volley.newRequestQueue(MainActivity.this);
                //adapter = new PharmacieAdapter(MainActivity.this,pharmaciesList);
                //rvPharmacies.setAdapter(adapter);
                Log.e("JSON 3", " #################### ");
                parseJSON();

                emptyStateTextView.setText("");


                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // *** Swipable Parts ***
        swipablePart();


        // *** Ads part ***


    }







    /*     */

    private void contentSetter() {

        emptyStateTextView = findViewById(R.id.empty_view);

        rvPharmacies = findViewById(R.id.rvPharmacies);

        rvPharmacies.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        rvPharmacies.setLayoutManager(llm);

        pharmaciesList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);


    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /*     */

    private void parseJSON() {


        Log.e("ParseJSON", "Method ParseJSON");
        JsonArrayRequest request = new JsonArrayRequest(SPEC_JSON,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jObject = jsonArray.getJSONObject(i);

                                String nom = jObject.getString("nom");
                                //Log.e("nom", nom);
                                //Log.e("SONIC",""+nom);
                                String zone = jObject.getJSONObject("zone").getString("nom");
                                String adresse = jObject.getString("adresse");
                                String telephone = "+212666666666";
                                String coordonnee = jObject.getString("lat")+","+jObject.getString("log");
                                String etat = "Ouvert";

                                Pharmacie pharmacie = new Pharmacie(nom, zone, adresse.toLowerCase(), telephone, coordonnee,etat);
                                double lating = Double.parseDouble(pharmacie.getCoordonnee().split(",", 2)[0]);
                                double longing = Double.parseDouble(pharmacie.getCoordonnee().split(",", 2)[1]);

                                double dis=distance(currentLat, currentLong, lating, longing);
                                if (true) {
                                    pharmacie.setDistance(dis);
                                    pharmaciesList.add(pharmacie);

                                }


                            }
                            Collections.sort(pharmaciesList, Pharmacie.ComparatorDistance);

                            //Log.e("data", pharmaciesList.toString());
                            Log.e("ParseJSON", "Adapter ParseJSON");
                            adapter = new PharmacieAdapter(MainActivity.this, pharmaciesList);
                            rvPharmacies.setAdapter(adapter);

                            // Setting up a divider
                            //RecyclerView.ItemDecoration divider = new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL);
                            //rvPharmacies.addItemDecoration(divider);

                            // Ajout du ClickListener
                            adapter.setOnItemClickListener(MainActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }


    // Click + Geolocation
    @Override
    public void onItemClick(int position) {


        Pharmacie clickedItem = pharmaciesList.get(position);



        // Testing the result
        String mylatlong = currentLat+","+currentLong;
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.putExtra("recycleview",clickedItem.getCoordonnee());
        mapIntent.putExtra("mylatlong",mylatlong);
        mapIntent.putExtra("cle",apikey);


        startActivity(mapIntent);


    }



    public void swipablePart() {

        // Swipable part
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // Swipe movement
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //Get the phone number of the swiped Item from the recyclerview
                Pharmacie clickedItem = pharmaciesList.get(viewHolder.getAdapterPosition());


                Intent call_button = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+clickedItem.getTelephone()));

                // call the dial intent
                startActivity(call_button);

                // repopulate the recyclerview not to dismiss the data
                adapter.clear();
                rvPharmacies.setAdapter(adapter);

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getHeight();

                // Setting up the background
                final ColorDrawable background = new ColorDrawable(Color.GREEN);
                background.setBounds(16,itemView.getTop(),itemView.getLeft() + (int) dX, itemView.getBottom());
                background.draw(c);

                // Setting up the icon image
                final Drawable dial_icon = ContextCompat.getDrawable(MainActivity.this,R.drawable.call_white_24dp);
                int intrinsicWidth = 0;
                int intrinsicHeight = 0;

                intrinsicWidth = dial_icon.getIntrinsicWidth();
                intrinsicHeight = dial_icon.getIntrinsicHeight();

                int dial_iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int dial_iconBottom = dial_iconTop + intrinsicHeight;


                dial_icon.setBounds(itemView.getLeft()+48, dial_iconTop, itemView.getLeft()+48 + intrinsicWidth, dial_iconBottom);
                dial_icon.draw(c);


                // changing the opacity
                viewHolder.itemView.setAlpha(0.5f);

                // Put back the opacity to full when is swiped back
                if(dX == 0){
                    viewHolder.itemView.setAlpha(1f);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }

            // limit at which the call to intent gets triggered
            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.7f;
            }


        }).attachToRecyclerView(rvPharmacies);

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        //Log.e("OnRestart","Restartu");

        adapter = new PharmacieAdapter(MainActivity.this, pharmaciesList);
        rvPharmacies.setAdapter(adapter);
        Log.e("JSON 4", "!!!!!!!!!!!!!!!!");
        parseJSON();


    }

    @Override
    public void onResume() {
        super.onResume();
        emptyStateTextView.setText("");
        pharmaciesList.clear();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void getCurrentLocation() {

        if (!isLocationEnabled(MainActivity.this)){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            currentLat = lati;
                            currentLong = longi;

                            parseJSON();

                        } else {

                        }
                    }
                }, Looper.getMainLooper());

    }





}