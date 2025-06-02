package com.example.ballrsrv.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ballrsrv.data.Court
import com.example.ballrsrv.databinding.ItemCourtBinding
import com.squareup.picasso.Picasso

class CourtsAdapter(
    private val courts: List<Court>,
    private val onCourtClick: (Court) -> Unit
) : RecyclerView.Adapter<CourtsAdapter.CourtViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val binding = ItemCourtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        holder.bind(courts[position])
    }

    override fun getItemCount(): Int = courts.size

    inner class CourtViewHolder(
        private val binding: ItemCourtBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(court: Court) {
            binding.apply {
                courtName.text = court.name
                courtDescription.text = court.description
                courtPrice.text = "₱${court.price}"
                
                court.imageUrl?.let { url ->
                    Picasso.get().load(url).into(courtImage)
                }

                root.setOnClickListener { onCourtClick(court) }
            }
        }
    }
} 