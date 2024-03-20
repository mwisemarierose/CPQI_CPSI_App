package com.example.tnsapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.QuestionAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.Questions
import com.example.tnsapp.parsers.questionParser

class PopupActivity(
    context: Context,
    private val auditId: Int,
    private val audit: String,
    private val catId: Int,
    private val catName: String,
    private val respondent: String,
    private val cwsName: String
) : Dialog(context) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup)
        setupUI()
    }

    private fun setupUI() {
        val closeIcon: ImageView = findViewById(R.id.closeIcon)
        val popupTitle: TextView = findViewById(R.id.popUpTitle)
        val answerDetails: Array<Answers> = arrayOf(Answers(0, respondent, Answers.IGNORE, 0, cwsName))

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val items: List<Questions> = questionParser(audit, auditId, catId)

        popupTitle.text = catName
        adapter = QuestionAdapter(items, answerDetails)

        recyclerView.adapter = adapter

        closeIcon.setOnClickListener {
            dismiss()
        }
    }
}