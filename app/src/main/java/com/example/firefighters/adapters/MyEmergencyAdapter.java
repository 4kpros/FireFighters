package com.example.firefighters.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyEmergencyAdapter extends RecyclerView.Adapter<MyEmergencyAdapter.MyEmergencyHolder> {

    private final ArrayList<EmergencyModel> emergencies;
    private final Context context;

    public MyEmergencyAdapter(Context context, ArrayList<EmergencyModel> emergencies) {
        this.emergencies = emergencies;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyEmergencyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyEmergencyHolder holder, int position) {
        //
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull MyEmergencyHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.idName.setText("Emergency " + emergencies.get(holder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return emergencies.size();
    }

    public class MyEmergencyHolder extends RecyclerView.ViewHolder{
        TextView idName;

        public MyEmergencyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            idName = itemView.findViewById(R.id.text_name);
        }
    }
}
