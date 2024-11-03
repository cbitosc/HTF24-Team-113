package com.hacktoberfest.patientmonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.databinding.ActivityEditProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Uri uri;
    ProgressDialog dialog;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating Profile...");
        dialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    User user=snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfile()).into(binding.profileImage);
                    binding.username.setText(user.getName());
                    binding.email.setText(user.getEmail());
                    binding.phoneNo.setText(user.getMobileNo());
                    binding.addressInfo.setText(user.getAddress());
                    binding.aboutMe.setText(user.getAbout());
                    binding.education.setText(user.getEducation());
                    binding.special.setText(user.getSpecialization());
                    binding.hospital.setText(user.getHospital());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String name=binding.username.getText().toString();
                String address=binding.addressInfo.getText().toString();
                String about=binding.aboutMe.getText().toString();
                String phoneNo=binding.phoneNo.getText().toString();
                String email=binding.email.getText().toString();
                String education=binding.education.getText().toString();
                String specialization=binding.special.getText().toString();
                String hospital=binding.hospital.getText().toString();

                if(name.isEmpty() || address.isEmpty() || about.isEmpty() || phoneNo.isEmpty() || email.isEmpty()) {
                    Toast.makeText(EditProfile.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                DatabaseReference reference=database.getReference().child("Users").child(auth.getUid());

                if(uri !=null) {
                    StorageReference storageReference=storage.getReference().child("profile_image").child(auth.getUid());
                    storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profile=uri.toString();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("name", name);
                                    updates.put("address", address);
                                    updates.put("about", about);
                                    updates.put("mobileNo", phoneNo);
                                    updates.put("email", email);
                                    updates.put("profile", profile);
                                    updates.put("education", education);
                                    updates.put("specialization", specialization);
                                    updates.put("hospital", hospital);

                                    reference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(EditProfile.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }else {
                                                dialog.dismiss();
                                                Toast.makeText(EditProfile.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("address", address);
                    updates.put("about", about);
                    updates.put("mobileNo", phoneNo);
                    updates.put("email", email);
                    updates.put("education", education);
                    updates.put("specialization", specialization);
                    updates.put("hospital", hospital);

                    reference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(EditProfile.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(EditProfile.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if(requestCode==10) {
                uri=data.getData();
                binding.profileImage.setImageURI(uri);
            }
        }
    }
}


