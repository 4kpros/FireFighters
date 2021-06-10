package com.example.firefighters.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.FireStationModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyFireStationAdapter extends RecyclerView.Adapter<MyFireStationAdapter.MyFireStationHolder>{

    private final ArrayList<FireStationModel> fireStations;
    private final Context context;

    public MyFireStationAdapter(Context context, ArrayList<FireStationModel> fireStations) {
        this.fireStations = fireStations;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyFireStationAdapter.MyFireStationHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyFireStationAdapter.MyFireStationHolder holder, int position) {
        //
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull MyFireStationAdapter.MyFireStationHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.idName.setText("Emergency " + fireStations.get(holder.getAdapterPosition()).getId());
    }

    @Override
    public int getItemCount() {
        return fireStations.size();
    }

    public class MyFireStationHolder extends RecyclerView.ViewHolder{
        TextView idName;

        public MyFireStationHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            initViews(itemView);
        }

        private void initViews(View itemView) {
            idName = itemView.findViewById(R.id.text_name);
        }
    }
}
