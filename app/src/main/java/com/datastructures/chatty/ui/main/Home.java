package com.datastructures.chatty.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datastructures.chatty.R;
import com.datastructures.chatty.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Home extends AppCompatActivity {

    private ActivityMainBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); //hide the title bar
        //Binding and drawing layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Tab layout setup
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this::goToSettings);


        String phone = getIntent().getStringExtra("phone");
        try {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(phone);

            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        String imageUrl = doc.getString("profileImageUrl");
                        Glide.with(this).load(imageUrl).
                                diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.currentUserProfileImage);
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

}