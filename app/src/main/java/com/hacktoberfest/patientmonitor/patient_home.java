package com.hacktoberfest.patientmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.hacktoberfest.patientmonitor.Auth.Login;

public class patient_home extends AppCompatActivity {
    String user_mail, user_documentID;
    Button myInformationBtn, medicationsBtn,appointmentsBtn,connectDocter,addAppointmentsBtn, addMedicinesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);
        Intent intent = getIntent();
        if (intent != null) {
            user_mail = intent.getStringExtra("USER_EMAIL");
            user_documentID = intent.getStringExtra("USER_DOCUMENT_ID");
            Log.d("patients_reminder", "User email: " + user_mail);
            Log.d("patients_reminder", "User document ID: " + user_documentID);
        }

        myInformationBtn = findViewById(R.id.myInformationBtn);
        medicationsBtn = findViewById(R.id.medicationsBtn);
        appointmentsBtn = findViewById(R.id.appointmentsBtn);
        connectDocter = findViewById(R.id.connectDoctorBtn);
        addAppointmentsBtn = findViewById(R.id.addAppointmentsBtn);
        addMedicinesBtn = findViewById(R.id.addMedicinesBtn);

        myInformationBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, patient_myInformation.class);
            startActivity(i1);
        });
        medicationsBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, patient_medicationReminder.class);
            // Pass the user's email and document ID as extras
            i1.putExtra("USER_EMAIL", user_mail);
            i1.putExtra("USER_DOCUMENT_ID", user_documentID); // Pass the document ID
            startActivity(i1);
        });
        appointmentsBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, patient_appointments.class);
            startActivity(i1);
        });
        connectDocter.setOnClickListener(view->{
            Intent i1 = new Intent(this, DoctorListActivity.class);
            startActivity(i1);
        });
        addAppointmentsBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, patient_addAppointments.class);
            startActivity(i1);
        });
        addMedicinesBtn.setOnClickListener(view->{
            Intent i1 = new Intent(this, MedicationList.class);
            i1.putExtra("USER_DOCUMENT_ID", user_documentID); // Pass the document ID
            startActivity(i1);
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(patient_home.this, Login.class));
                finish();
        });
    }
}