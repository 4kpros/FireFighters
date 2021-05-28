package com.example.firefighters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.models.HydrantModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HydrantAdapter extends  RecyclerView.Adapter<HydrantHolder> {

    private ArrayList<HydrantModel> hydrantList;
    private Context context;
    private final OnItemClickListener listener;

    public HydrantAdapter(Context context, ArrayList<HydrantModel> hydrantList, OnItemClickListener listener) {
        this.hydrantList = hydrantList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HydrantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_emergency, parent, false);
        return new HydrantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HydrantHolder holder, int position) {
        holder.checkInteractions(position, listener);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull HydrantHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull HydrantHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return hydrantList.size();
    }

    public interface OnItemClickListener{
        void onItemMoreClick(long position);
        void onItemStreetClick(long position);
    }
}
