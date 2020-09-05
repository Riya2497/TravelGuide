package com.jignesh.streety;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.jignesh.streety.Model.Places;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ARActivity extends AppCompatActivity implements OnClickBeyondarObjectListener,OnMapReadyCallback, LocationListener {

    private BeyondarFragmentSupport beyondArFragment;
    ArrayList<GeoObject> myGeoObjList = new ArrayList<>();
    ArrayList<Places> placesList = new ArrayList<>();
    World world;
    LocationManager man;
    double latitude, longitude;
    Location loc;
    boolean isGps,isNet;


    // TODO: 10/26/2018 1) Check that AR code   2) Add location code    3) Check for permission.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        if (getIntent() != null) {
            if (getIntent().hasExtra("allplaces")) {
                placesList = getIntent().getParcelableArrayListExtra("allplaces");
            }
        }

        final SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) // Checking for Camera
                && mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null // Checking for Accelerometer
                && mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) // Checking for Magnetic Sensor
        {
            Log.d("error", "onCreate: supported");
            // Supported
        } else {
            // Not supported
            // Toast with proper message
            Toast.makeText(this, "AR is not supported on your device.", Toast.LENGTH_SHORT).show();
            finish();
            Log.d("error", "onCreate: not supported");
            return;
        }

        beyondArFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        createWorld();
    }


    void createWorld() {
        //--------------------------
        // This distance will set icons away from users as the value increases.
        beyondArFragment.setPushAwayDistance(10);


        // Creating a world where all objects will be put.
        // world = new World(ARActivity.this);

        world = new World(getApplicationContext());


        // Default icon.
        // This is useful if you are loading images from Internet and the connection get lost.
        world.setDefaultBitmap(R.drawable.default_icon, World.LIST_TYPE_DEFAULT);

        // Setting user's position in the World.
        //world.setGeoPosition(22.468705, 73.076045);
        //world.setLocation();
        man = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGps = man.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGps) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            man.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, (LocationListener) this);
            if (man != null) {
                loc = man.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    world.setGeoPosition(latitude, longitude);
                }
            }
        }
        else {
            Toast.makeText(this, "error!!!", Toast.LENGTH_SHORT).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        createGeoObjectsList();

        // Adding Wold data in to beyondArFragment.
        beyondArFragment.setWorld(world);


        // Setting Click Event Listener and callbacks will be received in 'onClickBeyondarObject'.
        beyondArFragment.setOnClickBeyondarObjectListener(this);
        //--------------------------
    }


    void createGeoObjectsList() {
        // myGeoObjList.clear();
        //Log.d("Error", "my latlng: " + myLocation.latitude + " " + myLocation.longitude);
        //MyGeoObject myGeoObject;
        //------------------
        // myGeoObject = new MyGeoObject(1);

//        myGeoObject.setGeoPosition(22.311797, 73.196928);

        // Drawable is available
        //      myGeoObject.setImageResource(R.drawable.baby);


        //    myGeoObject.setName("Test icon");

        //  myGeoObjList.add(myGeoObject);
        //------------------
/*
        GeoObject go1 = new GeoObject(1l);
        go1.setGeoPosition(22.463890, 73.075797);
        go1.setImageResource(R.drawable.residence);
        go1.setName("civil");
        myGeoObjList.add(go1);
        world.addBeyondarObject(go1);

        GeoObject go2 = new GeoObject(2l);
        go2.setGeoPosition(22.466880, 73.075973);
        go2.setImageResource(R.drawable.shop);
        go2.setName("it");
        myGeoObjList.add(go2);
        world.addBeyondarObject(go2);

        GeoObject go3 = new GeoObject(3l);
        go3.setGeoPosition(22.468704, 73.076140);
        go3.setImageResource(R.drawable.book);
        go3.setName("library");
        myGeoObjList.add(go3);
        world.addBeyondarObject(go3);

        GeoObject go4 = new GeoObject(4l);
        go4.setGeoPosition(22.468785, 73.076294);
        go4.setImageResource(R.drawable.rest);
        go4.setName("gcr");
        myGeoObjList.add(go4);
        world.addBeyondarObject(go4);*/

       for(int i=0;i<placesList.size();i++)
        {
            double lat = placesList.get(i).getLat();
            double lon = placesList.get(i).getLon();
            String name = placesList.get(i).getType();
            GeoObject geoObject = new GeoObject(i+1);
            geoObject.setGeoPosition(lat, lon);
            switch(name) {
                case "Shop" :
                    geoObject.setImageResource(R.drawable.shop);
                    break;
                case "University" :
                    geoObject.setImageResource(R.drawable.university);
                    break;
                case "Parking" :
                    geoObject.setImageResource(R.drawable.parking);
                    break;
                case "Residence" :
                    geoObject.setImageResource(R.drawable.residence);
                    break;
                case "Garden" :
                    geoObject.setImageResource(R.drawable.garden);
                    break;
                case "Monument" :
                    geoObject.setImageResource(R.drawable.monument);
                    break;
                case "Dam" :
                    geoObject.setImageResource(R.drawable.dam);
                    break;
                case "Resort" :
                    geoObject.setImageResource(R.drawable.resort);
                    break;
                case "Temple" :
                    geoObject.setImageResource(R.drawable.temple);
                    break;
                case "Railway Station" :
                    geoObject.setImageResource(R.drawable.railway_station);
                    break;
                case "Forest" :
                    geoObject.setImageResource(R.drawable.forest);
                    break;
                case "Lab" :
                    geoObject.setImageResource(R.drawable.lab);
                    break;
                case "Restaurant" :
                    geoObject.setImageResource(R.drawable.restaurant2);
                    break;
                case "Book" :
                    geoObject.setImageResource(R.drawable.book);
                    break;
                case "Money" :
                    geoObject.setImageResource(R.drawable.money);
                    break;
                case "Rest" :
                    geoObject.setImageResource(R.drawable.rest);
                    break;
                case "Sports" :
                    geoObject.setImageResource(R.drawable.sports);
                    break;
                case "Wc" :
                    geoObject.setImageResource(R.drawable.washroom);
                    break;
                case "Telephone" :
                    geoObject.setImageResource(R.drawable.telephone);
                    break;

            }
            geoObject.setName(placesList.get(i).getText());
            myGeoObjList.add(geoObject);

        }
        // Adding GeoObjects to the world
        for (GeoObject geoObject : myGeoObjList) {
            world.addBeyondarObject(geoObject);
        }

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
        if(arrayList.size()>0)
        {
            String name=arrayList.get(0).getName();
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        isNet = man.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
                    world.setGeoPosition(latitude, longitude);
                }
            }
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}


