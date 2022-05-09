package com.datastructures.chatty.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.databinding.ActivitySplashScreenBinding;
import com.datastructures.chatty.screens.authentication.LoginFormActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            //Do something after delay
            finish();
            startActivity(new Intent(SplashScreen.this, LoginFormActivity.class));
        }, 3000);
    }
}

