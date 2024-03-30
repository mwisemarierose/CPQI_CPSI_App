package com.example.tnsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.R
import com.example.tnsapp.data.Answers


class AddNewListAdapter (val items: List<Answers>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<AddNewListAdapter.ViewHolder>()  {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val auditCwsNameView: TextView = itemView.findViewById(R.id.auditCWSName)
        val auditDateView: TextView = itemView.findViewById(R.id.auditDate)

        override fun onClick(v: View?) {

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.addnew_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.auditCwsNameView.text = currentItem.cwsName
        holder.auditDateView.text = currentItem.date
    }

    override fun getItemCount(): Int {
        return items.size
    }


}