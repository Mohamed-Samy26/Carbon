package com.datastructures.chatty.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceClass {
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_NAME = "mypref";

    public SharedPreferenceClass(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    public Boolean loadNightModeState() {
        Boolean state = sharedPreferences.getBoolean("NightMode", false);
        return state;
    }
}
