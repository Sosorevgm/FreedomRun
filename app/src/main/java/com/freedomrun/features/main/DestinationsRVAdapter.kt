package com.freedomrun.features.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freedomrun.R
import com.freedomrun.data.models.DestinationModel

class DestinationsRVAdapter(
    var destinations: List<DestinationModel>,
    private val listener: IListener
) : RecyclerView.Adapter<DestinationsRVAdapter.DestinationHolder>() {

    interface IListener {
        fun onDestinationClicked(destination: DestinationModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationHolder {
        return DestinationHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_destination, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: DestinationHolder, position: Int) {
        holder.bind(destinations[position])
    }

    override fun getItemCount() = destinations.size

    class DestinationHolder(
        itemView: View,
        private val listener: IListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val root: View = itemView.findViewById(R.id.item_destination_root)
        private val tvAddress: TextView = itemView.findViewById(R.id.tv_item_destination_address)

        fun bind(destination: DestinationModel) {
            root.setOnClickListener { listener.onDestinationClicked(destination) }
            tvAddress.text = destination.address
        }
    }
}