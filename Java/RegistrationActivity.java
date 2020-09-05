package com.jignesh.streety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jignesh.streety.Interface.APIManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity
{
    EditText etName,etEmail,etPwd,etConfirmPwd;
    Button btnSignUp;
    SharedPref sharedPref;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mappingWidgets();

      btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    registerAPI();
                }
            }
        });
    }

    private void mappingWidgets()
    {
        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etPwd=findViewById(R.id.etPassword);
        etConfirmPwd=findViewById(R.id.etConfirmPassword);
        btnSignUp=findViewById(R.id.btnSignUp);
    }


    public boolean checkValidation()
    {
        boolean isValid=true;
        if(etName.getText().toString().trim().isEmpty())
        {
            etName.setError(getString(R.string.error_name));
            isValid=false;
        }
        else if(etEmail.getText().toString().trim().isEmpty())
        {
            etEmail.setError(getString(R.string.error_email));
            isValid=false;
        }
        else if(!validateEmail(etEmail.getText().toString()))
        {
            etEmail.setError(getString(R.string.error_email));
            isValid=false;
        }
        else if(etPwd.getText().toString().trim().isEmpty())
        {
            etPwd.setError(getString(R.string.error_pwd));
            isValid=false;
        }
        else if(etConfirmPwd.getText().toString().trim().isEmpty())
        {
            etConfirmPwd.setError(getString(R.string.error_confirmPwd));
            isValid = false;
        }

        else if(etPwd.getText().toString().equals(etConfirmPwd.getText().toString())) {
            isValid=true;
            Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show();
        }
        else
        {
            etConfirmPwd.setError(getString(R.string.error_confirmPwd));
            isValid = false;
        }
        return isValid;
    }
    public boolean validateEmail(String etEmail) {
        Matcher matcher;
        String email="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"+"\\@"+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"+"("+"\\."+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"+")+";
        Pattern pattern=Pattern.compile(email);
        matcher=pattern.matcher(etEmail);
        return matcher.matches();
    }

    void registerAPI(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
        params.put("name",etName.getText().toString().trim());
        params.put("email", etEmail.getText().toString().trim());
        params.put("password", etPwd.getText().toString().trim());

        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        Call<Map<String, Object>> call = api.register(params);

        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
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

                        Toast.makeText(RegistrationActivity.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {
                            sharedPref = new SharedPref(getApplicationContext());
                            sharedPref.setUsername(etName.getText().toString());
                            Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
                            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                            finish();
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(RegistrationActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(RegistrationActivity.this, " Registration failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
