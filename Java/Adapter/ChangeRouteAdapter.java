package com.jignesh.streety.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jignesh.streety.Model.Places;
import com.jignesh.streety.R;

import java.util.ArrayList;
import java.util.Collections;

public class ChangeRouteAdapter extends  RecyclerView.Adapter<ChangeRouteAdapter.ViewHolder> implements ItemTouchHelperAdapter
{
    ArrayList<Places> mplaces;

    public ChangeRouteAdapter(ArrayList<Places> places) {
        mplaces = places;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.change_route_cards, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeRouteAdapter.ViewHolder viewHolder, int i) {
        viewHolder.mPlaceName.setText(mplaces.get(viewHolder.getAdapterPosition()).getText());
    }

    @Override
    public int getItemCount() {
        return mplaces.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mplaces, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mplaces, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPlaceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlaceName = (TextView) itemView.findViewById(R.id.mPlace_Name);
        }
    }
}

