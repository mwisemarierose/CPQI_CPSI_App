package com.example.tnsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tnsapp.R
import com.example.tnsapp.data.Categories

class CategoryAdapter(val items: List<Categories>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val categoryNameView: TextView = itemView.findViewById(R.id.categoryName)
        val categoryIconView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = items[adapterPosition].id.toInt()
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_list, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.categoryNameView.text = currentItem.name
        // Load icon using Glide or Picasso for better performance
        // For simplicity, we'll assume you have a method to load image from URL
        loadImageFromUrl(currentItem.iconPath, holder.categoryIconView)
    }
    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("DiscouragedApi")
    private fun loadImageFromUrl(url: String, iconView: ImageView) {
        val resourceId = iconView.context.resources.getIdentifier(url, "drawable", iconView.context.packageName)
        Glide.with(iconView.context)
            .load(resourceId)
            .into(iconView)
    }
}