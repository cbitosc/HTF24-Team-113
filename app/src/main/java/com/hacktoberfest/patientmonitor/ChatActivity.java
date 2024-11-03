package com.hacktoberfest.patientmonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hacktoberfest.patientmonitor.Adapter.ChatUserAdapter;
import com.hacktoberfest.patientmonitor.Adapter.ChatsAdapter;
import com.hacktoberfest.patientmonitor.Models.ChatsModel;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.databinding.ActivityChatBinding;
import com.hacktoberfest.patientmonitor.databinding.BottomSheetDialogBinding;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ArrayList<ChatsModel> chatsList;
    ChatsAdapter chatsAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        shimmerFrameLayout=binding.shimmer;
        shimmerFrameLayout.startShimmer();
        setContentView(binding.getRoot());

        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();

        chatsList=new ArrayList<>();
        chatsAdapter=new ChatsAdapter(ChatActivity.this,chatsList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,false);
        binding.chatRv.setLayoutManager(linearLayoutManager);
        binding.chatRv.setAdapter(chatsAdapter);

        binding.editText.clearFocus();

        ArrayList<String> chatIds=new ArrayList<>();

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String role=snapshot.child("role").getValue(String.class);
                    if(role.equals("Doctor")) {
                        binding.textView14.setText("Patients");
                    }else {
                        binding.textView14.setText("Doctors");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("Users").child(auth.getUid()).child("userChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        ChatsModel model=new ChatsModel();
                        model.setChatId(dataSnapshot.child("chatId").getValue(String.class));
                        model.setReceiverId(dataSnapshot.child("receiverId").getValue(String.class));

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        binding.chatRv.setVisibility(View.VISIBLE);
                        chatsList.add(model);
                    }
                    chatsAdapter.notifyDataSetChanged();
                    binding.emptyTxt.setVisibility(View.GONE);
                }else {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!s.isEmpty()) {
                    ArrayList<ChatsModel> list=new ArrayList<>();
                    for(ChatsModel model:chatsList) {
                        database.getReference().child("Users").child(model.getReceiverId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user=snapshot.getValue(User.class);
                                if(user.getName().toLowerCase().contains(s.toLowerCase())) {
                                    list.add(model);
                                }
                                chatsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    chatsAdapter.updateList(list);
                }else{
                    chatsAdapter.updateList(chatsList);
                }
                return true;
            }
        });


        binding.createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog=new BottomSheetDialog(ChatActivity.this);
                BottomSheetDialogBinding bottomSheetDialogBinding=BottomSheetDialogBinding.inflate(getLayoutInflater());
                dialog.setContentView(bottomSheetDialogBinding.getRoot());
                ChatUserAdapter chatUserAdapter;
                ArrayList<User> list=new ArrayList<>();

                chatUserAdapter=new ChatUserAdapter(list,ChatActivity.this);

                bottomSheetDialogBinding.editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if(!s.isEmpty()) {
                            ArrayList<User> list1=new ArrayList<>();
                            for(User user:list) {
                                if (user.getName().toLowerCase().contains(s.toLowerCase())) {
                                    list1.add(user);
                                }
                            }
                            chatUserAdapter.updateList(list1);
                        }else{
                            chatUserAdapter.updateList(list);
                        }
                        return true;
                    }
                });

                database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())  {
                            User user=dataSnapshot.getValue(User.class);
                            user.setUserId(dataSnapshot.getKey());

                            database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        String role=snapshot.child("role").getValue(String.class);
                                        if(!auth.getUid().equals(dataSnapshot.getKey()) && !role.equals(user.getRole())) {
                                            list.add(user);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        chatUserAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                GridLayoutManager gridLayoutManager=new GridLayoutManager(ChatActivity.this,3);
                bottomSheetDialogBinding.usersRv.setLayoutManager(gridLayoutManager);
                bottomSheetDialogBinding.usersRv.setAdapter(chatUserAdapter);

                dialog.show();
            }
        });
    }
}

