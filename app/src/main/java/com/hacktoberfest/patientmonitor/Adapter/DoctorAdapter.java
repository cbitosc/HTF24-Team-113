package com.hacktoberfest.patientmonitor.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hacktoberfest.patientmonitor.Models.Doctor;
import com.hacktoberfest.patientmonitor.R;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private OnDoctorConnectClickListener connectClickListener;

    public DoctorAdapter(Context context, List<Doctor> doctorList, OnDoctorConnectClickListener listener) {
        this.context = context;
        this.doctorList = doctorList;
        this.connectClickListener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.doctorName.setText(doctor.getName());
        holder.doctorSpecialization.setText(doctor.getSpecialization());

        holder.connectButton.setOnClickListener(v -> connectClickListener.onConnectClick(doctor));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView doctorName, doctorSpecialization;
        Button connectButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.doctor_profile_image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialization = itemView.findViewById(R.id.doctor_specialization);
            connectButton = itemView.findViewById(R.id.connect_button);
        }
    }

    public interface OnDoctorConnectClickListener {
        void onConnectClick(Doctor doctor);
    }
}
