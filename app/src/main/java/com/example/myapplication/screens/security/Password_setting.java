package com.example.myapplication.screens.security;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.datastructures.chatty.R;
import com.example.myapplication.ui.main.SettingActivity;

import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class Password_setting extends AppCompatActivity {
    PatternLockView mpatternLockView;
    String Save_Pattern_Key = "Pattern Code";
    String final_Pattern = "";
    Button btnDelete;
    AppCompatImageView imageback;
    Animation scaleUp,scaleDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_pattern);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar
        mpatternLockView = (PatternLockView) findViewById(R.id.pattern_LockView);
        mpatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_Pattern = PatternLockUtils.patternToString(mpatternLockView, pattern);
            }

            @Override
            public void onCleared() {

            }
        });
        scaleUp= AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scaleDown= AnimationUtils.loadAnimation(this,R.anim.scale_down);
        AppCompatButton btnSetUp = findViewById(R.id.btnSetPattern);

        btnSetUp.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_UP){
                btnSetUp.startAnimation(scaleUp);

            }else if(event.getAction()==MotionEvent.ACTION_DOWN){
                btnSetUp.startAnimation(scaleDown);
            }
            if (!final_Pattern.equals("")) {
                Paper.book().write(Save_Pattern_Key, final_Pattern);
                Toast.makeText(Password_setting.this, "Pattern saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else Toast.makeText(Password_setting.this, "Please insert a pattern", Toast.LENGTH_SHORT).show();
            return true;
        });
        btnDelete = findViewById(R.id.Delete_lock_App);
        btnDelete.setOnClickListener(v -> {
            assert Save_Pattern_Key != null;
            Paper.book().delete(Save_Pattern_Key);
            Paper.book().destroy();
            startActivity(new Intent(Password_setting.this,
                    com.example.myapplication.ui.main.SplashScreen.class));
            finish();
        });
        imageback=findViewById(R.id.imageBack);
        imageback.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_UP){
                imageback.startAnimation(scaleUp);
                imageback.setOnClickListener(view->onBackPressed());

            }else if(event.getAction()==MotionEvent.ACTION_DOWN){
                imageback.startAnimation(scaleDown);
                startActivity(new Intent(Password_setting.this, SettingActivity.class));
            }

            return true;
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


