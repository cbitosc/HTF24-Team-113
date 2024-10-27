package com.hacktoberfest.patientmonitor;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class patient_medicationReminder extends AppCompatActivity {
    String medicineName;

    private TextInputEditText timeInput, instructionInput, medicineNameInput;
    private int selectedHour, selectedMinute;
    Intent intent;
    Button save;
    String user_mail, user_documentID;
    DatabaseReference db; // Reference for Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.patient_medications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intent = getIntent();
        if (intent != null) {
            user_mail = intent.getStringExtra("USER_EMAIL");
            user_documentID = intent.getStringExtra("USER_DOCUMENT_ID");
            Log.d("patients_reminder", "User email: " + user_mail);
            Log.d("patients_reminder", "User document ID: " + user_documentID);
        }

        // Initialize the Realtime Database reference
        db = FirebaseDatabase.getInstance().getReference();

        Spinner unitSpinner = findViewById(R.id.unitSpinner);
        String[] units = {"mg", "g", "ml", "L", "units"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        timeInput = findViewById(R.id.timeInput);
        save = findViewById(R.id.save_btn);
        instructionInput = findViewById(R.id.instruction);
        medicineNameInput = findViewById(R.id.medicineName);

        save.setOnClickListener(view -> {
            saveReminderToFirebase();
            scheduleDailyNotification(selectedHour, selectedMinute, medicineNameInput.getText().toString().trim()); // Pass medicine name
        });

        // Set current time on load
        updateTimeInputWithCurrentTime();

        // Set click listener for the time input
        timeInput.setOnClickListener(v -> showTimePicker());
    }

    private void updateTimeInputWithCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);
        @SuppressLint("DefaultLocale")
        String currentTime = String.format("%02d:%02d", selectedHour, selectedMinute);
        timeInput.setText(currentTime);
    }

    private void saveReminderToFirebase() {
        medicineName = medicineNameInput.getText().toString().trim();
        String instruction = instructionInput.getText().toString().trim();
        @SuppressLint("DefaultLocale")
        String time = String.format("%02d:%02d", selectedHour, selectedMinute);

        if (medicineName.isEmpty() || instruction.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user_documentID != null) {
            // Reference to the specific user's reminders in Realtime Database
            DatabaseReference userRef = db.child("Users").child(user_documentID).child("reminders").child(time);

            // Create a map to hold the reminder data
            Map<String, Object> reminderData = new HashMap<>();
            reminderData.put("medicine_name", medicineName);
            reminderData.put("instruction", instruction);
            reminderData.put("time", time);

            userRef.setValue(reminderData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(patient_medicationReminder.this, "Reminder saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("patients_reminder", "Error saving reminder", e);
                        Toast.makeText(patient_medicationReminder.this, "Failed to save reminder", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    this.selectedHour = selectedHour;
                    this.selectedMinute = selectedMinute;
                    @SuppressLint("DefaultLocale")
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(selectedTime); // Set the selected time in the input field
                    Toast.makeText(getApplicationContext(), "Time set to: " + selectedTime, Toast.LENGTH_SHORT).show();
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void scheduleDailyNotification(int hour, int minute, String medicineName) { // Accept medicine name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Call the AlarmHelper to schedule the notification with the medicine name
        AlarmHelper.scheduleDailyNotification(this, hour, minute, medicineName);
    }
}