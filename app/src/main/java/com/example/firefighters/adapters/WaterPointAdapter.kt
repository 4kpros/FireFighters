package com.example.firefighters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.models.WaterPointModel

class WaterPointAdapter(var context: Context, var waterPoints: ArrayList<WaterPointModel>) :
    RecyclerView.Adapter<WaterPointHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterPointHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_point_water, parent, false)
        return WaterPointHolder(view)
    }

    override fun onBindViewHolder(holder: WaterPointHolder, position: Int) {
        holder.textName!!.text = "WP ${waterPoints[holder.adapterPosition].id}"
    }

    override fun getItemCount(): Int {
        return waterPoints.size
    }
}
