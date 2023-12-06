package com.example.firefighters.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R

class EmergencyPointHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textName: TextView? = null

    init {
        initViews()
    }

    private fun initViews() {
        textName = itemView.findViewById(R.id.text_name)
    }
}
