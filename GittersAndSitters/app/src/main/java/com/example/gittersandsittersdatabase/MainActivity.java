package com.example.gittersandsittersdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class is responsible for the initial HabitTracker login screen and handles the logic for an
 * attempted user login. If there is a successful login, a "new" User object is created with the
 * logged in user's userID fields as attributes. All of the user's existing Habits and HabitEvents
 * are fetched from the Firestore as well.
 */
public class MainActivity extends AppCompatActivity {

    // Declare variables to be referenced
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private String userID;
    private ArrayList<Habit> habitList;
    private ArrayList<HabitEvent> eventList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.login_emailAddress);
        editTextPassword = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progress_bar);
        // Get an instance of Firebase Authentication SDK
        mAuth = FirebaseAuth.getInstance();
        // Get an instance of the Firebase Firestore
        db = FirebaseFirestore.getInstance();

       // Register button to switch to RegisterActivity
        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

        // Login button to execute logic for attempted login
        final Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeLoginAttempt();
            }
        });
    }

    /**
     * This method handles the logic for an attempted user login. If there is a successful login,
     * a "new" User object is created with the logged in user's userID fields as attributes.
     * All of the user's existing Habits and HabitEvents are fetched from the Firestore as well.
     */
    public void executeLoginAttempt() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check to see if email field is empty
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        // Check to see if email format is correct
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        // Check to see if password field is empty
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        // Check to see if password length is less than 6
        if (password.length() < 6) {
            editTextPassword.setError("Min password length is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        // Initialize progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Attempt to sign in user with provided email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // if sign in is successful
                if (task.isSuccessful()) {
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                    // if email has previously been verified
                    if (fUser.isEmailVerified()) {

                        // Get the string that uniquely identifies this user in the Firestore
                        userID = mAuth.getCurrentUser().getUid();
                        // Get document reference for document with this unique UserID
                        DocumentReference docRef = db.collection("Users").document(userID);
                        // This listener reads the document referenced by docRef
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    // if document matching userID exists
                                    if (document.exists()) {

                                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                        // Get data from userID document
                                        String username = (String) document.getData().get("userName");
                                        String email = (String) document.getData().get("email");
                                        // Create "new" User
                                        user = new User(userID, username, email);

                                        // Create DataDownloader object for getting the logged in user's data
                                        DataDownloader dataDownloader = new DataDownloader(userID);

                                        // get logged in user's data from Firestore
                                        dataDownloader.getUserHabits(new FirestoreHabitListCallback() {
                                            @Override
                                            // Call back enables us to get the downloaded EventList
                                            public void onHabitListCallback(ArrayList<Habit> returnedHabitList) {

                                                habitList = returnedHabitList;

                                                dataDownloader.getHabitEvents(new FirestoreEventListCallback() {
                                                    @Override
                                                    public void onHabitCallback(ArrayList<HabitEvent> returnedEventList) {
                                                        eventList = returnedEventList;

                                                        // Match the HabitEvents with their corresponding Habits
                                                        matchEventsToHabits();

                                                        user.setAllUserHabits(habitList);
                                                        Intent intent = new Intent(MainActivity.this, HabitActivity.class);
                                                        intent.putExtra("user", user);
                                                        MainActivity.this.startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
                    }
            else {
                fUser.sendEmailVerification();
                Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
            }
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
        }
            }
        });
    }

    /**
     * This method matches a list of HabitEvents with their corresponding Habits
     */
    public void matchEventsToHabits() {

        // for each HabitEvent
        for (int i = 0; i < eventList.size(); i++) {
            HabitEvent habitEvent = eventList.get(i);
            String eventParentHabitName = habitEvent.getParentHabitName();
            boolean found = false;

            // for each Habit
            for (int j = 0; j < habitList.size() && !found; j++) {
                Habit habit = habitList.get(j);
                if (habit.getHabitName().equals(eventParentHabitName)) {
                    found = true;
                    habit.addHabitEvent(habitEvent);
                }
            }
        }
    }
}