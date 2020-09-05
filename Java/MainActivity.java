package com.jignesh.streety;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jignesh.streety.Adapter.SliderAdapter;

public class MainActivity extends AppCompatActivity {
    ViewPager vwPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] mdots;
    Button btNext;
    Button btPrevious;
    int mcurrent;
    Toolbar app_bar;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(getApplicationContext());
        Boolean b=sharedPref.getBool();

        if(!b) {

            setContentView(R.layout.activity_main);
            sharedPref.setBool(true);
            mappingWidgets();
            addListeners();



            sliderAdapter = new SliderAdapter(this);
            vwPager.setAdapter(sliderAdapter);

            mDotsIndicator(0);

            vwPager.addOnPageChangeListener(viewListener);
            btPrevious.setVisibility(View.INVISIBLE);
        }
        else
        {
            Intent ii=new Intent(getApplicationContext(),LoginActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ii);
            finish();
        }


    }

    private void mappingWidgets()
    {
        vwPager=findViewById(R.id.vwPager);
        dotsLayout=findViewById(R.id.dotsLayout);
        btNext=findViewById(R.id.btNext);
        btPrevious=findViewById(R.id.btPrevious);
    }

    private void addListeners()
    {
       btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btNext.getText().toString().equals("Got it!!"))
                {
                    Intent ii=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(ii);
                }else {
                    vwPager.setCurrentItem(mcurrent + 1);
                }
            }
        });
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vwPager.setCurrentItem(mcurrent-1);
            }
        });
    }
public void mDotsIndicator(int position)
{

    mdots=new TextView[4];
    dotsLayout.removeAllViews();

    for(int i=0;i<mdots.length;i++)
    {
        mdots[i]=new TextView(this);
        mdots[i].setText(Html.fromHtml("&#8226;"));
        mdots[i].setTextSize(35);
        mdots[i].setTextColor(getResources().getColor(R.color.appColorWhite));

        dotsLayout.addView(mdots[i]);
    }

    if(mdots.length>0)
    {
        mdots[position].setTextColor(getResources().getColor(R.color.Dots_active));
    }

}

    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i)
        {
                mDotsIndicator(i);

                mcurrent=i;
                if(mcurrent==0)
                {
                    btPrevious.setEnabled(false);
                    btNext.setEnabled(true);
                    btPrevious.setVisibility(View.INVISIBLE);
                    btNext.setVisibility(View.VISIBLE);
                }
                else if(mcurrent==(mdots.length-1))
                {
                    btPrevious.setEnabled(true);
                    btNext.setText("Got it!!");
                    btNext.setEnabled(true);
                    btNext.setVisibility(View.VISIBLE);
                    btPrevious.setVisibility(View.VISIBLE);
                   // btNext.setVisibility(View.INVISIBLE);
                   // btNext.setEnabled(false);
                }
                else
                {
                    btPrevious.setEnabled(true);
                    btNext.setEnabled(true);
                    btPrevious.setVisibility(View.VISIBLE);
                    btNext.setVisibility(View.VISIBLE);
                }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
