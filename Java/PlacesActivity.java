package com.jignesh.streety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jignesh.streety.Adapter.PlacesAdapter;
import com.jignesh.streety.Interface.APIManager;
import com.jignesh.streety.Interface.RecyclerViewClickListener;
import com.jignesh.streety.Model.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesActivity extends AppCompatActivity implements RecyclerViewClickListener {

    RecyclerView mRecyclerView;
    TextView type;
    Button btDone;
    PlacesAdapter adapter;
    String item;

    ArrayList<Places> placesList = new ArrayList<>();
    private boolean isFromMaps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        if (getIntent() != null) {
            if (getIntent().hasExtra("places")) {
                placesList = getIntent().getParcelableArrayListExtra("places");
            }
            if (getIntent().hasExtra("isFromMaps")) {
                isFromMaps = getIntent().getBooleanExtra("isFromMaps", false);
            }
            if (getIntent().hasExtra("location")) {
                item = getIntent().getStringExtra("location");


            }
        }


        btDone = findViewById(R.id.btDone);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
       PlacesAdapter adapter = new PlacesAdapter(placesList, isFromMaps, this, this);
        mRecyclerView.setAdapter(adapter);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(getApplicationContext(), MapsActivity.class);
                ii.putExtra("places", placesList);
                setResult(RESULT_OK, ii);
                ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();

            }
        });

    }


    @Override
    public void onItemClick(int position) {
        placesList.get(position).setChecked(true);

        Intent ii = new Intent(this, DetailsActivity.class);
        ii.putExtra("place_name",placesList.get(position).getText());
        startActivity(ii);
    }

}
