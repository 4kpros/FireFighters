package com.example.firefighters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firefighters.R;
import com.example.firefighters.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirefighterAdapter extends RecyclerView.Adapter<FirefighterHolder> {

    Context context;
    ArrayList<UserModel> fireFighter;

    public FirefighterAdapter(Context context, ArrayList<UserModel> fireFighter) {
        this.context = context;
        this.fireFighter = fireFighter;
    }

    @NonNull
    @NotNull
    @Override
    public FirefighterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_point_firefighter, parent, false);
        return new FirefighterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FirefighterHolder holder, int position) {
        holder.textName.setText(fireFighter.get(holder.getAdapterPosition()).getMail());
    }

    @Override
    public int getItemCount() {
        return fireFighter.size();
    }
}
