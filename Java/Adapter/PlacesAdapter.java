package com.jignesh.streety.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jignesh.streety.Model.Places;
import com.jignesh.streety.R;
import com.jignesh.streety.Interface.RecyclerViewClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PlacesAdapter extends RecyclerView.Adapter {

    private ArrayList<Places> placesList;
    Context mContext;
    private RecyclerViewClickListener mListener;
    private boolean isFromMaps;
    DisplayImageOptions defaultOptions;
    ImageLoaderConfiguration config;

    public class ImageTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtType;
        CheckBox checkBox;
        ImageView image;

        CompoundButton.OnCheckedChangeListener checkedChangeListener;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtType = (TextView) itemView.findViewById(R.id.type);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            this.image = (ImageView) itemView.findViewById(R.id.background);

            itemView.setOnClickListener(this);
            checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    placesList.get(getAdapterPosition()).setChecked(b);
                }
            };
            checkBox.setOnCheckedChangeListener(checkedChangeListener);
        }


        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }
    }

    public PlacesAdapter(ArrayList<Places> data, boolean isFromMaps, Context context, RecyclerViewClickListener listener) {
        this.placesList = data;
        this.mContext = context;
        this.isFromMaps = isFromMaps;
        mListener = listener;

        defaultOptions = new DisplayImageOptions.Builder()

           .cacheInMemory(true)
                .cacheOnDisk(true)

           .build();

        config = new ImageLoaderConfiguration.Builder(getApplicationContext())

           .defaultDisplayImageOptions(defaultOptions)

           .build();
        ImageLoader.getInstance().init(config);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_card, parent, false);
        return new ImageTypeViewHolder(view);
    }

   /* @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Places.TEXT_TYPE;
            case 1:
                return Places.PLACES_CARD;
            default:
                return -1;
        }
    }*/


    @Override
   public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        Places object = placesList.get(listPosition);

        if (object != null) {
            ((ImageTypeViewHolder) holder).txtType.setText(object.getText());
            ImageLoader.getInstance().displayImage(object.getData(),
                    ((ImageTypeViewHolder) holder).image); // Default options will be used
            //((ImageTypeViewHolder) holder).image.setImageResource(object.getData());

            if (isFromMaps) {
                ((ImageTypeViewHolder) holder).checkBox.setVisibility(View.VISIBLE);

                ((ImageTypeViewHolder) holder).checkBox.setOnCheckedChangeListener(null);
                ((ImageTypeViewHolder) holder).checkBox.setChecked(object.isChecked());
                ((ImageTypeViewHolder) holder).checkBox.setOnCheckedChangeListener(((ImageTypeViewHolder) holder).checkedChangeListener);
            } else {
                ((ImageTypeViewHolder) holder).checkBox.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }


}
