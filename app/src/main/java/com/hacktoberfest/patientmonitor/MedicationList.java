package com.hacktoberfest.patientmonitor;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Models.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MedicationList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;
    private String userDocumentID; // Store the user document ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication_list);

        // Retrieve the document ID from the intent
        userDocumentID = getIntent().getStringExtra("USER_DOCUMENT_ID");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicineList = new ArrayList<>();

        // Fetch medication reminders from Firebase
        fetchMedicationReminders();
    }

    private void fetchMedicationReminders() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        if (userDocumentID != null) {
            // Reference to the specific user's medication reminders in Realtime Database
            db.child("Users").child(userDocumentID).child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot reminderSnapshot : dataSnapshot.getChildren()) {
                        // Assuming the structure: /Users/{userDocumentID}/reminders/{time}
                        String medicineName = reminderSnapshot.child("medicine_name").getValue(String.class);
                        String time = reminderSnapshot.child("time").getValue(String.class);
                        String instruction = reminderSnapshot.child("instruction").getValue(String.class);

                        // Add the fetched medicine reminder to the list
                        medicineList.add(new Medicine(medicineName, time, instruction));
                    }
                    // Notify adapter of data changes
                    medicineAdapter = new MedicineAdapter(medicineList);
                    recyclerView.setAdapter(medicineAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle potential errors
                    Log.e("MedicationList", "Error fetching medication reminders", databaseError.toException());
                }
            });
        } else {
            Log.e("MedicationList", "User document ID is null");
        }
    }
}
