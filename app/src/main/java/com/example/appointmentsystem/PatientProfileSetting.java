package com.example.appointmentsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PatientProfileSetting extends AppCompatActivity {

    private ImageView profileImg;
    private EditText nameInput, emailInput, phoneInput;
    private Button saveButton;
    private DatabaseReference userRef;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile_setting);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference("patient_profile_images");

            profileImg = findViewById(R.id.profile_img);
            nameInput = findViewById(R.id.name_input);
            emailInput = findViewById(R.id.email_input);
            phoneInput = findViewById(R.id.phone_input);
            saveButton = findViewById(R.id.save_button);

            // Set up click listener for profile image to select image
            profileImg.setOnClickListener(v -> openFileChooser());

            // Set up click listener for "Change Image" button to select image
            Button changeImageButton = findViewById(R.id.change_picture_button);
            changeImageButton.setOnClickListener(v -> openFileChooser());

            // Set up click listener for save button to save changes
            saveButton.setOnClickListener(v -> savePatientInfo());

            // Load existing patient information from Firebase
            loadPatientInfo();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileImg);
        }
    }

    private void loadPatientInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    nameInput.setText(name);
                    emailInput.setText(email);
                    phoneInput.setText(phone);
                    if (imageUrl != null) {
                        Glide.with(PatientProfileSetting.this).load(imageUrl).into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientProfileSetting.this, "Failed to load patient info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePatientInfo() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        // Save the text information to Firebase Realtime Database
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);

        // Save the image to Firebase Storage if the image has been changed
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.child("profileImage").setValue(imageUrl);
                        Toast.makeText(PatientProfileSetting.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the profile setting activity
                    }).addOnFailureListener(e -> Toast.makeText(PatientProfileSetting.this, "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(PatientProfileSetting.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the profile setting activity
        }
    }
}
