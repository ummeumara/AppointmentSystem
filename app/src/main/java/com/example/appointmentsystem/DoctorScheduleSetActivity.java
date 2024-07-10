package com.example.appointmentsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DoctorScheduleSetActivity extends AppCompatActivity {

    private TextInputEditText dateEdit, startTimeEdit, endTimeEdit;
    private Button saveBtn;
    private DatabaseReference userRef;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_scheduale);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            dateEdit = findViewById(R.id.date_edit);
            startTimeEdit = findViewById(R.id.start_time_edit);
            endTimeEdit = findViewById(R.id.end_time_edit);
            saveBtn = findViewById(R.id.save_btn);
            calendar = Calendar.getInstance();

            dateEdit.setOnClickListener(v -> showDatePickerDialog());
            startTimeEdit.setOnClickListener(v -> showTimePickerDialog(startTimeEdit));
            endTimeEdit.setOnClickListener(v -> showTimePickerDialog(endTimeEdit));

            saveBtn.setOnClickListener(v -> saveDoctorSchedule());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(dateEdit, "MM/dd/yyyy");
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText timeEdit) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateLabel(timeEdit, "HH:mm");
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void updateLabel(EditText editText, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }

    private void saveDoctorSchedule() {
        String date = dateEdit.getText().toString();
        String startTime = startTimeEdit.getText().toString();
        String endTime = endTimeEdit.getText().toString();

        if (!date.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty()) {
            Map<String, String> schedule = new HashMap<>();
            schedule.put("date", date);
            schedule.put("startTime", startTime);
            schedule.put("endTime", endTime);

            userRef.child("schedule").setValue(schedule)
                    .addOnSuccessListener(aVoid -> Toast.makeText(DoctorScheduleSetActivity.this, "Schedule saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(DoctorScheduleSetActivity.this, "Failed to save schedule", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
