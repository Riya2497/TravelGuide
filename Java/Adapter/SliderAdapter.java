package com.jignesh.streety.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jignesh.streety.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutinflater;

    public SliderAdapter(Context context)
    {
        this.context=context;
    }

    //arrays for icons

    public String[] slide_headings={
            "",
            "Shortest Sequence",
            "Near By Places",
            "AR Mode"
    };

    public int[] slide_images={
            R.drawable.ic_launcher,
            R.drawable.ic_timeline_white_24dp,
        R.drawable.ic_room_white_24dp,
            R.drawable.ic_3d_rotation_white_24dp

    };

    public String[] slide_desc={
            "Streety. See More. Travel More",
            "shortest sequence connecting all tourist places in city",
            "see hotels,restaurants,and any neary by visiting place",
            "Visualize Your Trip better With AR"
    };
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==(RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutinflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutinflater.inflate(R.layout.slidelayout,container,false);

        ImageView iv=view.findViewById(R.id.iv);
        TextView tvHeading=view.findViewById(R.id.tvHeading);
        TextView tvDescription=view.findViewById(R.id.tvDescription);

        iv.setImageResource(slide_images[position]);
        tvHeading.setText(slide_headings[position]);
        tvDescription.setText(slide_desc[position]);

        container.addView(view);


        return view;
    }
    @Override
    public  void destroyItem(ViewGroup container,int position,Object object)
    {
        container.removeView((RelativeLayout)object);
    }
}
