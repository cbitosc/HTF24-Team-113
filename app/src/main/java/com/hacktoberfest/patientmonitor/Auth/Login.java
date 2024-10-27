package com.hacktoberfest.patientmonitor.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.MainActivity;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.R;
import com.hacktoberfest.patientmonitor.databinding.ActivityLoginBinding;
import com.hacktoberfest.patientmonitor.doctor_home;
import com.hacktoberfest.patientmonitor.patient_home;

public class Login extends AppCompatActivity {
    EditText editEmail, editPass;
    Button loginBtn;
    TextView signupText;
    FirebaseAuth auth;
    android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);
        loginBtn = findViewById(R.id.signupBtn);
        signupText = findViewById(R.id.signupText);

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String email = editEmail.getText().toString().trim();
                String pass = editPass.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6) {
                    progressDialog.dismiss();
                    editPass.setError("Password must be 6 chars");
                    Toast.makeText(Login.this, "Password must be greater than 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    editEmail.setError("Invalid email");
                    Toast.makeText(Login.this, "Invalid email", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getUid(); // This is the Firebase Auth user ID
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Users")
                                        .child(userId) // Fetch user data using userId
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    User user = snapshot.getValue(User.class);
                                                    String role = user.getRole();
                                                    String userEmail = user.getEmail();

                                                    // Get the document ID for the user
                                                    String documentId = snapshot.getKey(); // This is the document ID

                                                    progressDialog.dismiss();
                                                    Intent intent;
                                                    if ("Doctor".equals(role)) {
                                                        intent = new Intent(Login.this, doctor_home.class);
                                                    } else {
                                                        intent = new Intent(Login.this, patient_home.class);
                                                    }

                                                    // Pass the user's email and document ID as extras
                                                    intent.putExtra("USER_EMAIL", userEmail);
                                                    intent.putExtra("USER_DOCUMENT_ID", documentId); // Pass the document ID
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }
}
