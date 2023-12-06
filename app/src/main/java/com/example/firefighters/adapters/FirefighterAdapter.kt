package com.example.firefighters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.models.UserModel

class FirefighterAdapter(var context: Context, var fireFighter: ArrayList<UserModel>) :
    RecyclerView.Adapter<FirefighterHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirefighterHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.card_point_firefighter, parent, false)
        return FirefighterHolder(view)
    }

    override fun onBindViewHolder(holder: FirefighterHolder, position: Int) {
        holder.textName!!.text = fireFighter[holder.adapterPosition].mail
    }

    override fun getItemCount(): Int {
        return fireFighter.size
    }
}
