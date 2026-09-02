package com.samedtevin.bagcilarapp.adapter.uiadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.samedtevin.bagcilarapp.databinding.ItemLocationSearchBinding

class LocationSearchAdapter(private val onClick: (AutocompletePrediction) -> Unit): RecyclerView.Adapter<LocationSearchAdapter.LocationViewHolder>() {

    private val items = mutableListOf<AutocompletePrediction>()

    fun submitList(newItems: List<AutocompletePrediction>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    inner class LocationViewHolder(private val binding: ItemLocationSearchBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: AutocompletePrediction){

            binding.tvLocationName.text = item.getPrimaryText(null)
            binding.tvLocationAddress.text = item.getSecondaryText(null)
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationSearchAdapter.LocationViewHolder {
        val binding = ItemLocationSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LocationSearchAdapter.LocationViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}