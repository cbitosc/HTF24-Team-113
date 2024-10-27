package com.hacktoberfest.patientmonitor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Chat_win;
import com.hacktoberfest.patientmonitor.Models.ChatsModel;
import com.hacktoberfest.patientmonitor.Models.User;
import com.hacktoberfest.patientmonitor.R;
import com.hacktoberfest.patientmonitor.databinding.ActivityChatBinding;
import com.hacktoberfest.patientmonitor.databinding.UserItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.viewholder> {

    Context context;
    ArrayList<ChatsModel> list;

    public ChatsAdapter(Context context, ArrayList<ChatsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatsAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.viewholder holder, int position) {
        ChatsModel chatsModel=list.get(position);

//        FirebaseDatabase.getInstance().getReference().child("chats").child(chatsModel.getChatId()).child("messages").child(chatsModel.getLastMsgId())
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                MessageModel messageModel=snapshot.getValue(MessageModel.class);
//                                holder.binding.lastMsg.setText(messageModel.getMessage());
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(chatsModel.getReceiverId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile()).into(holder.binding.profileImage);
                holder.binding.name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.chatItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityChatBinding binding=ActivityChatBinding.inflate(LayoutInflater.from(view.getContext()));
                binding.editText.clearFocus();
                Intent intent=new Intent(context, Chat_win.class);
                intent.putExtra("receiverId",chatsModel.getReceiverId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void updateList(ArrayList<ChatsModel> list) {
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        UserItemBinding binding;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding= UserItemBinding.bind(itemView);
        }
    }
}











