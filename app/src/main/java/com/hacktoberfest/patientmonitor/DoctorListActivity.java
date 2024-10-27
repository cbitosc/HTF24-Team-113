package com.hacktoberfest.patientmonitor;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Adapter.DoctorAdapter;
import com.hacktoberfest.patientmonitor.Models.Doctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorListActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorConnectClickListener {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorlist);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(this, doctorList, this);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        fetchDoctorData();
    }

    private void fetchDoctorData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    if ("Doctor".equalsIgnoreCase(role)) {
                        String id = userSnapshot.getKey();
                        String name = userSnapshot.child("name").getValue(String.class);
                        String specialization = userSnapshot.child("qualification").getValue(String.class);

                        if (id != null && name != null && specialization != null) {
                            doctorList.add(new Doctor(id, name, specialization));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorListActivity.this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
                Log.e("DoctorListActivity", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onConnectClick(Doctor doctor) {
        String doctorName = doctor.getName();
        String patientId = auth.getCurrentUser().getUid(); // Get current user's patient ID
        getCurrentUserDetails(patientId, doctorName);
    }

    private void getCurrentUserDetails(String patientId, String doctorName) {
        databaseReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String patientName = dataSnapshot.child("name").getValue(String.class);

                    Log.d("patientName", patientName);

                    if (patientName != null) {
                        addConnectionRequestForDoctor(doctorName, patientId, patientName);
                        addConnectionRequestForPatient(doctorName, patientId, patientName);
                    } else {
                        Toast.makeText(DoctorListActivity.this, "Failed to retrieve patient details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DoctorListActivity.this, "Patient not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DoctorListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addConnectionRequestForDoctor(String doctorName, String patientId, String patientName) {
        databaseReference.orderByChild("name").equalTo(doctorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        String doctorId = doctorSnapshot.getKey(); // Get the doctor's ID
                        Map<String, Object> newRequest = new HashMap<>();
                        //newRequest.put("patientId", patientId);
                        newRequest.put("patientName", patientName);
                        newRequest.put("status", "Pending");
                        newRequest.put("timestamp", System.currentTimeMillis()); // Using current timestamp

                        databaseReference.child(doctorId).child("requests").child(patientId).setValue(newRequest)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FirebaseDatabase", "Doctor Request added successfully");
                                    Toast.makeText(DoctorListActivity.this, "Request sent to Doctor successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("FirebaseDatabase", "Error adding Doctor request", e);
                                    Toast.makeText(DoctorListActivity.this, "Failed to send request to Doctor", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(DoctorListActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DoctorListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addConnectionRequestForPatient(String doctorName, String patientId, String patientName) {
//        Map<String, Object> newRequestForPatient = new HashMap<>();
//        //newRequestForPatient.put("doctorId", doctorName); // Using doctor's name as doctor ID
//        newRequestForPatient.put("doctorName", doctorName); // Add the doctor's name
//        newRequestForPatient.put("status", "Pending");
//        newRequestForPatient.put("timestamp", System.currentTimeMillis()); // Using current timestamp
//
//        databaseReference.child(patientId).child("requests").child(docterId).push().setValue(newRequestForPatient)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d("FirebaseDatabase", "Patient Request added successfully");
//                })
//                .addOnFailureListener(e -> {
//                    Log.w("FirebaseDatabase", "Error adding Patient request", e);
//                });

        databaseReference.orderByChild("name").equalTo(doctorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                        String doctorId = doctorSnapshot.getKey(); // Get the doctor's ID
                        Map<String, Object> newRequest = new HashMap<>();
                        //newRequest.put("patientId", patientId);
                        newRequest.put("doctorName", doctorName); // Add the doctor's name
                        newRequest.put("status", "Pending");
                        newRequest.put("timestamp", System.currentTimeMillis()); // Using current timestamp

                        databaseReference.child(patientId).child("requests").child(doctorId).setValue(newRequest)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FirebaseDatabase", "Patient Request added successfully");
                                    Toast.makeText(DoctorListActivity.this, "Request sent to Doctor successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("FirebaseDatabase", "Error adding Patient request", e);
                                    Toast.makeText(DoctorListActivity.this, "Failed to send request to Doctor", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(DoctorListActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DoctorListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }


}
