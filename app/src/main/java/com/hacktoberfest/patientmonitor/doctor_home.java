package com.hacktoberfest.patientmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hacktoberfest.patientmonitor.Auth.Login;

public class doctor_home extends AppCompatActivity {
    Button myPatientsBtn, appointmentBtn,profileBtn,patientRequestBtn,myCalendarBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);

        myPatientsBtn = findViewById(R.id.myPatientsBtn);
        appointmentBtn = findViewById(R.id.appointementBtn);
        profileBtn = findViewById(R.id.profileBtn);
        patientRequestBtn = findViewById(R.id.patientRequestBtn);
        myCalendarBtn = findViewById(R.id.myCalendarBtn);

        myPatientsBtn.setOnClickListener(view->{
                Intent i1 = new Intent(this, doctor_myPatients.class);
                startActivity(i1);
        });
        appointmentBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, doctor_appointment.class);
            startActivity(i1);
        });
        profileBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, doctor_profile.class);
            startActivity(i1);
        });
        patientRequestBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, doctor_patientRequest.class);
            startActivity(i1);
        });
        myCalendarBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, doctor_myCalender.class);
            startActivity(i1);
        });
        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(doctor_home.this, Login.class));
            finish();
        });
    }
}

