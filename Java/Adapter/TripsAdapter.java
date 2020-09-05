package com.jignesh.streety.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jignesh.streety.Interface.RecyclerViewClickListener;
import com.jignesh.streety.Model.Trips;
import com.jignesh.streety.R;

import java.util.ArrayList;

public class TripsAdapter extends RecyclerView.Adapter {
   public ArrayList<Trips> mTrips;
    private RecyclerViewClickListener mListener;
    String trip_name;


    public TripsAdapter(ArrayList<Trips> trips) {
        mTrips = trips;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTripName;
        TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mTripName = (TextView) itemView.findViewById(R.id.trip_name);
            mDate = (TextView) itemView.findViewById(R.id.trip_date);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trips_card, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder,  int position) {
        Trips object = mTrips.get(position);
        if(object!=null) {
            ((ViewHolder) holder).mTripName.setText(object.getText());
            ((ViewHolder) holder).mDate.setText(object.getTrip_date());
        }

    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }
}
