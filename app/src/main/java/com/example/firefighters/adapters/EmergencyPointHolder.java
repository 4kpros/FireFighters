package com.example.firefighters.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.firefighters.R;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyPointHolder extends RecyclerView.ViewHolder {

    TextView textName;

    public EmergencyPointHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        initViews();
    }


    private void initViews() {
        textName = itemView.findViewById(R.id.text_name);
    }
}
