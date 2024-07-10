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

public class PatientDashboardActivity extends AppCompatActivity {

    private Button profileButton;
    private Button doctorButton;
    private ImageButton optionsMenuButton;
    private ImageView patientImage;
    private TextView patientName;
    private TextView patientInfo;

    private FirebaseAuth auth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard_activity);

        profileButton = findViewById(R.id.profile_button);
        doctorButton = findViewById(R.id.doctor_button);
        optionsMenuButton = findViewById(R.id.options_menu);
        patientImage = findViewById(R.id.patient_img);
        patientName = findViewById(R.id.patient_name);
        patientInfo = findViewById(R.id.patient_info);

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
                        String info = snapshot.child("info").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                        if (name != null) {
                            patientName.setText(name);
                        }

                        if (info != null) {
                            patientInfo.setText(info);
                        }

                        if (profileImageUrl != null) {
                            Glide.with(PatientDashboardActivity.this)
                                    .load(profileImageUrl)
                                    .into(patientImage);
                        } else {
                            patientImage.setImageResource(R.drawable.person_placeholder);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PatientDashboardActivity.this, "Failed to fetch patient details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PatientDashboardActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, PatientProfileSetting.class);
            startActivity(intent);
        });

        doctorButton.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDashboardActivity.this, DoctorsListActivity.class);
            startActivity(intent);
        });

        optionsMenuButton.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(PatientDashboardActivity.this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_help_center) {
                Intent helpIntent = new Intent(PatientDashboardActivity.this, HelpCenterActivity.class);
                startActivity(helpIntent);
                return true;
            } else if (item.getItemId() == R.id.action_contact) {
                Intent contactIntent = new Intent(PatientDashboardActivity.this, ContactActivity.class);
                startActivity(contactIntent);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }
}
