package com.example.appointmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    private Spinner userTypeSpinner;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = findViewById(R.id.inputname);
        inputEmail = findViewById(R.id.inputemail);
        inputPassword = findViewById(R.id.inputpswd);
        inputConfirmPassword = findViewById(R.id.inputconpswd);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        registerButton = findViewById(R.id.button);
        TextView alreadyAccount = findViewById(R.id.alreadyacc);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Create ArrayAdapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    private void registerUser() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        String userType = userTypeSpinner.getSelectedItem().toString();

        if (userType.equals("Select User Type")) {
            Toast.makeText(MainActivity.this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals(confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid(); // Get the unique user ID
                                User newUser = new User(userId, name, email, userType); // Include user ID
                                mDatabase.child("users").child(userId).setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                    // Redirect to LoginActivity or MainActivity
                                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                String errorMessage = "Registration Failed";
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    errorMessage = "Email already registered!";
                                }
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }

    public static class User {
        public String id;
        public String name;
        public String email;
        public String userType;

        public User() {
        }

        public User(String id, String name, String email, String userType) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.userType = userType;
        }
    }
}
