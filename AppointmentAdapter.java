package com.example.cattlehealth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvName.setText(appointment.getName());
        holder.tvDate.setText(appointment.getDate());
        holder.tvLocation.setText(appointment.getLocation());
        holder.tvAnimalKind.setText(appointment.getAnimalKind());
        holder.tvAge.setText(appointment.getAge());
        holder.tvContactInfo.setText(appointment.getContactInfo());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvLocation, tvAnimalKind, tvAge, tvContactInfo;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAnimalKind = itemView.findViewById(R.id.tvAnimalKind);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvContactInfo = itemView.findViewById(R.id.tvContactInfo);
        }
    }
}
