package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class DataProvider extends AppCompatActivity {

    SharedPreferences sharedPreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
    public String phoneNumber = sharedPreferences.getString("phone", null);
}
