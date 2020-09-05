package com.jignesh.streety;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jignesh.streety.Interface.APIManager;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    TextView tvTripsMade,tvUserName,tvCount;
    Button btEdit,btSignOut;
    EditText etName;
    CircleImageView cvImage;
    SharedPref sharedPref;
    String email,fb_id,google_id;
    Boolean fb,em,g;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mappingWidgets();

        sharedPref = new SharedPref(this);
        String user=sharedPref.getUsername();
        email=sharedPref.getEmailValue();
        em=sharedPref.getEmail();
        fb=sharedPref.getFBId();
        g=sharedPref.getGoogleId();
       Log.d("Email",email);
        fb_id=sharedPref.getFBIdValue();
        google_id=sharedPref.getGoogleIdValue();
        CountTripsAPI();
        if(!em) {
            tvUserName.setText(email);
        }
        if(!fb)
        {
            tvUserName.setText(user);
        }
        tvTripsMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ii=new Intent(getApplicationContext(),TripsActivity.class);
                startActivity(ii);
            }
        });

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,1888);      //1888 Camera request
            }
        });

        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.setEmail("",false);
                sharedPref.setFBId("",false);
                sharedPref.setGoogleId("",false);
                Intent ii=new Intent(getApplicationContext(),LoginActivity.class);
                ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ii);
                finish();
            }
        });

    }

    void CountTripsAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Sending param
        Map<String, String> params = new HashMap<>();
        Log.d("Email",email);
        params.put("email",email);
        params.put("fb_id",fb_id);
        params.put("google_id",google_id);


        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.countTripC(params);

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
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

                        /*Toast.makeText(ProfileActivity.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();*/

                        // TODO: Read response here
                        if (content.get("status").getAsInt() != 0) {
                           int trips=content.get("trips").getAsInt();
                           //Log.d("trips",trips);
                           tvCount.setText(""+trips);
                           sharedPref.setTripCount(trips);
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(ProfileActivity.this, "No trips made.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
               } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
    public void mappingWidgets() {
        tvTripsMade=findViewById(R.id.tvTripsmade);
        tvUserName=findViewById(R.id.tvUsername);
        tvCount=findViewById(R.id.tvCount);
        btEdit=findViewById(R.id.btEdit);
        cvImage=findViewById(R.id.cvImage);
        etName=findViewById(R.id.etName);
        btSignOut=findViewById(R.id.btSignOut);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1888 && resultCode==Activity.RESULT_OK)
        {
            Bitmap photo= (Bitmap)data.getExtras().get("data");
            cvImage.setImageBitmap(photo);
        }
    }
}
