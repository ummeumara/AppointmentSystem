package com.example.appointmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorsListActivity extends AppCompatActivity {

    private ListView listViewDoctors;
    private ArrayList<Doctor> doctorList;
    private ArrayAdapter<Doctor> doctorAdapter;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);

        listViewDoctors = findViewById(R.id.list_view_doctors);

        doctorList = new ArrayList<>();
        doctorAdapter = new ArrayAdapter<Doctor>(this, R.layout.item_doctor, R.id.doctor_name, doctorList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView imageView = view.findViewById(R.id.doctor_image);
                TextView nameTextView = view.findViewById(R.id.doctor_name);
                TextView specialtyTextView = view.findViewById(R.id.doctor_specialty);
                Button bookButton = view.findViewById(R.id.book_button);

                Doctor doctor = doctorList.get(position);
                Glide.with(DoctorsListActivity.this).load(doctor.getProfileImage()).into(imageView);
                nameTextView.setText(doctor.getName());
                specialtyTextView.setText(doctor.getSpecialty());

                // Book button click listener
                bookButton.setOnClickListener(v -> {
                    Intent intent = new Intent(DoctorsListActivity.this, DoctorProfileActivity.class);
                    intent.putExtra("doctorId", doctor.getId());
                    startActivity(intent);
                });

                return view;
            }
        };

        listViewDoctors.setAdapter(doctorAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        fetchDoctors();
    }

    private void fetchDoctors() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userType").exists() && snapshot.child("userType").getValue(String.class).equals("Doctor")) {
                        String id = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        String specialty = snapshot.child("specialization").getValue(String.class);
                        String imageUrl = snapshot.child("profileImage").getValue(String.class);
                        doctorList.add(new Doctor(id, name, specialty, imageUrl));
                    }
                }
                doctorAdapter.notifyDataSetChanged();
                if (doctorList.isEmpty()) {
                    Toast.makeText(DoctorsListActivity.this, "No doctors found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsListActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
