package com.datastructures.chatty.screens.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;
import com.datastructures.chatty.databinding.ActivityVerifyPhoneNumberBinding;

import com.datastructures.chatty.main.home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {
    private String verificationCodesBySystem;
    private EditText verificationCode;
    private ProgressBar progressBar;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String name;
    private String description;
    private String phoneNo;
    private String activityDeterminant;
    private Uri imageUri;
    private String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVerifyPhoneNumberBinding binding = ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityDeterminant = getIntent().getStringExtra("determinant");
        Button verify_Btn = findViewById(R.id.verify_Btn);
        verificationCode = findViewById(R.id.editTextNumberSigned);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        phoneNo = getIntent().getStringExtra("phoneNo");
        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        sendVerificationCodeToUser(phoneNo);

        binding.verifyBtn.setOnClickListener(v -> {

            String code = verificationCode.getText().toString();

            if (code.isEmpty() || code.length() < 6) {
                verificationCode.setError("Wrong verification code. ");
                verificationCode.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }

        });

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+20" + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //In this function, we will handle the possible events occurred while verifying the SMS code.
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //Get the code in global variable
            verificationCodesBySystem = s;
        }

        //This is the function performed the automatic verification
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }

        }

        //It will be executed when the verification will be failed. So to handle that we will simply show the error message to the user.
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //It will be used to verify the credentials entered by the user
    // furthermore match them with the system generated verification ID.
    private void verifyCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodesBySystem, verificationCode);
        signInTheUserByCredential(credential);

    }


    private void signInTheUserByCredential(PhoneAuthCredential credential) {


        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneNumber.this, new OnCompleteListener<AuthResult>() {

            private void showInfoToUser(Task<AuthResult> task) {
                //here manage the exceptions and show relevant information to user
            }

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String newMessage="......";
                if (task.isSuccessful()) {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        newMessage = "This account is already Exist";
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.progressBar), newMessage, Snackbar.LENGTH_LONG);
                        snackbar.setAction("Dismiss", v -> {

                        });
                        snackbar.show();
                    }
                    if(activityDeterminant.equals("signup"))
                        saveInfo();

                    navigateToHome();
                }
            }

        });
    }

    private void navigateToHome() {
        Intent intent =new Intent(VerifyPhoneNumber.this , home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveInfo() {

        String randomKey = UUID.randomUUID().toString();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profiles/"+System.currentTimeMillis()+randomKey);
       storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               Toast.makeText(VerifyPhoneNumber.this, "file put success", Toast.LENGTH_SHORT).show();
               storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       downloadUrl = uri.toString();
                       Map<String,Object> map = new HashMap<>();
                       map.put("name",name);
                       map.put("description",description);
                       map.put("profileImageUrl",downloadUrl);
                       db.collection("users").document(phoneNo).set(map).addOnCompleteListener(task -> {
                           Toast.makeText(VerifyPhoneNumber.this , "Access granted " , Toast.LENGTH_SHORT).show();
                       });
                   }
               });
           }
       });


    }
}