package com.datastructures.chatty.screens.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;
import com.datastructures.chatty.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {


    private EditText descriptionEditText, registerUserNameEditText, registerPhoneEditText;
    private CircleImageView ProfilePhoto;
    private String phone;
    private Uri profileImageUri;

    //Chek if The username is Valid or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        descriptionEditText = findViewById(R.id.description);
        registerUserNameEditText = findViewById(R.id.registering_user_name);
        registerPhoneEditText = findViewById(R.id.registering_user_phone);
        ProfilePhoto=findViewById(R.id.current_user_profile_image);
        TextView toLogin = findViewById(R.id.to_login_text);

        toLogin.setOnClickListener(view -> {

            Intent intent =new Intent(SignUp.this , LoginFormActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.rigesterButton.setOnClickListener(view -> {
            // Performing Validation by calling validation functions
            if(!validateUsername() || !validateName() || ! validatePhoneNo() ){
                Toast.makeText(this, "Make sure everything is ok and try again :)", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = registerUserNameEditText.getText().toString().trim();
            String descriptionStr = descriptionEditText.getText().toString().trim();
            phone = registerPhoneEditText.getText().toString().trim();

            try {
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(phone);

                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            Toast.makeText(this, "phone number is already registered login instead", Toast.LENGTH_LONG).show();
                        }else {

                            Intent intent = new Intent(SignUp.this, VerifyPhoneNumber.class);
                            intent.putExtra("phoneNo", phone);
                            intent.putExtra("name", name);
                            intent.putExtra("description", descriptionStr);
                            intent.putExtra("determinant", "signup");
                            intent.putExtra("imageUri", profileImageUri.toString());
                            startActivity(intent);
                        }
                    }
                });
            }catch (Exception e){
                System.out.println(e.getMessage());
            }


        });
        binding.currentUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImg();
            }
        });

    }
    public final void pickImg() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        this.startActivityForResult(intent, 3);
    }
    private boolean validateUsername() {
        String val = registerUserNameEditText.getText().toString();


        if (val.isEmpty()) {
            registerUserNameEditText.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 20) {
            registerUserNameEditText.setError("Username too long");
            return false;

        } else {
            registerUserNameEditText.setError(null);
            return true;
        }
    }
    //Chek if The name is Valid or not
    private Boolean validateName() {
        String val = descriptionEditText.getText().toString();

        if (val.isEmpty()) {
            descriptionEditText.setError("Field cannot be empty");
            return false;
        }
        else {
            descriptionEditText.setError(null);
            return true;
        }
    }
    //Chek if The phoneNumber is Valid or not
    private Boolean validatePhoneNo() {
        String val = registerPhoneEditText.getText().toString();

        if (val.isEmpty()) {
            registerPhoneEditText.setError("Field cannot be empty");
            return false;
        } else {
            registerPhoneEditText.setError(null);
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            profileImageUri = data.getData();
            ProfilePhoto.setImageURI(profileImageUri);
        }else{
            Toast.makeText(this, "Error occurred -1", Toast.LENGTH_LONG).show();
        }

    }
}
