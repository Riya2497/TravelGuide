package com.jignesh.streety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jignesh.streety.Adapter.ChangeRouteAdapter;
import com.jignesh.streety.Adapter.TripsAdapter;
import com.jignesh.streety.Interface.APIManager;
import com.jignesh.streety.Interface.RecyclerViewClickListener;
import com.jignesh.streety.Model.Places;
import com.jignesh.streety.Model.Trips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripsActivity extends AppCompatActivity implements RecyclerViewClickListener {

    ArrayList<Trips> mTrips = new ArrayList<>();

    RecyclerView mRecyclerView, rv1;
    SharedPref sharedPref;
    int trip_count;
    String email;
    TripsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        sharedPref = new SharedPref(this);
        trip_count=sharedPref.getTripCount();
        email=sharedPref.getEmailValue();
        Log.d("EMAIL", email);
        mRecyclerView = findViewById(R.id.recycler_view1);
        mRecyclerView.setHasFixedSize(true);

        viewTripsAPI();

    }

    void viewTripsAPI() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.viewtripsC(params);

        final ProgressDialog progressDialog = new ProgressDialog(TripsActivity.this);
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
                            mTrips = gson.fromJson(content.get("trip_name").getAsJsonArray().toString(),
                                    new TypeToken<ArrayList<Trips>>() {
                                    }.getType());
                            mAdapter = new TripsAdapter(mTrips);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(TripsActivity.this));
                            mRecyclerView.setAdapter(mAdapter);


                            // PlacesAdapter adapter = new PlacesAdapter(placesList, isFromMaps, MapsActivity.this, MapsActivity.this);
                            //mRecyclerView.setAdapter(adapter);
                        }

                    } else {
                        Toast.makeText(TripsActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                        Log.d("error", "false ");
                    }
                } catch (Exception e) {
                    Toast.makeText(TripsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                    Log.d("error", "false ");

                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(TripsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                Log.d("error", "false ");


                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    public void onItemClick(int position)
    {

        String name=mTrips.get(position).getText();

        Intent ii = new Intent(this, NavigationActivity.class);
        ii.putExtra("trip_name",name);
        startActivity(ii);
    }

}
