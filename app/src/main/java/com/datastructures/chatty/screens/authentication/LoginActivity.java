package com.datastructures.chatty.screens.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText loginPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPhone = findViewById(R.id.login_phone);
        Button login = findViewById(R.id.login_button);
        TextView toRegister = findViewById(R.id.to_register_text);

        toRegister.setOnClickListener(view -> {
            Intent intent =new Intent(LoginActivity.this , SignUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        login.setOnClickListener(view -> {

            String phone = loginPhone.getText().toString();
            try {
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(phone);

                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            Intent intent =new Intent(LoginActivity.this,VerifyPhoneNumber.class);
                            intent.putExtra("phoneNo",phone);
                            intent.putExtra("determinant","login");
                            intent.putExtra("imageUri","mipmap-mdpi/user_img.png");

                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, "User doesn't exist signup first!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
//            //REMOOOOOOOOVE NEXT BLOCK, for chat testing
//            ///////////////////////////////////////////////////
//            finally {
//                Intent intent =new Intent(LoginActivity.this , home.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//            /////////////////////////////////////////////////
        });
    }
}