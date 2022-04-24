package com.ismt.journeyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;

    private Animation topAnim, bottomAnim;
    private ImageView imgTravel;
    private TextView appname, slogan;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imgTravel = findViewById(R.id.imgTravel);
        appname = findViewById(R.id.appname);
        slogan = findViewById(R.id.slogan);

        imgTravel.setAnimation(topAnim);
        appname.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        sharedPreferences = getSharedPreferences("journal_prefs", Context.MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("user_login",false);


        new Handler().postDelayed(() -> {
            if(check){
                startActivity(new Intent(this, Dashboard.class));
            }
            else {

                startActivity(new Intent(this, SignIn.class));
            }
            finish();
        },SPLASH_SCREEN);
    }
}