package com.example.appointmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCancelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.appointmentDate.setText("Date: " + appointment.getDate());
        holder.appointmentTime.setText("Time: " + appointment.getTime());
        holder.appointmentDescription.setText("Description: " + appointment.getDescription());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentDate, appointmentTime, appointmentDescription;
        Button cancelButton;

        public AppointmentViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            appointmentDate = itemView.findViewById(R.id.appointment_date);
            appointmentTime = itemView.findViewById(R.id.appointment_time);
            appointmentDescription = itemView.findViewById(R.id.appointment_description);
            cancelButton = itemView.findViewById(R.id.cancel_button);

            cancelButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCancelClick(position);
                    }
                }
            });
        }
    }
}
