package com.cs407.map_application.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.map_application.R
import com.cs407.map_application.model.PlanSegment

class PlanAdapter(private val segments: List<PlanSegment>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startLocation: TextView = itemView.findViewById(R.id.startLocation)
        val duration: TextView = itemView.findViewById(R.id.duration)
        val transportMode: ImageView = itemView.findViewById(R.id.transportMode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val segment = segments[position]
        holder.startLocation.text = segment.startLocation
        holder.duration.text = segment.duration
        holder.transportMode.setImageResource(
            when (segment.transportMode) {
                "Walking" -> R.drawable.ic_walk
                "Car" -> R.drawable.ic_car
                "Bus" -> R.drawable.ic_bus
                else -> R.drawable.ic_default_transport
            }
        )
    }

    override fun getItemCount(): Int = segments.size
}