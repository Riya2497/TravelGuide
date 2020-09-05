package com.jignesh.streety;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jignesh.streety.Model.Places;

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

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    private GoogleMap map;
    LocationManager man;
    boolean isGps;
    double latitude, longitude;
    LatLng position;
    Location loc;
    ArrayList<Places> placesList = new ArrayList<>();
    ArrayList<Places> allplacesList = new ArrayList<>();
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;
    MarkerOptions markerOptions = new MarkerOptions();
    String distance = "";
    String duration = "";

    private ProgressDialog progressDialog;
    private LatLng current;
    Button btOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        if (getIntent() != null) {
            if (getIntent().hasExtra("places")) {
                placesList = getIntent().getParcelableArrayListExtra("places");
            }
        }
        if (getIntent() != null) {
            if (getIntent().hasExtra("allplaces")) {
                allplacesList = getIntent().getParcelableArrayListExtra("allplaces");
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        man = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGps = man.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGps) {
            if (ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, (LocationListener) this);
            if (man != null) {
                loc = man.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    double lati = loc.getLatitude();
                    double longi = loc.getLongitude();
                    current = new LatLng(lati, longi);
                }
            }
        }

        Button btChangeRoute = findViewById(R.id.btChangeRoute);
        btChangeRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(getApplicationContext(), ChangeRouteActivity.class);
                ii.putExtra("places", placesList);
                startActivity(ii);
            }
        });

        Button btAR = findViewById(R.id.btAR);
        btAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(getApplicationContext(), ARActivity.class);
                ii.putExtra("allplaces", allplacesList);
                startActivity(ii);
            }
        });

        btOk = findViewById(R.id.btOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(ii);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO: Step-8: Override onMapReady method and handle loaded map here
        map = googleMap;

        // TODO: Step-10: Show markers of locations on map and set zoom level in such a way that all markers can be visible in screen
        showMarkers();

        // TODO: Step-12: Call API from button click or in similar other way
        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current == null) {
                    Toast.makeText(NavigationActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                } else {
                    if (placesList.size() >= 2) {
                        ArrayList<LatLng> latLngList = new ArrayList<>();
                        for (int i = 0; i < placesList.size() - 1; i++) {
                            latLngList.add(new LatLng(placesList.get(i).getLat(), placesList.get(i).getLon()));
                        }

                        String url = getDirectionsUrl(current,
                                new LatLng(placesList.get(placesList.size() - 1).getLat(), placesList.get(placesList.size() - 1).getLon()),
                                latLngList);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    } else {
                        Toast.makeText(NavigationActivity.this, "At least two locations are required for drawing route", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    // TODO: Step-11: Add following methods and async tasks

    /**
     * Get direction url for Google Direction API.
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest, List<LatLng> wayPointList) {
        // Building the parameters
        String parameters = "key=" + getString(R.string.google_maps_key)
                + "&" + "origin=" + origin.latitude + "," + origin.longitude
                + "&" + "destination=" + dest.latitude + "," + dest.longitude
                + "&" + "sensor=false";

        String wayPointLatLngs = "";

        if (wayPointList.size() > 0) {
            StringBuilder wayPointLatLngBuilder = new StringBuilder();

            for (LatLng wayPoint : wayPointList) {
                wayPointLatLngBuilder.append(wayPoint.latitude).append(",").append(wayPoint.longitude).append("|");
            }

            if (wayPointLatLngBuilder.toString().length() > 1) {
                wayPointLatLngs = wayPointLatLngBuilder.toString()
                        .substring(0, wayPointLatLngBuilder.toString().length() - 1);
            }
        }

        if (!wayPointLatLngs.isEmpty()) {
            parameters += "&" + "waypoints=" + wayPointLatLngs;
        }

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
    }

    /**
     * Adds markers in map.
     */
    private void showMarkers() {
        for (Places place : placesList) {
            if (place.isChecked()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < placesList.size(); i++) {

                    map.addMarker(new MarkerOptions().position(new LatLng(placesList.get(i).getLat(), placesList.get(i).getLon())));

                    // Adding location in bounds
                    builder.include(new LatLng(placesList.get(i).getLat(), placesList.get(i).getLon()));
                }

                final LatLngBounds bounds = builder.build();

                // Waiting for map to be loaded properly
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                                getResources().getDimensionPixelSize(R.dimen.map_bounds_padding))); // Padding for markers
                    }
                });
            }
        }
    }

    /**
     * Downloads json data from url.
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder stringBuilder = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error", "Exception while downloading url: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        Log.d("Error", "downloadUrl: " + data);

        return data;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        /*        man=(LocationManager)getSystemService(LOCATION_SERVICE);
        isGps = man.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGps) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            if (man != null) {
                loc = man.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    LatLng position = new LatLng(latitude, longitude);
                    for(int i=0;i<points.size();i++)
                    {
                        //TODO : changed from 1to 0
                        if(position.latitude==points.get(i).latitude
                                && position.longitude==points.get(i).longitude)
                        {
                            points.remove(i);
                            break;
                        }
                    }

                    map.clear();

                    showMarkers();

                    lineOptions.addAll(points);
                    lineOptions.width(getResources().getDimension(R.dimen.route_thickness));
                    lineOptions.color(Color.BLACK);
                    map.addPolyline(lineOptions);

                }
            }
        }*/
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

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(NavigationActivity.this);
            }

            progressDialog.setMessage(getString(R.string.please_wait_));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            if (map == null) {
                return;
            }

            map.clear();

            showMarkers();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                //TODO : changed from 1 to 0
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    position = new LatLng(lat, lng);

                    points.add(position);
                }
                points.add(current);








                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(getResources().getDimension(R.dimen.route_thickness));
                lineOptions.color(Color.BLACK);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
}
