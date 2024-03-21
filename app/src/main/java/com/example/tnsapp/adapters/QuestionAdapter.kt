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

class QuestionAdapter(private val items: List<Questions>, private var answerDetails: Array<Answers>) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(currentItem: Questions) {
            yesBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if(item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size && answerDetails[index].qId == currentItem.id) {
                            answerDetails[index] = Answers(index.toLong(), answerDetails[index].responderName, Answers.YES, item.id, answerDetails[index].cwsName)
                        }
                        else {
                            answerDetails = answerDetails.plus(Answers(index.toLong(), "", Answers.YES, item.id, ""))
                        }
                    }
                }

                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.green))
                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                noBtn.setBackgroundResource(R.drawable.border_maroon)
                ignoreBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                ignoreBtn.setBackgroundResource(R.drawable.border_grey)
            }

            noBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if(item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size && answerDetails[index].qId == currentItem.id) {
                            answerDetails[index] = Answers(index.toLong(), answerDetails[index].responderName, Answers.NO, item.id, answerDetails[index].cwsName)
                        }
                        else {
                            answerDetails = answerDetails.plus(Answers(index.toLong(), "", Answers.YES, item.id, ""))
                        }
                    }
                }

                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.maroon))
                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                yesBtn.setBackgroundResource(R.drawable.border_green)
                ignoreBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                ignoreBtn.setBackgroundResource(R.drawable.border_grey)
            }

            ignoreBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if(item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size && answerDetails[index].qId == currentItem.id) {
                            answerDetails[index] = Answers(index.toLong(), answerDetails[index].responderName, Answers.IGNORE, item.id, answerDetails[index].cwsName)
                        }
                        else {
                            answerDetails = answerDetails.plus(Answers(index.toLong(), "", Answers.YES, item.id, ""))
                        }
                    }
                }

                ignoreBtn.setBackgroundColor(itemView.resources.getColor(R.color.grey))
                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                yesBtn.setBackgroundResource(R.drawable.border_green)
                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                noBtn.setBackgroundResource(R.drawable.border_maroon)
            }
        }

        val questionNumberView : TextView = itemView.findViewById(R.id.popUpTextNumbering)
        val questionNameView: TextView = itemView.findViewById(R.id.popUpText)
        private val yesBtn: Button = itemView.findViewById(R.id.yesButton)
        private val noBtn: Button = itemView.findViewById(R.id.noButton)
        private val ignoreBtn: Button = itemView.findViewById(R.id.ignoreButton)
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