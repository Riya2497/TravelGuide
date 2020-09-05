package com.jignesh.streety;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jignesh.streety.Adapter.ChangeRouteAdapter;
import com.jignesh.streety.Adapter.PlacesAdapter;
import com.jignesh.streety.Model.Places;

import java.util.ArrayList;

public class ChangeRouteActivity extends AppCompatActivity {
    //ArrayList<String> mPlaces;
    ArrayList<Places> placesList = new ArrayList<>();
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_route);
        if (getIntent() != null) {
            if (getIntent().hasExtra("places")) {
                placesList = getIntent().getParcelableArrayListExtra("places");
            }
        }


        mRecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager;
        ChangeRouteAdapter mAdapter;

       /* mPlaces = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mPlaces.add("Atapi");
            mPlaces.add("Lakshmi Vilas Palace");
            mPlaces.add("Surya Palace");
            mPlaces.add("Inorbit Mall");
            mPlaces.add("Reliance Mall");
            mPlaces.add("Sayaji Hotel");
            mPlaces.add("Kamati Baug");
        }*/

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(this);
        mAdapter=new ChangeRouteAdapter(placesList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

    }
}
