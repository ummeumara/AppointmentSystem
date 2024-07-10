package com.example.appointmentsystem;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointments extends AppCompatActivity {

    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        appointmentsRecyclerView = findViewById(R.id.appointments_recycler_view);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        appointmentAdapter.setOnItemClickListener(position -> cancelAppointment(position));

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

            // Load appointments from Firebase
            loadAppointments();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAppointments() {
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);
                }
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorAppointments.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment(int position) {
        // Get the appointment to be canceled
        Appointment appointment = appointmentList.get(position);
        // Assuming appointment IDs are stored as keys in the database
        String appointmentId = appointmentsRef.push().getKey();

        // Remove the appointment from the database
        if (appointmentId != null) {
            appointmentsRef.child(appointmentId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Remove the appointment from the list and notify the adapter
                        appointmentList.remove(position);
                        appointmentAdapter.notifyItemRemoved(position);
                        Toast.makeText(DoctorAppointments.this, "Appointment canceled", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(DoctorAppointments.this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show());
        }
    }
}
