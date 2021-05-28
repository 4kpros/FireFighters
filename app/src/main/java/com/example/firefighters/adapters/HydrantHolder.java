package com.example.firefighters.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.firefighters.R;
import com.example.firefighters.models.HydrantModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HydrantHolder extends RecyclerView.ViewHolder {

    CardView cardHydrant;
    TextView streetDistance;
    TextView streetName;
    TextView degree;
    TextView status;
    TextView date;

    MaterialButton streetViewButton;

    ImageView moreInfo;

    View statusColor;

    RelativeLayout cardClickButton;

    public HydrantHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        initViews(itemView);
    }

    public void checkInteractions(final long position, final HydrantAdapter.OnItemClickListener listener) {
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
    }

    private void initViews(View view) {
        cardHydrant = itemView.findViewById(R.id.card_hydrant);
        streetDistance = itemView.findViewById(R.id.street_distance);
        streetName = itemView.findViewById(R.id.street_name);
        degree = itemView.findViewById(R.id.text_fire_degree);
        date = itemView.findViewById(R.id.text_hydrant_date);
        status = itemView.findViewById(R.id.text_hydrant_status);

        streetViewButton = itemView.findViewById(R.id.button_street_view);

        moreInfo = itemView.findViewById(R.id.more_info);

        statusColor = itemView.findViewById(R.id.view_hydrant_status_color);

        cardClickButton = itemView.findViewById(R.id.relative_button_card);
    }
}
