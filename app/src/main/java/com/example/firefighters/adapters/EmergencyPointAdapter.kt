package com.example.firefighters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.models.EmergencyModel

class EmergencyPointAdapter(var context: Context, var emergencies: ArrayList<EmergencyModel>) :
    RecyclerView.Adapter<EmergencyPointHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyPointHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.card_point_emergency, parent, false)
        return EmergencyPointHolder(view)
    }

    override fun onBindViewHolder(holder: EmergencyPointHolder, position: Int) {
        holder.textName!!.text = "EM ${emergencies[holder.adapterPosition].id}"
    }

    override fun getItemCount(): Int {
        return emergencies.size
    }
}
