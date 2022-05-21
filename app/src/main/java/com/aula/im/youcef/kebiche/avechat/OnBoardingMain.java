package com.aula.im.youcef.kebiche.avechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class OnBoardingMain extends AppCompatActivity {

    ViewPager viewPager ;
    ViewPagerAdapter adapter;
    TextView skip ;
    DotsIndicator dotsIndicator;
    int curPos = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_main);

        Button cont = (Button) findViewById(R.id.continueToAuth);

        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager(viewPager);

        skip = findViewById(R.id.skipBtn);

        // skip and continue display handlers
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position != curPos){
                    curPos = position ;
                    if(position == 2){
                        cont.setVisibility(View.VISIBLE);
                        skip.setVisibility(View.INVISIBLE);
                    }else {
                        cont.setVisibility(View.INVISIBLE);
                        skip.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAuth();
            }
        });
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAuth();
            }
        });
    }

    private void toAuth(){

        //no longer show onboarding page after the first run and hitting continue
        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();


        Intent authIntent = new Intent(this , AuthHandlerMain.class);
        startActivity(authIntent);
        finish();
    }



}