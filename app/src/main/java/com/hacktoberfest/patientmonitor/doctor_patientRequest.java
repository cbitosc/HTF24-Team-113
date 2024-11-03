package com.hacktoberfest.patientmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.hacktoberfest.patientmonitor.Adapter.RequestAdapter;
import com.hacktoberfest.patientmonitor.Models.RequestModel;

import java.util.ArrayList;

public class doctor_patientRequest extends AppCompatActivity {

    RecyclerView recyclerView;
    RequestAdapter adapter;
    ArrayList<RequestModel> requestList;
    DatabaseReference requestsRef;
    String doctorId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_patientrequest);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        adapter = new RequestAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        doctorId = FirebaseAuth.getInstance().getUid();
        requestsRef = FirebaseDatabase.getInstance().getReference("Users").child(doctorId).child("requests");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading requests...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        loadRequests();
    }

    private void loadRequests() {
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                        RequestModel request = requestSnapshot.getValue(RequestModel.class);
                        if (request != null) {
                            request.setPatientId(requestSnapshot.getKey());
                            requestList.add(request);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(doctor_patientRequest.this, "No requests found", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.e("DoctorPatientRequest", "Error fetching requests: " + error.getMessage());
                Toast.makeText(doctor_patientRequest.this, "Error fetching requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
