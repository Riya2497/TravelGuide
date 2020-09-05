package com.jignesh.streety;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jignesh.streety.Adapter.PlacesAdapter;
import com.jignesh.streety.Interface.APIManager;
import com.jignesh.streety.Interface.RecyclerViewClickListener;
import com.jignesh.streety.Model.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnFailureListener, View.OnClickListener,RecyclerViewClickListener {

    Button btDone,btAddLoc;
    private GoogleMap mMap;
    LocationManager man;
    Location loc;
    double lati, longi ;
    String area, soc, city, state, country, pin, dno;
    boolean isNet, isGps;
    Address address;
    Geocoder geo;
    boolean first = true;
    LatLng prev , current;
    SupportMapFragment mapFragment ;
    String item;
    Boolean isFromMaps=true;
    RecyclerView mRecyclerView;

    ArrayList<Places> placesList = new ArrayList<>();
    ArrayList<Places> allplacesList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mappingWidgets();
        addListeners();

        if (getIntent() != null) {
            if (getIntent().hasExtra("location")) {
                item = getIntent().getStringExtra("location");
            }
        }

        listPlacesAPI();
        //add list items
        /*placesList.add(new Places(Places.PLACES_CARD, "Inorbit Mall", R.drawable.download, false, 22.3223, 73.1655));
        placesList.add(new Places(Places.PLACES_CARD, "Eva Mall", R.drawable.evamall, false, 22.2731, 73.1895));
        placesList.add(new Places(Places.PLACES_CARD, "Kamati Baugh", R.drawable.kamatibaug, false, 22.3115, 73.1914));
        placesList.add(new Places(Places.PLACES_CARD, "Laxmi Vilas Palace", R.drawable.laxmivilas, false, 22.2936, 73.1919));
        placesList.add(new Places(Places.PLACES_CARD, "Statue of unity", R.drawable.statueofunity, false, 21.8380, 73.7191));
        placesList.add(new Places(Places.PLACES_CARD, "Narmada Dam", R.drawable.narmadadam, false, 21.8285, 73.7494));
        placesList.add(new Places(Places.PLACES_CARD, "Orsang resort", R.drawable.orsang, false, 22.0000, 73.4739));
        placesList.add(new Places(Places.PLACES_CARD, "Pavagadh", R.drawable.pavagadh, false, 22.4620, 73.5241));
        placesList.add(new Places(Places.PLACES_CARD, "Vadodara Railway Station", R.drawable.vadodararailwaystation, false, 22.3108, 73.1809));
        placesList.add(new Places(Places.PLACES_CARD, "Mumbai Central", R.drawable.mumbaicentral, false, 18.9690, 72.8205));
        placesList.add(new Places(Places.PLACES_CARD, "Gir", R.drawable.gir, false, 21.1243, 70.8242));
        placesList.add(new Places(Places.PLACES_CARD, "Church gate", R.drawable.churchgate, false, 18.9322, 72.8264));
        placesList.add(new Places(Places.PLACES_CARD, "Hawa Mahal", R.drawable.jaipur, false, 26.9239, 75.8267));
*/

        man = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGps = man.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGps) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, (LocationListener) this);
            if (man != null) {
                loc = man.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    lati = loc.getLatitude();
                    longi = loc.getLongitude();
                }
            }

        }
        else {
            Toast.makeText(this, "error!!!", Toast.LENGTH_SHORT).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("google", "Map loaded");

        mMap = googleMap;

        Toast.makeText(getApplicationContext(), "IN MAP", Toast.LENGTH_LONG).show();
        if (first) {
            prev = new LatLng(lati, longi);
            first = false;
        }

        // Add a marker in Sydney and move the camera
        current = new LatLng(lati, longi);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(current).title("current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //Polyline line = mMap.addPolyline(new PolylineOptions().add(prev, current).width(5).color(Color.RED));
        prev = current;
    }

    void loadMarkers() {
               if (mMap != null) {
            mMap.clear();
            for (Places place : placesList) {
                if (place.isChecked()) {
                    current = new LatLng(place.getLat(), place.getLon());
                    mMap.addMarker(new MarkerOptions().position(current));
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void mappingWidgets() {
        btDone = findViewById(R.id.btDone);
        btAddLoc=findViewById(R.id.btAddLocation);
    }

    private void addListeners() {
        btDone.setOnClickListener(this);
        btAddLoc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btAddLocation:
                intent = new Intent(getApplicationContext(), PlacesActivity.class);
                intent.putExtra("places", placesList);
                intent.putExtra("location", item);
                intent.putExtra("isFromMaps", true);
                startActivityForResult(intent, 1000);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);



                break;
            case R.id.btDone:
                intent = new Intent(getApplicationContext(), NavigationActivity.class);
                ArrayList<Places> filterPlaces=new ArrayList<>();
                for (Places place : placesList) {
                    if (place.isChecked()) {
                        filterPlaces.add(place);
                    }
                }
                intent.putExtra("places", filterPlaces);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("allplaces", allplacesList);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;


        }
    }

    @Override
    public void onFailure( Exception e) {
        Log.d("google", "Map failed: " + e.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.hasExtra("places")) {
                    placesList = data.getParcelableArrayListExtra("places");

                    //loadMarkers();
                }
            }
            loadMarkers();
        }
    }

    void listPlacesAPI() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
        params.put("place_name", item);

        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.getPlaces(params);

        final ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    // Read response as follow
                    if (response != null && response.body() != null) {
                        //Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "onResponse: body: " + response.body());

                        // Read response as follow
                        Map<String, Object> map = response.body();

                        // Converting response map to JsonObject
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(map);

                        Log.d("error", "jsonString: " + jsonString);

                        JsonObject content = gson.fromJson(jsonString, JsonObject.class);

                       /* if (content.has("message")) {
                            Toast.makeText(PlacesActivity.this,
                                    content.get("message").getAsString(),
                                    Toast.LENGTH_SHORT).show();
                        }*/

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {

                            Log.d("error", "false ");
                            // Convert JsonArray to your custom model class list as follow
                            placesList = gson.fromJson(content.get("places_array").getAsJsonArray().toString(),
                                    new TypeToken<ArrayList<Places>>() {
                                    }.getType());
                            allplacesList=placesList;
                           // PlacesAdapter adapter = new PlacesAdapter(placesList, isFromMaps, MapsActivity.this, MapsActivity.this);
                            //mRecyclerView.setAdapter(adapter);
                        }

                    } else {
                        Toast.makeText(MapsActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                        Log.d("error", "false ");
                    }
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                    Log.d("error", "false ");

                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(MapsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                Log.d("error", "false ");


                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}
