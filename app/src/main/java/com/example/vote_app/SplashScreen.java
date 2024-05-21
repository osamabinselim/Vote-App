package com.example.vote_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vote_app.activities.HomeActivity;
import com.example.vote_app.activities.LoginActivity;


public class SplashScreen extends AppCompatActivity {
    public static final String PREFERENCES ="prefkeys";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn="islogin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    protected void onStart(){
        super.onStart();

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        Boolean bol=sharedPreferences.getBoolean(IsLogIn,false);


        new Handler().postDelayed(()->{
            if (bol){
                startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                finish();
            }else {
            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
            finish();
            }

        },3000);
    }
}