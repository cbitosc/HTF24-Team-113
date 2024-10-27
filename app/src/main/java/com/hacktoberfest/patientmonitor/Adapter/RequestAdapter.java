package com.hacktoberfest.patientmonitor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hacktoberfest.patientmonitor.Models.RequestModel;
import com.hacktoberfest.patientmonitor.R;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private Context context;
    private ArrayList<RequestModel> requestList;

    public RequestAdapter(Context context, ArrayList<RequestModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestModel request = requestList.get(position);
        holder.patientName.setText(request.getPatientName());
        holder.requestStatus.setText(request.getStatus());

        String doctorId = FirebaseAuth.getInstance().getUid();
        holder.buttonAccept.setOnClickListener(v -> {
            String requestId = request.getPatientId();
            updateRequestStatus(requestId, doctorId, "Accepted");
        });

        holder.buttonReject.setOnClickListener(v -> {
            String requestId = request.getPatientId();
            updateRequestStatus(requestId, doctorId, "Rejected");
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView patientProfile;
        TextView patientName, requestStatus;
        Button buttonAccept, buttonReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            patientProfile = itemView.findViewById(R.id.patientProfile);
            patientName = itemView.findViewById(R.id.patientName);
            requestStatus = itemView.findViewById(R.id.requestStatus);
            buttonAccept = itemView.findViewById(R.id.buttonAccept);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }

    private void updateRequestStatus(String requestId, String doctorId, String status) {
        DatabaseReference requestRef1 = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(doctorId)
                .child("requests")
                .child(requestId);

        requestRef1.child("status").setValue(status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Request " + status.toLowerCase() + " successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update request status.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference requestRef2 = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(requestId)
                .child("requests")
                .child(doctorId);

        requestRef2.child("status").setValue(status).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(context, "Request " + status.toLowerCase() + " successfully.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "Failed to update request status.", Toast.LENGTH_SHORT).show();
//            }
        });
    }

}
