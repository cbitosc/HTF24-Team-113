package com.hacktoberfest.patientmonitor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hacktoberfest.patientmonitor.Adapter.MessageAdapter;
import com.hacktoberfest.patientmonitor.Models.MessageModel;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.databinding.ActivityChatWinBinding;
import com.hacktoberfest.patientmonitor.databinding.ImgBottomSheetBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Chat_win extends AppCompatActivity {

    ActivityChatWinBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Intent intent;
    String receiverId;
    ArrayList<MessageModel> messageList;
    MessageAdapter messageAdapter;
    ActivityResultLauncher<String> galleryLauncer;
    String senderRoom;
    String receiverRoom;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatWinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        intent=getIntent();
        receiverId=intent.getStringExtra("receiverId");
        senderRoom=auth.getUid()+receiverId;
        receiverRoom=receiverId+auth.getUid();
        messageList=new ArrayList<>();
        progressDialog=new ProgressDialog(Chat_win.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait...");
        progressDialog.setTitle("Uploading image");

        database.getReference().child("Users").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile()).into(binding.profileImage);
                binding.receiverName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    MessageModel model=dataSnapshot.getValue(MessageModel.class);
                    model.setMessageId(dataSnapshot.getKey());
                    messageList.add(model);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.editMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String msg=binding.editMsg.getText().toString();
                if(!msg.isEmpty()) {
                    binding.card1.setVisibility(View.GONE);
//                    binding.editMsg.setPadding(dpToPx(16), 0, 0, 0);
                }else {
                    binding.card1.setVisibility(View.VISIBLE);
//                    binding.editMsg.setPadding(dpToPx(42), 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog=new BottomSheetDialog(Chat_win.this);
                ImgBottomSheetBinding imgBinding=ImgBottomSheetBinding.inflate(getLayoutInflater());
                dialog.setContentView(imgBinding.getRoot());

                Toast.makeText(Chat_win.this,"clikced",Toast.LENGTH_SHORT);

                imgBinding.camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,10);
                        dialog.dismiss();
                    }
                });

                imgBinding.albums.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        galleryLauncer.launch("image/*");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        galleryLauncer=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                messageHelper(o);
            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=binding.editMsg.getText().toString();
                if(TextUtils.isEmpty(msg)) {
                    Toast.makeText(Chat_win.this, "Enter msg", Toast.LENGTH_SHORT).show();
                    return;
                }
                MessageModel model=new MessageModel(auth.getUid(),new Date().getTime(),receiverId);
                model.setMessage(msg);
                binding.editMsg.setText("");

                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });
            }
        });

        messageAdapter=new MessageAdapter(this,messageList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(Chat_win.this,LinearLayoutManager.VERTICAL,false);
        binding.messageRv.setLayoutManager(linearLayoutManager);
        binding.messageRv.setAdapter(messageAdapter);

    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10 && resultCode==RESULT_OK) {
            if(data !=null) {
                Bitmap bitmap=(Bitmap) data.getExtras().get("data");
                Uri uri=getImageUri(Chat_win.this,bitmap);
                messageHelper(uri);
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
        String path=MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);
    }

    private void messageHelper(Uri data) {
        progressDialog.show();
        MessageModel model = new MessageModel(auth.getUid(), new Date().getTime(), receiverId);

        StorageReference storageReference = storage.getReference().child("chats").child("chats").child(senderRoom).child(new Date().getTime() + "");
        storageReference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        model.setImage(uri.toString());

                        database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Chat_win.this, "image sent", Toast.LENGTH_SHORT);
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.msg_options,menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }
}



