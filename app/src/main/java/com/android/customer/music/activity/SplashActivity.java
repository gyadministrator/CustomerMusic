package com.android.customer.music.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.customer.music.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_splash);
    }
}
