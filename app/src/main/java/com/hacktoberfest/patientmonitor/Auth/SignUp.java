package com.hacktoberfest.patientmonitor.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.MainActivity;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.R;
import com.hacktoberfest.patientmonitor.doctor_home;
import com.hacktoberfest.patientmonitor.patient_home;

public class SignUp extends AppCompatActivity {

    EditText editEmail,editPass,editUsername;
    Button signupBtn;
    TextView signupText;
    FirebaseAuth auth;
    FirebaseDatabase database;
    android.app.ProgressDialog progressDialog;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        editEmail=findViewById(R.id.editEmail);
        editPass=findViewById(R.id.editPass);
        editUsername=findViewById(R.id.editUsername);
        signupBtn=findViewById(R.id.signupBtn);
        signupText=findViewById(R.id.signupText);

        Spinner mySpinner = findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter(SignUp.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.dropdown_options));
        myAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mySpinner.setAdapter(myAdapter);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = String.valueOf(parent.getItemAtPosition(position));
                role=selectedOption;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,Login.class);
                startActivity(intent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String email=editEmail.getText().toString().trim();
                String pass=editPass.getText().toString().trim();
                String username=editUsername.getText().toString().trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(username)) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                }else if(pass.length()<6) {
                    progressDialog.dismiss();
                    editPass.setError("password must be 6 chars");
                    Toast.makeText(SignUp.this, "Password must be greater than 6 characters", Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    editEmail.setError("Invalid email");
                    Toast.makeText(SignUp.this, "Invalid email", Toast.LENGTH_SHORT).show();
                }else {
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String profile="";
                                if(role.equals("Doctor")) {
                                    profile="https://firebasestorage.googleapis.com/v0/b/patient-monitor-54324.appspot.com/o/doctor.avif?alt=media&token=1e6ebba8-22de-4cad-ba27-a5683f8fbeab";
                                }else if(role.equals("Patient")) {
                                    profile="https://firebasestorage.googleapis.com/v0/b/patient-monitor-54324.appspot.com/o/patient.webp?alt=media&token=1ead44dc-6e01-4d15-a2e5-b5df45c8ef96";
                                }
                                User user=new User();
                                user.setProfile(profile);
                                user.setName(username);
                                user.setEmail(email);
                                user.setPassword(pass);
                                user.setRole(role);

                                Log.d("userId", "onComplete: "+task.getResult().getUser().getUid());
                                DatabaseReference databaseReference=database.getReference().child("Users").child(task.getResult().getUser().getUid());
                                databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            try {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()) {
                                                            User user=snapshot.getValue(User.class);
                                                            String role=user.getRole();
                                                            if(role.equals("Doctor")) startActivity(new Intent(SignUp.this, doctor_home.class));
                                                            else if(role.equals("Patient")) startActivity(new Intent(SignUp.this, patient_home.class));
                                                            progressDialog.dismiss();
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
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}



