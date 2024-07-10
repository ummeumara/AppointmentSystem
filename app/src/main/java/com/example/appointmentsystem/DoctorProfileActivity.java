package com.example.appointmentsystem;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView nameTextView, specialtyTextView;
    private TableLayout scheduleTable;
    private Button bookButton;
    private DatabaseReference doctorRef;
    private String doctorId;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        profileImage = findViewById(R.id.doctor_profile_image);
        nameTextView = findViewById(R.id.doctor_name);
        specialtyTextView = findViewById(R.id.doctor_specialty);
        scheduleTable = findViewById(R.id.schedule_table);
        bookButton = findViewById(R.id.book_button);

        doctorId = getIntent().getStringExtra("doctorId");
        if (doctorId == null || doctorId.isEmpty()) {
            Toast.makeText(this, "Invalid doctor ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        doctorRef = FirebaseDatabase.getInstance().getReference("users").child(doctorId);

        loadDoctorInfo();
        loadDoctorSchedule();
        setupBookButton();
    }

    private void loadDoctorInfo() {
        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String specialty = dataSnapshot.child("specialization").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    nameTextView.setText(name);
                    specialtyTextView.setText(specialty);
                    if (imageUrl != null) {
                        Glide.with(DoctorProfileActivity.this).load(imageUrl).into(profileImage);
                    }
                } else {
                    Toast.makeText(DoctorProfileActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorProfileActivity.this, "Failed to load doctor info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDoctorSchedule() {
        doctorRef.child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String time = dateSnapshot.getValue(String.class);
                    addScheduleRow(date, time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorProfileActivity.this, "Failed to load doctor schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addScheduleRow(String date, String time) {
        TableRow row = new TableRow(this);

        TextView dateTextView = new TextView(this);
        dateTextView.setText(date);
        dateTextView.setPadding(8, 8, 8, 8);
        row.addView(dateTextView);

        TextView timeTextView = new TextView(this);
        timeTextView.setText(time);
        timeTextView.setPadding(8, 8, 8, 8);
        row.addView(timeTextView);

        scheduleTable.addView(row);
    }

    private void setupBookButton() {
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle booking action here
                Toast.makeText(DoctorProfileActivity.this, "Booked", Toast.LENGTH_SHORT).show();
                playSuccessSound();
                showSuccessFragment();
            }
        });
    }

    private void playSuccessSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.success_sound);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    private void showSuccessFragment() {
        SuccessFragment successFragment = new SuccessFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, successFragment)
                .addToBackStack(null)
                .commit();
    }
}
