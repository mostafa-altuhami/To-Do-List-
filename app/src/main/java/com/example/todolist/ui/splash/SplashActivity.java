package com.example.todolist.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.R;
import com.example.todolist.core.manager.DailyResetManager;
import com.example.todolist.ui.home.MainActivity;

import java.util.Objects;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        DailyResetManager.checkDailyReset(this);


        final Intent i = new Intent(SplashActivity.this, MainActivity.class);
        new Handler(getMainLooper()).postDelayed(() -> {
            startActivity(i);
            finish();
        }, 150);
    }
}