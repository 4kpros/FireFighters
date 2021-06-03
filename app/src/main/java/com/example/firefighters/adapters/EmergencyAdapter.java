package com.example.firefighters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyAdapter extends  RecyclerView.Adapter<EmergencyHolder> {

    private ArrayList<EmergencyModel> emergencies;
    private Context context;
    private final OnItemClickListener listener;

    public EmergencyAdapter(Context context, ArrayList<EmergencyModel> emergencies, OnItemClickListener listener) {
        this.emergencies = emergencies;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmergencyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_emergency, parent, false);
        return new EmergencyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyHolder holder, int position) {
        holder.bindListener(position, listener);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull EmergencyHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.date.setText(emergencies.get(holder.getAdapterPosition()).getSendDate() + "  " + emergencies.get(holder.getAdapterPosition()).getSendHour());
        holder.status.setText(emergencies.get(holder.getAdapterPosition()).getStatus());
        holder.degree.setText(emergencies.get(holder.getAdapterPosition()).getGravity());
        holder.streetName.setText(emergencies.get(holder.getAdapterPosition()).getStreetName());
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull EmergencyHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }

    public interface OnItemClickListener{
        void onItemMoreClick(int position);
        void onItemStreetClick(int position);
        void onItemCardClick(int position);
    }
}
