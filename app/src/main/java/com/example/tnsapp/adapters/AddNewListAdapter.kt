package com.example.tnsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.R
import com.example.tnsapp.data.RecordedAudit


class AddNewListAdapter(
    val items: Map<Pair<String, String?>, Int>,
    private val totalItems: Int,
    private val auditQSize: Int
) :
    RecyclerView.Adapter<AddNewListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val auditCwsNameView: TextView = itemView.findViewById(R.id.auditCWSName)
        val totalAView: TextView = itemView.findViewById(R.id.totalA)
        val auditDateView: TextView = itemView.findViewById(R.id.auditDate)
        val auditScoreView: TextView = itemView.findViewById(R.id.auditScore)

        override fun onClick(v: View?) {}
    }


    // function getDataToExport()

    fun getDataToExport(): Map<Pair<String, String>, Int> {
        val dataToExport = mutableMapOf<Pair<String, String>, Int>()

        for ((key, value) in items) {
            val category = key.first // Get category name
            val date = key.second // Get date

            // Combine category and date into a Pair
            val pairKey = Pair(category, date)

            // Add the score to the map
            dataToExport[pairKey as Pair<String, String>] = value
        }

        return dataToExport
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.addnew_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items.keys.elementAt(position)
        holder.auditCwsNameView.text = currentItem.first
        holder.auditDateView.text = currentItem.second
        holder.totalAView.text = "${items.entries.elementAt(position).value}/ $auditQSize"
        holder.auditScoreView.text = ((items.entries.elementAt(position).value * 100) / auditQSize).toString() + "%"
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 0 else items.size
    }
}