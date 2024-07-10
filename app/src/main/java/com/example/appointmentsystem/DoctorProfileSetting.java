package com.example.appointmentsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DoctorProfileSetting extends AppCompatActivity {

    private ImageView profileImg;
    private TextInputEditText nameEdit, emailEdit, specializationEdit, phoneEdit, addressEdit;
    private Button saveBtn, editImageButton;
    private DatabaseReference userRef;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile_setting_activity);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference("doctor_profile_images"); // Replace "doctor_profile_images" with your desired storage path

            profileImg = findViewById(R.id.profile_img);
            nameEdit = findViewById(R.id.name_edit);
            emailEdit = findViewById(R.id.email_edit);
            specializationEdit = findViewById(R.id.specialization_edit);
            phoneEdit = findViewById(R.id.phone_edit);
            addressEdit = findViewById(R.id.address_edit);
            saveBtn = findViewById(R.id.save_btn);
            editImageButton = findViewById(R.id.edit_image_button);
            ImageButton optionsMenuButton = findViewById(R.id.options_menu);

            editImageButton.setOnClickListener(v -> openFileChooser());

            saveBtn.setOnClickListener(v -> saveDoctorInfo());

            // Load existing doctor information from Firebase
            loadDoctorInfo();
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
            profileImg.setImageURI(imageUri);
        }
    }

    private void loadDoctorInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String specialization = dataSnapshot.child("specialization").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    nameEdit.setText(name);
                    emailEdit.setText(email);
                    specializationEdit.setText(specialization);
                    phoneEdit.setText(phone);
                    addressEdit.setText(address);
                    if (imageUrl != null) {
                        Glide.with(DoctorProfileSetting.this).load(imageUrl).into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorProfileSetting.this, "Failed to load doctor info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDoctorInfo() {
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String specialization = specializationEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String address = addressEdit.getText().toString();

        // Save the text information to Firebase Realtime Database
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("specialization").setValue(specialization);
        userRef.child("phone").setValue(phone);
        userRef.child("address").setValue(address);

        // Save the image to Firebase Storage if the image has been changed
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.child("profileImage").setValue(imageUrl);
                        Toast.makeText(DoctorProfileSetting.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        openDoctorDashboard(); // Open Doctor Dashboard after saving
                    }).addOnFailureListener(e -> Toast.makeText(DoctorProfileSetting.this, "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(DoctorProfileSetting.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            openDoctorDashboard(); // Open Doctor Dashboard after saving
        }
    }

    private void openDoctorDashboard() {
        Intent intent = new Intent(DoctorProfileSetting.this, DoctorDashboardActivity.class);
        startActivity(intent);
        finish(); // Close the profile setting activity
    }
}
