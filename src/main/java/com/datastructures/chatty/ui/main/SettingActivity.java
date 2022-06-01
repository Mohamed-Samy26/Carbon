package com.datastructures.chatty.ui.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;
import com.datastructures.chatty.screens.authentication.LoginFormActivity;
import com.datastructures.chatty.utils.SharedPreferenceClass;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    private Switch SwitchTheme;
    private static SharedPreferenceClass sharedPreferenceClass;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesHome;
    private static final String SHARED_PREFERENCES_NAME = "mypref";
    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadDarkModeState();

        super.onCreate(savedInstanceState);

//        logout_btn = findViewById(R.id.logout_button);
//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                logout(view);
//            }
//        });
        sharedPreferencesHome = getSharedPreferences(SHARED_PREFERENCES_NAME,Activity.MODE_PRIVATE);
        loadLocale();
        setContentView(R.layout.activity_setting);
        changeActionBarLanguage();

        initWidgets();
        changeSwitchToLoadModeState();

    }

    private void loadDarkModeState() {
        sharedPreferenceClass = new SharedPreferenceClass(this);
        if(sharedPreferenceClass.loadNightModeState())
            setTheme(R.style.dark_theme);
        else
            setTheme(R.style.app_theme);
    }

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChangeLanguage", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("Language", "");
        setLocale(language);
    }

    private void changeActionBarLanguage() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
    }

    private void initWidgets() {
        SwitchTheme = findViewById(R.id.switch1);
    }

    private void changeSwitchToLoadModeState() {
        if(sharedPreferenceClass.loadNightModeState()) {
            SwitchTheme.setChecked(true);
        }
        SwitchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sharedPreferenceClass.setNightModeState(true);
//                    themeTextView.setText(R..textMode);
                }
                else {
                    sharedPreferenceClass.setNightModeState(false);
//                    themeTextView.setText("Light");
                }
                recreate();
            }
        });
    }

    public void changeLanguage(View view) {
        showChangeLanguageDialog();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "Arabic"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("ar");
                        break;
                }
                recreate();
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("ChangeLanguage", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    public void goToHome(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sharedPreferencesHome.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(getApplicationContext(), "Log out successfully", Toast.LENGTH_SHORT).show();
        Intent intent =new Intent(SettingActivity.this, LoginFormActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}