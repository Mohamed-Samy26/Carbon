package com.example.myapplication.screens.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.datastructures.chatty.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupTapFragment extends Fragment {

    private EditText descriptionEditText, registerUserNameEditText, registerUserPhoneEditText;
    private CircleImageView profilePhoto;
    private final String DEFAULT_IMAGE = "https://www.pixsy.com/wp-content/uploads/2021/04/ben-sweet-2LowviVHZ-E-unsplash-1.jpeg";
    private Uri profileImageUri = Uri.parse(DEFAULT_IMAGE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);
        initWidgets(root);
        return root;
    }

    private void initWidgets(ViewGroup root) {
        descriptionEditText = root.findViewById(R.id.description);
        registerUserNameEditText = root.findViewById(R.id.registering_user_name);
        registerUserPhoneEditText = root.findViewById(R.id.registering_user_phone);
        profilePhoto = root.findViewById(R.id.current_user_profile_image);
        Button regbtn = root.findViewById(R.id.register_button);

        regbtn.setOnClickListener(view -> {
            // Performing Validation by calling validation functions
            if (!validateDescription() || !validateName() || !validatePhone()) {
                Toast.makeText(getActivity(), "Make sure everything is ok and try again :)", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = registerUserNameEditText.getText().toString().trim();
            String descriptionStr = descriptionEditText.getText().toString().trim();
            String phoneNumber = registerUserPhoneEditText.getText().toString().trim();


            try {
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(phoneNumber);

                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Toast.makeText(getActivity(), "Phone number is already registered login instead", Toast.LENGTH_LONG).show();
                        } else {
                            ArrayList<String> arr = new ArrayList<String>();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("description", descriptionStr);
                            user.put("profileImageUrl", profileImageUri.toString() != null ?
                                    profileImageUri.toString() : DEFAULT_IMAGE);
                            user.put("hasStory", false);
                            user.put("friends", arr);
                            user.put("lastStory", "");
                            user.put("storyUrl", "");
                            user.put("privacy", true);

                            docRef.set(user);
                            Toast.makeText(getActivity(), "Successfully Registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));

                            profilePhoto.setBackgroundResource(R.drawable.user_icon);
                            registerUserNameEditText.setText("");
                            descriptionEditText.setText("");
                            registerUserPhoneEditText.setText("");
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        profilePhoto.setOnClickListener(view -> pickImg());
    }

    private void pickImg() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        this.startActivityForResult(intent, 3);
    }
    //Chek if The username is Valid or not
    private boolean validateDescription() {
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

    private Boolean validatePhone() {
        String val = registerUserPhoneEditText.getText().toString();

        if (val.isEmpty()) {
            registerUserPhoneEditText.setError("Field cannot be empty");
            return false;
        }
        else {
            registerUserPhoneEditText.setError(null);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            profileImageUri = data.getData();
            profilePhoto.setImageURI(profileImageUri);
        }else{
            profileImageUri = Uri.parse("https://10play.com.au/ip/s3/2022/01/28/a9333564010931a07b777e8c32f2ce8c-1123582.png?image-profile=image_max&io=landscape");
        }

    }
}
