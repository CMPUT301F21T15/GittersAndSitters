package com.example.gittersandsittersdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class is responsible for the initial HabitTracker login screen
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare variables to be referenced

    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private FirebaseAuth mAuth; // The entry point of the Firebase Authentication SDK
    FirebaseFirestore fStore;   // The entry point for all Cloud Firestore operations
    private ProgressBar progressBar;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        signIn = findViewById(R.id.login_button);
        signIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.login_emailAddress);
        editTextPassword = findViewById(R.id.login_password);

        progressBar = findViewById(R.id.progress_bar);

        // Get an instance of Firebase Authentication SDK
        mAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                startActivity(new Intent(this, Register.class));
                break;

            case R.id.login_button:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        /**
         * Check to see if email field is empty
         */

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        /**
         * Check to see if email format is correct
         */

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        /**
         * Check to see if password field is empty
         */

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        /**
         * Check to see if password length is less than 6
         */

        if (password.length() < 6) {
            editTextPassword.setError("Min password length is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        // Get the string that uniquely identifies this user in the Firestore
                        userID = mAuth.getCurrentUser().getUid();
                        // Get document reference for document with this unique UserID
                        DocumentReference docRef = fStore.collection("Users").document(userID);
                        // This listener reads the document referenced by docRef
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        // Get the values to which the specified keys are mapped
                                        String username = (String) document.getData().get("userName");
                                        String email = (String) document.getData().get("email");
                                        User user = new User(username, email);
                                        Intent intent = new Intent(MainActivity.this,HabitActivity.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);

                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }

                        });

                        //NOTE: Changed from Profile

                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                        //startActivity(new Intent(MainActivity.this,ProfileActivity.class));

                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
