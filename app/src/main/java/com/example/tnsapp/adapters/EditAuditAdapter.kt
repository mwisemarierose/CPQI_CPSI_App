package com.example.tnsapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tnsapp.R
import com.example.tnsapp.data.Categories

class EditAuditAdapter(
    private val items: List<Categories>,
    private val listener: OnItemClickListener,
    applicationContext: Context
) : RecyclerView.Adapter<EditAuditAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val categoryNameView: TextView = itemView.findViewById(R.id.categoryName)
        val categoryIconView: ImageView = itemView.findViewById(R.id.imageView)
        val progressBar: View = itemView.findViewById(R.id.progressBar)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.editaudit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.categoryNameView.text = currentItem.name
        loadImageFromUrl(currentItem.iconPath, holder.categoryIconView)
        holder.progressBar.visibility = View.VISIBLE // Assuming you want to always show the progress bar
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("DiscouragedApi")
    private fun loadImageFromUrl(url: String, iconView: ImageView) {
        val resourceId = iconView.context.resources.getIdentifier(url, "drawable", iconView.context.packageName)
        Glide.with(iconView.context).load(resourceId).into(iconView)
    }
}
