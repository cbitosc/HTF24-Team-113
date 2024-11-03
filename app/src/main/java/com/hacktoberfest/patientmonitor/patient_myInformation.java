package com.hacktoberfest.patientmonitor;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class patient_myInformation extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private DatabaseReference userRef;
    private String selectedSymptom;
    private String selectedRating;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_myinformation);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        save = findViewById(R.id.saveBtn);
        // Array of symptoms
        String[] symptoms = new String[]{
                "Anger", "Allergies", "Back pain", "Cold", "Cough", "Headaches", "Depressed mood", "Dry Eyes", "Dry Skin", "Ear Infection", "Eye Infection", "Hair loss",
                "Heartburn", "Itching", "Memory Problems", "Mood Swings", "Pain", "Stomach Pain", "Rashes", "Skin problems", "Sleep problems", "Stress", "Voice Changes"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, symptoms);
        autoCompleteTextView.setAdapter(adapter);

        new Handler().postDelayed(() -> autoCompleteTextView.showDropDown(), 100);
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autoCompleteTextView.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("symptoms");

        // Show rating dialog and save symptom when selected
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedSymptom = (String) parent.getItemAtPosition(position);
            showRatingDialog();
        });
    }

    private void showRatingDialog() {
        Dialog ratingDialog = new Dialog(this);
        ratingDialog.setContentView(R.layout.emoji_rating_dialog);
        ratingDialog.show();

        TextView[] emojis = {
                ratingDialog.findViewById(R.id.emoji1),
                ratingDialog.findViewById(R.id.emoji2),
                ratingDialog.findViewById(R.id.emoji3),
                ratingDialog.findViewById(R.id.emoji4),
        };

        for (TextView emoji : emojis) {
            emoji.setOnClickListener(v -> {
                selectedRating = ((TextView) v).getText().toString();
                ratingDialog.dismiss();
            });
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedRating.isEmpty() && !selectedSymptom.isEmpty()){
                saveSymptom(selectedSymptom, selectedRating);
                }
            }
        });

    }

    private void saveSymptom(String symptomName, String rating) {
        // Generate a unique ID for each symptom
        String symptomId = userRef.push().getKey();

        // Current date for symptom entry
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create a map for the symptom entry
        Map<String, Object> symptomData = new HashMap<>();
        symptomData.put("name", symptomName);
        symptomData.put("date", currentDate);
        symptomData.put("rating", rating);

        // Save symptom to database under the generated ID
        if (symptomId != null) {
            userRef.child(symptomId).setValue(symptomData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(patient_myInformation.this, "Symptom added successfully", Toast.LENGTH_SHORT).show();
                    autoCompleteTextView.getText().clear();  // Clear input
                } else {
                    Toast.makeText(patient_myInformation.this, "Failed to add symptom", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
