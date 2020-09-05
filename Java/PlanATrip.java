package com.jignesh.streety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jignesh.streety.Interface.APIManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Integer.parseInt;

public class PlanATrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {


    Spinner spLocation;
    TextView etDate;
    Button bt_go;
    SharedPref sharedPref;
    String item;
    String email;
    String fbid;
    String gid;
    //CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_atrip);
        mappingWidgets();
        sharedPref = new SharedPref(this);
        Boolean b1 = sharedPref.getEmail();
        Boolean b2 = sharedPref.getFBId();
        Boolean b3 = sharedPref.getGoogleId();
        email=sharedPref.getEmailValue();
        fbid = sharedPref.getFBIdValue();
        gid = sharedPref.getGoogleIdValue();

        ArrayList<String> location= new ArrayList<String>();
        location.add("--SELECT--");
        location.add("Vadodara");
        location.add("SVIT");
        ArrayAdapter<String> locAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,location);

        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLocation.setAdapter(locAdapter);

        spLocation.setOnItemSelectedListener(this);


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd;
                dpd = DatePickerDialog.newInstance(
                        PlanATrip.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.setMinDate(now);
                dpd.setTitle("Date Picker Dialog");
                dpd.setAccentColor("#C71585");
                dpd.setThemeDark(true);
                dpd.show(getFragmentManager(),"DatePicker");

            }
        });
        bt_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    insertTripAPI();
                    /*Intent ii = new Intent(getApplicationContext(), MapsActivity.class);
                    ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ii);*/

            }
        });
    }
    void mappingWidgets()
    {
        spLocation=findViewById(R.id.spLocation);
        etDate=findViewById(R.id.etDate);
        bt_go=findViewById(R.id.bt_go);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        int month=monthOfYear+1;
        //String date=""+dayOfMonth+"/"+month+"/"+year;
        String date=""+year+"-"+month+"-"+dayOfMonth+" 00:00:00" ;
        etDate.setText(date);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    void insertTripAPI(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Date date=Calendar.getInstance().getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dt= df.format(date);
        Map<String, String> params = new HashMap<>();

        params.put("email", email);
        params.put("trip_name", item);
        params.put("trip_date",etDate.getText().toString().trim() );
        params.put("created_date",dt);
        params.put("fb_id",fbid);
        params.put("google_id",gid);

        Log.d("error", "email" + email);
        Log.d("error", "trip_name" + item);
        Log.d("error", "trip_date" + etDate.getText().toString().trim());
        Log.d("error", "created_date" + dt);

        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.inserttripdetailC(params);

        final ProgressDialog progressDialog = new ProgressDialog(PlanATrip.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
               // try {
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

                        /*Toast.makeText(PlanATrip.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();*/
                        Log.d("status",content.get("status").getAsString());

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {
                            Log.d("status",content.get("status").getAsString());
                            Intent ii = new Intent(getApplicationContext(), MapsActivity.class);
                            ii.putExtra("location", item);
                            setResult(RESULT_OK, ii);
                            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(PlanATrip.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
                /*} catch (Exception e) {
                    Toast.makeText(PlanATrip.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }*/
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(PlanATrip.this, "Failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
}

