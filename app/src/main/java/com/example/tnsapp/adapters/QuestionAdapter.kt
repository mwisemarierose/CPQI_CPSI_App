package com.example.tnsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.R
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.Questions

class QuestionAdapter(private val items: List<Questions>, private val answerDetails: Array<Answers>) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(currentItem: Questions) {
            yesBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if (index < answerDetails.size) {
                        answerDetails[index] = Answers(0, answerDetails[index].responderName, Answers.YES, item.id, answerDetails[index].cwsName)
                    }
                }
            }


            noBtn.setOnClickListener {
                for (i in items.indices) {
                    answerDetails[i] = Answers(0, answerDetails[i].responderName, Answers.NO, items[i].id, answerDetails[i].cwsName)
                }
            }

            ignoreBtn.setOnClickListener {
                for (i in items.indices) {
                    answerDetails[i] = Answers(0, answerDetails[i].responderName, Answers.IGNORE, items[i].id, answerDetails[i].cwsName)
                }
            }
        }

        val questionNumberView : TextView = itemView.findViewById(R.id.popUpTextNumbering)
        val questionNameView: TextView = itemView.findViewById(R.id.popUpText)
        val yesBtn: Button = itemView.findViewById(R.id.yesButton)
        val noBtn: Button = itemView.findViewById(R.id.noButton)
        val ignoreBtn: Button = itemView.findViewById(R.id.ignoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.popup_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.questionNumberView.text = position.plus(1).toString()
        holder.questionNameView.text = currentItem.qName
        holder.bind(currentItem)
    }
}