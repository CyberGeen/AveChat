package com.aula.im.youcef.kebiche.avechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class splashScreen extends AppCompatActivity {

    ImageView backgroung , logo ;
    LottieAnimationView chatting ;
    Intent authPage , onBoarding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //init
        backgroung = findViewById(R.id.bgVlImg);
        logo = findViewById(R.id.logoImg);
        chatting = findViewById(R.id.lottie_splash_chat);

        //animating
        backgroung.animate().translationY(-2600).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(2400).setDuration(1000).setStartDelay(4000);
        chatting.animate().translationY(2400).setDuration(1000).setStartDelay(4000);

        authPage = new Intent(this , AuthHandlerMain.class);
        onBoarding = new Intent(this , OnBoardingMain.class );

        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);

        //disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //start the activity on the end of the animation
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                    if(firstRun == false ){
                        startActivity(onBoarding);
                        finish();
                    }else {
                        startActivity(authPage);
                        finish();
                    }
                    }
                },
                4700
        );
    }
}