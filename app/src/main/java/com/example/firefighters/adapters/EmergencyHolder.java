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

public class EmergencyHolder extends RecyclerView.ViewHolder {

    CardView cardHydrant;
    TextView streetDistance;
    TextView emergencyName;
    TextView degree;
    TextView status;
    TextView date;

    MaterialButton streetViewButton;

    ImageView moreInfo;

    View statusColor;

    RelativeLayout cardClickButton;

    public EmergencyHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        initViews(itemView);
    }

    public void bindListener(final int position, final EmergencyAdapter.OnItemClickListener listener) {
        //Street view button have be clicked ?
        streetViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemStreetClick(position);
            }
        });
        //More info button have be clicked ?
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemMoreClick(position);
            }
        });
        //On card clicked
        cardClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemCardClick(position);
            }
        });
    }

    private void initViews(View view) {
        cardHydrant = itemView.findViewById(R.id.card_hydrant);
        streetDistance = itemView.findViewById(R.id.street_distance);
        emergencyName = itemView.findViewById(R.id.emergency_name);
        degree = itemView.findViewById(R.id.text_fire_degree);
        date = itemView.findViewById(R.id.text_hydrant_date);
        status = itemView.findViewById(R.id.text_hydrant_status);

        streetViewButton = itemView.findViewById(R.id.button_street_view);

        moreInfo = itemView.findViewById(R.id.more_info);

        statusColor = itemView.findViewById(R.id.view_hydrant_status_color);

        cardClickButton = itemView.findViewById(R.id.relative_button_card);
    }
}
