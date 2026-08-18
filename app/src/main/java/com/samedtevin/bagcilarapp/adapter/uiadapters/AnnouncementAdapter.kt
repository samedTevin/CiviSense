package com.samedtevin.bagcilarapp.adapter.uiadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samedtevin.bagcilarapp.databinding.ItemAnnouncementBinding
import com.samedtevin.bagcilarapp.model.Announcement
import java.text.SimpleDateFormat
import java.util.Locale

class AnnouncementAdapter(private val announcements: MutableList<Announcement>): RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    var onItemClick: ((id: String) -> Unit)? = null
    inner class AnnouncementViewHolder(val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(announcement: Announcement){
            binding.apply {
                tvTitle.text = announcement.title
                tvDate.text = announcement.createdAt?.toDate().let { SimpleDateFormat("dd MMM yyyy",
                    Locale.getDefault()).format(it) }
            }
            Glide.with(binding.root).load(announcement.imageUrl).into(binding.ivAnnouncement)

            binding.root.setOnClickListener {
                onItemClick?.invoke(announcement.id)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnnouncementAdapter.AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AnnouncementAdapter.AnnouncementViewHolder,
        position: Int
    ) {
        holder.bind(announcements[position])
    }

    override fun getItemCount(): Int {
        return announcements.size
    }

    fun updateList(newList: List<Announcement>){
        announcements.clear()
        announcements.addAll(newList)
        notifyDataSetChanged()
    }
}