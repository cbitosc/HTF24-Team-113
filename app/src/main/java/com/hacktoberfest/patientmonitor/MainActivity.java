package com.hacktoberfest.patientmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Auth.Login;
import com.hacktoberfest.patientmonitor.Auth.SignUp;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                auth.signOut();
//                Intent intent=new Intent(MainActivity.this,Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

//        binding.chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this,ChatActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        progressDialog.show();
        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null) {
            progressDialog.dismiss();
            Intent intent=new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        } else {
            try {
                FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            User user=snapshot.getValue(User.class);
                            String role=user.getRole();
                            progressDialog.dismiss();
                            if(role.equals("Doctor")) startActivity(new Intent(MainActivity.this, doctor_home.class));
                            else if(role.equals("Patient")) startActivity(new Intent(MainActivity.this, patient_home.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



