package com.jignesh.streety;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jignesh.streety.Interface.APIManager;
import com.jignesh.streety.Model.Places;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity {

TextView tvPlaces,tvPat,tvTrips;
Button btProfile;
ImageView titlelogo;
    ArrayList<Places> placesList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState)


    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mappingWidgets();
        addListeners();

    }

    private void addListeners() {
        tvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAllPlacesAPI();


            }
        });
        tvPat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(),PlanATrip.class);
                startActivity(ii);

            }
        });
        tvTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(),TripsActivity.class);
                startActivity(ii);

            }
        });
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(ii);

            }
        });

    }

    private void mappingWidgets()
    {
        tvPlaces=findViewById(R.id.tvPlaces);
        tvPat=findViewById(R.id.tvPat);
        tvTrips=findViewById(R.id.tvTrips);
        btProfile=findViewById(R.id.btProfile);
        titlelogo=findViewById(R.id.titlelogo);
    }
    void listAllPlacesAPI() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
       Map<String, String> params = new HashMap<>();
       // params.put("place_name", item);

        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.listAllC(params);

        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
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

                            Intent  intent = new Intent(getApplicationContext(), PlacesActivity.class);
                            intent.putExtra("places", placesList);
                            intent.putExtra("isFromMaps", false);
                            startActivityForResult(intent, 1000);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }

                    } else {
                        Toast.makeText(HomeActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                        Log.d("error", "false ");
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                    Log.d("error", "false ");

                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                Log.d("error", "false ");


                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
}




