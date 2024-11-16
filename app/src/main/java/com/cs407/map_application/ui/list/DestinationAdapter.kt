package com.cs407.map_application.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs407.map_application.databinding.ItemDestinationBinding
import com.cs407.map_application.model.Destination

class DestinationAdapter :
    RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    private var destinations: List<Destination> = emptyList()
    private var onItemClickListener: ((Destination) -> Unit)? = null
    private var onDeleteClickListener: ((Destination) -> Unit)? = null

    inner class DestinationViewHolder(
        private val binding: ItemDestinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: Destination) {
            binding.apply {
                tvDestinationName.text = destination.name
                tvDestinationAddress.text = destination.address
                tvSequence.text = (destination.sequence + 1).toString()

                btnDelete.setOnClickListener {
                    onDeleteClickListener?.invoke(destination)
                }

                root.setOnClickListener {
                    onItemClickListener?.invoke(destination)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemDestinationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(destinations[position])
    }

    override fun getItemCount() = destinations.size

    fun setDestinations(newDestinations: List<Destination>) {
        destinations = newDestinations
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Destination) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Destination) -> Unit) {
        onDeleteClickListener = listener
    }
}