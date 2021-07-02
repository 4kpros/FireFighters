package com.example.firefighters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.models.WaterPointModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WaterPointAdapter extends RecyclerView.Adapter<WaterPointHolder> {

    Context context;
    ArrayList<WaterPointModel> waterPoints;

    public WaterPointAdapter(Context context, ArrayList<WaterPointModel> waterPoints) {
        this.context = context;
        this.waterPoints = waterPoints;
    }

    @NonNull
    @NotNull
    @Override
    public WaterPointHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_point_water, parent, false);
        return new WaterPointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WaterPointHolder holder, int position) {
        holder.textName.setText("WP"+waterPoints.get(holder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return waterPoints.size();
    }
}
