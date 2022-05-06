package com.datastructures.chatty.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.datastructures.chatty.R;
import com.datastructures.chatty.databinding.ActivitySplashScreenBinding;
import com.datastructures.chatty.screens.authentication.LoginActivity;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bot);


        binding.logo.startAnimation(topAnim);
        binding.name.startAnimation(bottomAnim);
        binding.designImage.startAnimation(topAnim);

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            checkStoragePermission();
            //Do something after delay
            finish();
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }, 3000);
    }
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_LONG).show();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length != 0 && grantResults[0] == 0) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                onBackPressed();
            }
        }

    }
}

