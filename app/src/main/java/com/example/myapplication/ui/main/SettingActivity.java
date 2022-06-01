package com.example.myapplication.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.datastructures.chatty.R;
import com.example.myapplication.screens.authentication.LoginActivity;
import com.example.myapplication.screens.security.Password_setting;
import com.example.myapplication.utils.SharedPreferenceClass;

import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private static SharedPreferenceClass sharedPreferenceClass;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_NAME = "mypref";
    public static final String KEY_PHONE_NUMBER = "phone";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_PROFILE_IMAGE = "image";
    TextView usernameTextView, phoneTextView, bioTextView;
    private CircleImageView userImage;

    String username;
    String phone;
    String bio;
    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadDarkModeState();

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //to hide title bar
        Objects.requireNonNull(getSupportActionBar()).hide(); //to hide action bar

        loadLocale();
        setContentView(R.layout.activity_setting);
        changeActionBarLanguage();
        initWidgets();
        getUserData();
    }

    private void initWidgets() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,Activity.MODE_PRIVATE);
        usernameTextView = findViewById(R.id.usernameSetting);
        phoneTextView = findViewById(R.id.phoneSetting);
        bioTextView = findViewById(R.id.bioSetting);
        userImage = findViewById(R.id.userImageSetting);
    }

    private void getUserData() {
        phone = sharedPreferences.getString(KEY_PHONE_NUMBER, null);
        username = sharedPreferences.getString(KEY_USER_NAME, null);
        imageURL = sharedPreferences.getString(KEY_PROFILE_IMAGE, null);

        if(imageURL != null) {
            Glide.with(getApplicationContext()).load(imageURL).
                    diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userImage);
        }
//        userImage.setImageURI(Uri.parse(imageURL));
        usernameTextView.setText(username);
        phoneTextView.setText(phone);
        bioTextView.setText(bio);
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
        if (actionBar != null)
        actionBar.setTitle(getResources().getString(R.string.app_name));
    }


    public void changeLanguage(View view) {
        showChangeLanguageDialog();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "العربية"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, (dialog, which) -> {
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

    public void goBack(View view) {
        finish();
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(getApplicationContext(), "Log out successfully", Toast.LENGTH_SHORT).show();
        Intent intent =new Intent(SettingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void app_lock(View view) {
        Intent intent =new Intent(SettingActivity.this,  Password_setting.class);
        startActivity(intent);
    }

    public void goToEditProfile(View view) {
        Intent intent =new Intent(SettingActivity.this,  ProfileActivity.class);
        startActivity(intent);
    }
}