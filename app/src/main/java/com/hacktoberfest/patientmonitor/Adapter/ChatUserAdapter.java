package com.hacktoberfest.patientmonitor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Chat_win;
import com.hacktoberfest.patientmonitor.Models.MessageModel;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.R;
import com.hacktoberfest.patientmonitor.databinding.BottomSheetDialogBinding;
import com.hacktoberfest.patientmonitor.databinding.FriendRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.viewholder> {

    ArrayList<User> list;
    Context context;

    public ChatUserAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatUserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.friend_rv_sample,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.viewholder holder, int position) {

        User user=list.get(position);
        Picasso.get().load(user.getProfile()).into(holder.binding.profileImage);
        holder.binding.friendName.setText(user.getName());

        holder.binding.friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogBinding binding=BottomSheetDialogBinding.inflate(LayoutInflater.from(view.getContext()));
                String senderRoom= FirebaseAuth.getInstance().getUid()+user.getUserId();
                String receiverRoom=user.getUserId()+FirebaseAuth.getInstance().getUid();

                binding.editText.clearFocus();

                FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Intent intent=new Intent(context, Chat_win.class);
                            intent.putExtra("receiverId",user.getUserId());
                            context.startActivity(intent);

                        }else {
                            long time=new Date().getTime();
                            ArrayList<String> members=new ArrayList<>();
                            members.add(FirebaseAuth.getInstance().getUid());
                            members.add(user.getUserId());

                            HashMap<String,String> map1=new HashMap<>();
                            HashMap<String,String> map2=new HashMap<>();

                            map1.put("chatId",senderRoom);
                            map1.put("receiverId",user.getUserId());

                            map2.put("chatId",receiverRoom);
                            map2.put("receiverId",FirebaseAuth.getInstance().getUid());
                            MessageModel messageModel=new MessageModel(FirebaseAuth.getInstance().getUid(),time,user.getUserId());
                            messageModel.setMessage("");

                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages").push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("userChats")
                                                    .push().setValue(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId()).child("userChats")
                                                                    .push().setValue(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Intent intent=new Intent(context, Chat_win.class);
                                                                            intent.putExtra("receiverId",user.getUserId());
                                                                            context.startActivity(intent);
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public void updateList(ArrayList<User> list) {
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        FriendRvSampleBinding binding;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding=FriendRvSampleBinding.bind(itemView);
        }
    }
}
