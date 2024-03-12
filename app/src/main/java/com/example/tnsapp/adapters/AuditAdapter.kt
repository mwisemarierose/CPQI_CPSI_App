package com.example.tnsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tnsapp.R
import com.example.tnsapp.data.AuditCategories

class AuditAdapter(private val items: List<AuditCategories>) : RecyclerView.Adapter<AuditAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val auditNameView: TextView = itemView.findViewById(R.id.auditName)
        val auditIconView: ImageView = itemView.findViewById(R.id.auditIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audit_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.auditNameView.text = currentItem.name
        // Load icon using Glide or Picasso for better performance
        // For simplicity, we'll assume you have a method to load image from URL
        loadImageFromUrl(currentItem.iconPath, holder.auditIconView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun loadImageFromUrl(url: String, iconView: ImageView) {
        Glide.with(iconView.context)
            .load(url)
            .into(iconView)
    }
}
