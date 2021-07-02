package com.example.firefighters.adapters;

import android.view.View;
import android.widget.TextView;

import com.example.firefighters.R;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WaterPointHolder extends RecyclerView.ViewHolder  {

    TextView textName;

    public WaterPointHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        initViews();
    }


    private void initViews() {
        textName = itemView.findViewById(R.id.text_name);
    }
}
