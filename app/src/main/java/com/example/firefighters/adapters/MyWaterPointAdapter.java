package com.example.firefighters.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firefighters.R;
import com.example.firefighters.models.WaterPointModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyWaterPointAdapter extends RecyclerView.Adapter<MyWaterPointAdapter.MyWaterPointHolder> {

    private final ArrayList<WaterPointModel> fireStations;
    private final Context context;

    public MyWaterPointAdapter(Context context, ArrayList<WaterPointModel> fireStations) {
        this.fireStations = fireStations;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyWaterPointAdapter.MyWaterPointHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyWaterPointAdapter.MyWaterPointHolder holder, int position) {
        //
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull MyWaterPointAdapter.MyWaterPointHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.idName.setText("WaterP " + fireStations.get(holder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return fireStations.size();
    }

    public class MyWaterPointHolder extends RecyclerView.ViewHolder{
        TextView idName;

        public MyWaterPointHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            idName = itemView.findViewById(R.id.text_name);
        }
    }
}
