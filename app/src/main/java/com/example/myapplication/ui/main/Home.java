package com.example.myapplication.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datastructures.chatty.R;
import com.example.myapplication.screens.chatroom.Message;
import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarnava.textwriter.TextWriter;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    private CircleImageView currentUserProfileImage;
    public static boolean hasRetrieved = false;
    public static ArrayList<Message> oldData = new ArrayList<Message>();
    private static SharedPreferenceClass sharedPreferenceClass;
    private SharedPreferences sharedPreferences;
    boolean dark = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar

        //Binding and drawing layout
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setContentView(R.layout.activity_main_dark);
        } else {
            setContentView(R.layout.activity_main);
        }
        //Tab layout setup
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);

        tabs.setupWithViewPager(viewPager);

        //Views
        AppCompatImageView settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this::goToSettings);
        currentUserProfileImage = findViewById(R.id.current_user_profile_image);

        //Vars
        SharedPreferences sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone" , null);

        CircleImageView meow = findViewById(R.id.current_user_profile_image);
        meow.setOnClickListener(view -> goToProfile(phone));

        TextWriter textWriter=findViewById(R.id.title2);
        textWriter.setWidth(8)
                .setDelay(30)
                .setConfig(TextWriter.Configuration.INTERMEDIATE)
                .setSizeFactor(20f)
                .setLetterSpacing(20f)
                .setColor(Color.WHITE)
                .setText("CARBON")
                .startAnimation();

        try {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(phone);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        String imageUrl = doc.getString("profileImageUrl");
                        Glide.with(getApplicationContext()).load(imageUrl).
                                diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(currentUserProfileImage);
                    }else {
                        Toast.makeText(this,
                                "User doesn't exist !",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


    public void goToSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
    }

    public void goToProfile( String phone) {
        Intent intent =new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("phone" , phone);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                dark = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                dark=true;
                break;
        }
    }
}