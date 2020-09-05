package com.jignesh.streety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jignesh.streety.Interface.APIManager;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity
{

    Button btFacebook, btGoogle, bt_login, bt_newAccount;
    EditText etEmail, etPassword;
    SharedPref sharedPref;
    TextView tv_forgotPwd;
    String fbid;
    Boolean f = false;
    String gid;
    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN=9001;


    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);





        sharedPref = new SharedPref(this);
        Boolean b1 = sharedPref.getEmail();
        Boolean b2 = sharedPref.getFBId();
        Boolean b3 = sharedPref.getGoogleId();
        Log.d("gb",b3.toString());
        Log.d("fb",b2.toString());
        Log.d("eb",b1.toString());



        if (!b1) {

            setContentView(R.layout.activity_launch);
            mappingWidgets();
            bt_newAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ii = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(ii);

                    return;
                }
            });
            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkValidation()) {
                        loginAPI();
                    }
                }
            });
            tv_forgotPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ii = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                    startActivity(ii);

                    return;
                }
            });

        }
        if (!b2) {
            setContentView(R.layout.activity_launch);
            mappingWidgets();
            bt_newAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ii = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(ii);

                    return;
                }
            });
            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkValidation()) {
                        loginAPI();
                    }
                }
            });
            tv_forgotPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ii = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                    startActivity(ii);

                    return;
                }
            });

        }

        if (!b3) {
            setContentView(R.layout.activity_launch);
            mappingWidgets();
            bt_newAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ii = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(ii);

                    return;
                }
            });
            btGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   signIn();
                }
            });
            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkValidation()) {
                        loginAPI();
                    }
                }
            });
            tv_forgotPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ii = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                    startActivity(ii);

                    return;
                }
            });
        }
        if(b1||b2||b3)
        {
            Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(ii);

            return;
        }


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    public void onSuccess(LoginResult loginResult) {
                        GraphLoginRequest(loginResult.getAccessToken());

                    }


                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        btFacebook.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));
            }
        });

    }

    private void signIn() {
        Log.d("G","In Signin");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            gid=account.getId();
            Log.d("gid",account.getId());
            sharedPref.setGoogleId(gid,true);
            sharedPref.setFBId("",false);
            sharedPref.setEmail("",false);
            googleLoginApi();

            // Signed in successfully, show authenticated UI.
           // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
           // updateUI(null);
        }
    }



   protected void GraphLoginRequest(AccessToken accesstoken)
    {

        GraphRequest graphRequest = GraphRequest.newMeRequest(accesstoken, new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse response)
            {
                Log.d("comp","in completed");
                try
                {

                    String name = jsonObject.getString("name");
                    fbid = jsonObject.getString("id");
                    sharedPref.setFBId(fbid,false);
                    sharedPref.setEmail("",true);
                    sharedPref.setGoogleId("",true);
                    sharedPref.setUsername(name);
                    Log.d("fbid", fbid);
                    Log.d("fbname", name);
                    fbloginAPI();

                } catch (Exception e)
                {
                    Log.d("comp","in catch");
                    Toast.makeText(LoginActivity.this, "FB JSON ERROR", Toast.LENGTH_SHORT).show();
                }

            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("fields","id,name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }





    private void mappingWidgets() {
        btFacebook = findViewById(R.id.btFacebook);
        btGoogle = findViewById(R.id.btGoogle);
        bt_login = findViewById(R.id.bt_login);
        bt_newAccount = findViewById(R.id.bt_newAccount);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tv_forgotPwd = findViewById(R.id.tv_forgotPwd);
    }


    public boolean checkValidation() {
        boolean isValid = true;

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError(getString(R.string.error_email));
            isValid = false;
        } else if (!validateEmail(etEmail.getText().toString())) {
            etEmail.setError(getString(R.string.error_email));
            isValid = false;
        } else if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.setError(getString(R.string.error_pwd));
            isValid = false;
        }
        return isValid;
    }

    public boolean validateEmail(String etEmail) {
        Matcher matcher;
        String email = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
        Pattern pattern = Pattern.compile(email);
        matcher = pattern.matcher(etEmail);
        return matcher.matches();
    }

    void loginAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
            params.put("email", etEmail.getText().toString().trim());
            params.put("password", etPassword.getText().toString().trim());
            params.put("fb_id", "");
            params.put("google_id", "");




        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.login(params);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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

                        Toast.makeText(LoginActivity.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {
                                sharedPref.setEmail(etEmail.getText().toString(), false);
                                sharedPref.setGoogleId("",true);
                                sharedPref.setFBId("",true);
                                sharedPref.setUsername(etEmail.getText().toString());


                            Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
                            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                            finish();
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(LoginActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.d("G","OAR");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);}
    }
    void fbloginAPI() {
        Log.d("msg","in");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
        params.put("fb_id",fbid);





        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.facebookloginC(params);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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

                        Toast.makeText(LoginActivity.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {

                            Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
                            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                            finish();
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(LoginActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }


    void googleLoginApi() {
        Log.d("msg", "in");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL) // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Sending param
        Map<String, String> params = new HashMap<>();
        params.put("google_id", gid);


        // Initializing APIManager
        APIManager api = retrofit.create(APIManager.class);

        // TODO: Note: Replace 'getDetails(param)' API method for every new API here
        Call<Map<String, Object>> call = api.googleloginC(params);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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

                        Toast.makeText(LoginActivity.this,
                                content.get("message").getAsString(),
                                Toast.LENGTH_SHORT).show();

                        // TODO: Read response here
                        if (content.get("status").getAsInt() == 1) {

                            Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
                            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                            finish();
                        }

                        // Convert JsonArray to your custom model class list as follow
                    /*ArrayList<YourModelClass> myModelList = gson.fromJson(content.get(array_name).getAsJsonArray().toString(),
                    	new TypeToken<ArrayList<YourModelClass>>(){}.getType());*/
                    } else {
                        Toast.makeText(LoginActivity.this, "No response available.", Toast.LENGTH_SHORT).show();

                        Log.d("Error", "No response available");
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error in reading response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                Log.d("Error", "onFailure: " + t.getMessage());
            }
        });
    }
}
