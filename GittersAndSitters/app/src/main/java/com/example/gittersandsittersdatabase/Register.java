package com.example.gittersandsittersdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText emailName;
    EditText userName;
    EditText userPassword;
    Button confirmButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailName = findViewById(R.id.editTextTextEmailAddress);
        userName = findViewById(R.id.editTextUserName);
        userPassword = findViewById(R.id.editTextUserPassword);
        confirmButton = findViewById(R.id.ConfirmAccount);
        mAuth = FirebaseAuth.getInstance();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailName.getText().toString().trim();
                String passWord = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    emailName.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(passWord)){
                    userPassword.setError("Password is required");
                    return;
                }
                if (passWord.length() < 6){
                    emailName.setError("Password is too short");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "user Created. ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        //login_button.setOnClickListener(new View.OnClickListener(){
        //    @Override
       //     public void onClick(View view){
       //         startActivity(new Intent(getApplicationContext(), Login.class));
       //     }
       //  });


    }

}

























