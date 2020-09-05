package com.jignesh.streety;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jignesh.streety.Interface.APIManager;
import com.jignesh.streety.Model.Places;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    String place_name;
    ArrayList<Places> placesList = new ArrayList<>();
    ImageView ivPlace;
    TextView tvPlace, tvDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mappingWidgets();
        if (getIntent() != null) {
            if (getIntent().hasExtra("place_name")) {
                place_name = getIntent().getStringExtra("place_name");
            }
        }
        detailsAPI();

    }

    private void mappingWidgets()
    {
        ivPlace=findViewById(R.id.ivPlace);
        tvPlace=findViewById(R.id.tvPlace);
        tvDesc=findViewById(R.id.tvDesc);

    }

    void detailsAPI () {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL) // Base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Sending param
            Map<String, String> params = new HashMap<>();
            params.put("place_name", place_name);

            // Initializing APIManager
            APIManager api = retrofit.create(APIManager.class);

            // TODO: Note: Replace 'getDetails(param)' API method for every new API here
            Call<Map<String, Object>> call = api.getPlaces(params);

            final ProgressDialog progressDialog = new ProgressDialog(DetailsActivity.this);
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

                                for (Places place : placesList) {
                                    String tvname=place.getText();
                                    String tvdesc=place.getDesc();
                                    String url=place.getData();
                                    tvPlace.setText(tvname);
                                    tvDesc.setText(tvdesc);
                                    ImageLoader.getInstance().displayImage(url, ivPlace);
                                    }
                                }


                        }
                        else
                            {
                            Toast.makeText(DetailsActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                            Log.d("Error", "No response available");
                            Log.d("error", "false ");
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                        Log.d("Error", "Error in reading response: " + e.getMessage());
                        Log.d("error", "false ");

                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(DetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    Log.d("error", "false ");


                    Log.d("Error", "onFailure: " + t.getMessage());
                }
            });
        }

    }

