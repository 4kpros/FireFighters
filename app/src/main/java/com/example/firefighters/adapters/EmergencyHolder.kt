package com.example.firefighters.adapters

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.google.android.material.button.MaterialButton

class EmergencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var cardHydrant: CardView? = null
    var streetDistance: TextView? = null
    var emergencyName: TextView? = null
    var degree: TextView? = null
    var status: TextView? = null
    var date: TextView? = null
    var streetViewButton: MaterialButton? = null
    var moreInfo: ImageView? = null
    var statusColor: View? = null
    var cardClickButton: RelativeLayout? = null

    init {
        initViews()
    }

    fun bindListener(position: Int, listener: EmergencyAdapter.OnItemClickListener) {
        //Street view button have be clicked ?
        streetViewButton!!.setOnClickListener { listener.onItemStreetClick(position) }
        //More info button have be clicked ?
        moreInfo!!.setOnClickListener { listener.onItemMoreClick(position) }
        //On card clicked
        cardClickButton!!.setOnClickListener { listener.onItemCardClick(position) }
    }

    private fun initViews() {
        cardHydrant = itemView.findViewById(R.id.card_hydrant)
        streetDistance = itemView.findViewById(R.id.street_distance)
        emergencyName = itemView.findViewById(R.id.emergency_name)
        degree = itemView.findViewById(R.id.text_fire_degree)
        date = itemView.findViewById(R.id.text_hydrant_date)
        status = itemView.findViewById(R.id.text_hydrant_status)
        streetViewButton = itemView.findViewById(R.id.button_street_view)
        moreInfo = itemView.findViewById(R.id.more_info)
        statusColor = itemView.findViewById(R.id.view_hydrant_status_color)
        cardClickButton = itemView.findViewById(R.id.relative_button_card)
    }
}
