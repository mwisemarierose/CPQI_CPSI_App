package com.example.tnsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.R
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.Questions

class QuestionAdapter(
    private val auditId: Int,
    private val items: List<Questions>,
    var answerDetails: Array<Answers>,
    private val respondent: String,
    private val cwsName: String,
    private val editMode: Boolean,
    private val existingAnswers: List<Answers>
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(currentItem: Questions) {
            yesBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if (item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size) {
                            if (answerDetails[index].qId == currentItem.id || answerDetails.size == 1) {
                                answerDetails[index] = Answers(
                                    null,
                                    respondent,
                                    Answers.YES,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName

                                )
                            } else {
                                answerDetails = answerDetails.plus(
                                    Answers(
                                        null,
                                        respondent,
                                        Answers.YES,
                                        currentItem.id,
                                        auditId.toLong(),
                                        cwsName
                                    )
                                )
                            }
                        } else {
                            answerDetails = answerDetails.plus(
                                Answers(
                                    null,
                                    respondent,
                                    Answers.YES,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName
                                )
                            )
                        }
                    }
                }


                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.green))
                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                noBtn.setBackgroundResource(R.drawable.border_maroon)
                skipBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                skipBtn.setBackgroundResource(R.drawable.border_grey)
            }

            noBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if (item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size) {
                            if (answerDetails[index].qId == currentItem.id || answerDetails.size == 1) {
                                answerDetails[index] = Answers(
                                    null,
                                    respondent,
                                    Answers.NO,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName
                                )
                            } else {
                                answerDetails = answerDetails.plus(
                                    Answers(
                                        null,
                                        respondent,
                                        Answers.NO,
                                        currentItem.id,
                                        auditId.toLong(),
                                        cwsName
                                    )
                                )
                            }
                        } else {
                            answerDetails = answerDetails.plus(
                                Answers(
                                    null,
                                    respondent,
                                    Answers.NO,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName
                                )
                            )
                        }
                    }
                }

                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.maroon))
                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                yesBtn.setBackgroundResource(R.drawable.border_green)
                skipBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                skipBtn.setBackgroundResource(R.drawable.border_grey)
            }

            skipBtn.setOnClickListener {
                items.forEachIndexed { index, item ->
                    if (item.id == currentItem.id) {
                        if (answerDetails.isNotEmpty() && index < answerDetails.size) {
                            if (answerDetails[index].qId == currentItem.id || answerDetails.size == 1) {
                                answerDetails[index] = Answers(
                                    null,
                                    respondent,
                                    Answers.SKIP,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName
                                )
                            } else {
                                answerDetails = answerDetails.plus(
                                    Answers(
                                        null,
                                        respondent,
                                        Answers.SKIP,
                                        currentItem.id,
                                        auditId.toLong(),
                                        cwsName
                                    )
                                )
                            }
                        } else {
                            answerDetails = answerDetails.plus(
                                Answers(
                                    null,
                                    respondent,
                                    Answers.SKIP,
                                    currentItem.id,
                                    auditId.toLong(),
                                    cwsName
                                )
                            )
                        }
                    }
                }

                skipBtn.setBackgroundColor(itemView.resources.getColor(R.color.grey))
                yesBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                yesBtn.setBackgroundResource(R.drawable.border_green)
                noBtn.setBackgroundColor(itemView.resources.getColor(R.color.transparent))
                noBtn.setBackgroundResource(R.drawable.border_maroon)
            }
        }

        val questionNumberView: TextView = itemView.findViewById(R.id.popUpTextNumbering)
        val questionNameView: TextView = itemView.findViewById(R.id.popUpText)
        val yesBtn: Button = itemView.findViewById(R.id.yesButton)
        val noBtn: Button = itemView.findViewById(R.id.noButton)
        val skipBtn: Button = itemView.findViewById(R.id.skipButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.popup_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.questionNumberView.text = (position + 1).toString()
        holder.questionNameView.text = currentItem.qName

        // Find the answer for this question from answersFromSP
        val answer = existingAnswers.find { it.qId == currentItem.id }

        // Set background color based on the answer (optional, selection highlights remain)
        when (answer?.answer) {
            Answers.YES -> {
                holder.yesBtn.setBackgroundColor(holder.itemView.resources.getColor(R.color.green))
            }

            Answers.NO -> {
                holder.noBtn.setBackgroundColor(holder.itemView.resources.getColor(R.color.maroon))
            }

            Answers.SKIP -> {
                holder.skipBtn.setBackgroundColor(holder.itemView.resources.getColor(R.color.grey))
            }
        }

        holder.bind(currentItem)
    }
}