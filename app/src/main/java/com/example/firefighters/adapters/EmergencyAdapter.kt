package com.example.firefighters.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.firefighters.R
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.utils.ConstantsValues

class EmergencyAdapter(
    private val context: Context,
    private val emergencies: ArrayList<EmergencyModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EmergencyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_emergency, parent, false)
        return EmergencyHolder(view)
    }

    override fun onBindViewHolder(holder: EmergencyHolder, position: Int) {
        holder.bindListener(position, listener)
    }

    override fun onViewAttachedToWindow(holder: EmergencyHolder) {
        super.onViewAttachedToWindow(holder)
        when (emergencies[holder.adapterPosition].status) {
            ConstantsValues.WORKING -> {
                holder.statusColor?.background =
                    AppCompatResources.getDrawable(context, R.drawable.circle_shape_hydrant_working)
            }
            ConstantsValues.FINISHED -> {
                holder.statusColor?.background =
                    AppCompatResources.getDrawable(context, R.drawable.circle_shape_hydrant_finished)
            }
            else -> {
                holder.statusColor?.background =
                    AppCompatResources.getDrawable(context, R.drawable.circle_shape_hydrant_not_working)
            }
        }
        holder.emergencyName?.text = "EM ${emergencies[holder.adapterPosition].id}"
        holder.date?.text = "${emergencies[holder.adapterPosition].sendDate} ${emergencies[holder.adapterPosition].sendHour}"
        holder.status?.text = emergencies[holder.adapterPosition].status
        holder.degree?.text = emergencies[holder.adapterPosition].gravity
        //holder.emergencyName.setText(emergencies.get(holder.getAdapterPosition()).getStreetName());
    }

    override fun getItemCount(): Int {
        return emergencies.size
    }

    interface OnItemClickListener {
        fun onItemMoreClick(position: Int)
        fun onItemStreetClick(position: Int)
        fun onItemCardClick(position: Int)
    }
}
