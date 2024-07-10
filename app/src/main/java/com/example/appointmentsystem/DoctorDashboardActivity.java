package com.example.appointmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
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

public class DoctorDashboardActivity extends AppCompatActivity {

    private Button profileButton;

    private Button scheduleButton;
    private ImageButton optionsMenuButton;
    private ImageView doctorImage;
    private TextView doctorName;
    private TextView doctorSpeciality;

    private FirebaseAuth auth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard_activity);

        profileButton = findViewById(R.id.profile_button);

        scheduleButton = findViewById(R.id.schedule_button);
        optionsMenuButton = findViewById(R.id.options_menu);
        doctorImage = findViewById(R.id.doctor_img);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpeciality = findViewById(R.id.doctor_speciality);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String specialization = snapshot.child("specialization").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                        if (name != null) {
                            doctorName.setText(name);
                        }

                        if (specialization != null) {
                            doctorSpeciality.setText(specialization);
                        }

                        if (profileImageUrl != null) {
                            Glide.with(DoctorDashboardActivity.this)
                                    .load(profileImageUrl)
                                    .into(doctorImage);
                        } else {
                            doctorImage.setImageResource(R.drawable.person_placeholder);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DoctorDashboardActivity.this, "Failed to fetch doctor details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(DoctorDashboardActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboardActivity.this, DoctorProfileSetting.class);
            startActivity(intent);
        });


        scheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDashboardActivity.this, DoctorScheduleSetActivity.class);
            startActivity(intent);
        });

        optionsMenuButton.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(DoctorDashboardActivity.this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_help_center) {
                Intent helpIntent = new Intent(DoctorDashboardActivity.this, HelpCenterActivity.class);
                startActivity(helpIntent);
                return true;
            } else if (item.getItemId() == R.id.action_contact) {
                Intent contactIntent = new Intent(DoctorDashboardActivity.this, ContactActivity.class);
                startActivity(contactIntent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
}