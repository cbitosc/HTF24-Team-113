package com.hacktoberfest.patientmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Auth.Login;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.databinding.DoctorProfileBinding;
import com.squareup.picasso.Picasso;

public class doctor_profile extends AppCompatActivity {
    DoctorProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DoctorProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent=new Intent(doctor_profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(doctor_profile.this,EditProfile.class);
                startActivity(intent);
            }
        });

        database.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    User user=snapshot.getValue(User.class);
                    binding.username.setText(user.getName());
                    binding.email.setText(user.getEmail());
                    binding.phoneNo.setText(user.getMobileNo());
                    binding.address.setText(user.getAddress());
                    binding.about.setText(user.getAbout());
                    binding.hospital.setText(user.getHospital());
                    binding.education.setText(user.getEducation());
                    binding.special.setText(user.getSpecialization());
                    Picasso.get().load(user.getProfile()).into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

