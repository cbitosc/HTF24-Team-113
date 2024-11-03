package com.hacktoberfest.patientmonitor.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hacktoberfest.patientmonitor.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hacktoberfest.patientmonitor.Models.MessageModel;
import com.hacktoberfest.patientmonitor.databinding.ReceiverLayoutBinding;
import com.hacktoberfest.patientmonitor.databinding.SenderLayoutBinding;
import com.hacktoberfest.patientmonitor.image_view;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> list;
    int send=1;
    int receive=2;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    FirebaseAuth auth=FirebaseAuth.getInstance();

    public MessageAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==send) {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewholder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new receiverViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getClass()==receiverViewholder.class) {
            receiverViewholder viewHolder=(receiverViewholder) holder;
            MessageModel messageModel=list.get(position);

            if(messageModel.getImage() !=null){
                viewHolder.binding.msgPic.setVisibility(View.VISIBLE);
                viewHolder.binding.msg.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImage()).into(viewHolder.binding.msgPic);
//                Glide.with(context).load(messageModel.getImage()).into(viewHolder.binding.msgPic);

                viewHolder.binding.msgPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, image_view.class);
                        intent.putExtra("url",messageModel.getImage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                viewHolder.binding.msgPic.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu menu=new PopupMenu(context,view);
                        menu.getMenuInflater().inflate(R.menu.msg_options,menu.getMenu());
                        String senderRoom=messageModel.getSenderUid()+messageModel.getReceiverUid();
                        String receiverRoom=messageModel.getReceiverUid()+messageModel.getSenderUid();
                        menu.getMenu().findItem(R.id.d_all).setVisible(false);

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId()==R.id.d_all) {
                                    menu.getMenu().findItem(R.id.d_all).setVisible(false);
                                    return false;
                                }else if(menuItem.getItemId()==R.id.d_me) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    database.getReference().child("chats").child(receiverRoom).child("messages").child(messageModel.getMessageId())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                }
                                return false;
                            }
                        });
                        menu.show();
                        return true;
                    }
                });


                return;
            }
            else if(messageModel.getMessage() !=null && !messageModel.getMessage().isEmpty()) {
                viewHolder.binding.msg.setVisibility(View.VISIBLE);
                viewHolder.binding.msgPic.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
                viewHolder.binding.msg.setText(messageModel.getMessage());

                viewHolder.binding.msg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu menu=new PopupMenu(context,view);
                        menu.getMenuInflater().inflate(R.menu.msg_options,menu.getMenu());
                        String senderRoom=messageModel.getSenderUid()+messageModel.getReceiverUid();
                        String receiverRoom=messageModel.getReceiverUid()+messageModel.getSenderUid();
                        menu.getMenu().findItem(R.id.d_all).setVisible(false);

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId()==R.id.d_all) {
                                    menu.getMenu().findItem(R.id.d_all).setVisible(false);
                                    return false;
                                }else if(menuItem.getItemId()==R.id.d_me) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    database.getReference().child("chats").child(receiverRoom).child("messages").child(messageModel.getMessageId())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                }else if(menuItem.getItemId()==R.id.copy) {
//                                    menuItem.setIcon(R.drawable.copy);
                                    ClipboardManager clipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboardManager.setText(messageModel.getMessage());
                                    return true;
                                }
                                return false;
                            }
                        });
                        menu.show();
                        return true;
                    }
                });
                return;
            }
            else {
                viewHolder.binding.msgPic.setVisibility(View.GONE);
                viewHolder.binding.msg.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
            }

        }else {
            senderViewholder viewHolder=(senderViewholder) holder;
            MessageModel messageModel=list.get(position);

            if(messageModel.getImage() !=null) {
                viewHolder.binding.msgPic.setVisibility(View.VISIBLE);
                viewHolder.binding.msg.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImage()).into(viewHolder.binding.msgPic);
//                Glide.with(context).load(messageModel.getImage()).into(viewHolder.binding.msgPic);

                viewHolder.binding.msgPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, image_view.class);
                        intent.putExtra("url",messageModel.getImage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                viewHolder.binding.msgPic.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu menu=new PopupMenu(context,view);
                        menu.getMenuInflater().inflate(R.menu.msg_options,menu.getMenu());
                        String senderRoom=messageModel.getSenderUid()+messageModel.getReceiverUid();
                        String receiverRoom=messageModel.getReceiverUid()+messageModel.getSenderUid();

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId()==R.id.d_all) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    deleteAll(messageModel,senderRoom,receiverRoom);
                                    return true;
                                }else if(menuItem.getItemId()==R.id.d_me) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    deleteMe(messageModel,senderRoom);
                                }else if(menuItem.getItemId()==R.id.copy) {
                                    menu.getMenu().findItem(R.id.copy).setVisible(false);
                                }
                                return false;
                            }
                        });
                        menu.show();
                        return true;
                    }
                });

                return;
            }
            else if(messageModel.getMessage() !=null && !messageModel.getMessage().isEmpty()) {
                viewHolder.binding.msg.setVisibility(View.VISIBLE);
                viewHolder.binding.msgPic.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
                viewHolder.binding.msg.setText(messageModel.getMessage());

                viewHolder.binding.msg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu menu=new PopupMenu(context,view);
                        menu.getMenuInflater().inflate(R.menu.msg_options,menu.getMenu());
                        String senderRoom=messageModel.getSenderUid()+messageModel.getReceiverUid();
                        String receiverRoom=messageModel.getReceiverUid()+messageModel.getSenderUid();

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId()==R.id.d_all) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    deleteAll(messageModel,senderRoom,receiverRoom);
                                    return true;
                                }else if(menuItem.getItemId()==R.id.d_me) {
//                                    menuItem.setIcon(R.drawable.delete);
                                    deleteMe(messageModel,senderRoom);
                                }else if(menuItem.getItemId()==R.id.copy) {
//                                    menuItem.setIcon(R.drawable.copy);
                                    ClipboardManager clipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboardManager.setText(messageModel.getMessage());
                                    return true;
                                }
                                return false;
                            }
                        });
                        menu.show();
                        return true;
                    }
                });
                return;
            }
            else {
                viewHolder.binding.msgPic.setVisibility(View.GONE);
                viewHolder.binding.msg.setVisibility(View.GONE);
                viewHolder.binding.msgPost.setVisibility(View.GONE);
            }
        }
    }

    private void deleteMe(MessageModel messageModel,String senderRoom) {
        database.getReference().child("chats").child(senderRoom).child("messages").child(messageModel.getMessageId())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyDataSetChanged();
                    }
                });
    }

    private void deleteAll(MessageModel messageModel,String senderRoom,String receiverRoom) {
        database.getReference().child("chats").child(senderRoom).child("messages")
                .child(messageModel.getMessageId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).child("messages")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                                MessageModel model=dataSnapshot.getValue(MessageModel.class);
                                                if(model.getMessageAt()==messageModel.getMessageAt()) {
                                                    database.getReference().child("chats").child(receiverRoom).child("messages")
                                                            .child(dataSnapshot.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    notifyDataSetChanged();
                                                                }
                                                            });
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(FirebaseAuth.getInstance().getUid().equals(list.get(position).getSenderUid())) {
            return send;
        }else {
            return receive;
        }
    }

    public class receiverViewholder extends RecyclerView.ViewHolder {
        ReceiverLayoutBinding binding;
        public receiverViewholder(@NonNull View itemView) {
            super(itemView);
            binding=ReceiverLayoutBinding.bind(itemView);
        }
    }

    public class senderViewholder extends RecyclerView.ViewHolder {
        SenderLayoutBinding binding;
        public senderViewholder(@NonNull View itemView) {
            super(itemView);
            binding=SenderLayoutBinding.bind(itemView);
        }
    }

}

