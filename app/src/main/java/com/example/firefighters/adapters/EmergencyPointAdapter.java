package com.example.firefighters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyPointAdapter extends RecyclerView.Adapter<EmergencyPointHolder> {

    Context context;
    ArrayList<EmergencyModel> emergencies;

    public EmergencyPointAdapter(Context context, ArrayList<EmergencyModel> emergencies) {
        this.context = context;
        this.emergencies = emergencies;
    }

    @NonNull
    @NotNull
    @Override
    public EmergencyPointHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_point_emergency, parent, false);
        return new EmergencyPointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EmergencyPointHolder holder, int position) {
        holder.textName.setText("EM"+emergencies.get(holder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }
}
