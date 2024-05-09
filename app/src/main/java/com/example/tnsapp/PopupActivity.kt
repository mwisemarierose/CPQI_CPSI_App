package com.example.tnsapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.QuestionAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.Categories
import com.example.tnsapp.data.Questions
import com.example.tnsapp.parsers.questionParser
class PopupActivity(
    context: Context,
    private val auditId: Int,
    private val audit: String,
    private val catId: Int,
    private val catName: String,
    private var answerDetails: Array<Answers>,
    private val respondent: String,
    private val cwsName: String,
    private val editMode: Boolean,
    private val viewMode: Boolean,
) : Dialog(context) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionAdapter
    private var dismissListener: DialogDismissListener? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var json: String = ""
    private val items: List<Categories> = emptyList()
    private fun updateCategoryCompletion() {
        val currentCategory = items.find { it.id.toInt() == catId }
        if (currentCategory != null) {
            currentCategory.completed = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup)
        setupUI()
    }

    interface DialogDismissListener {
        fun onDialogDismissed(updatedAnswers: Array<Answers>? = null, categoryId: Int)
    }

    fun setDismissListener(listener: DialogDismissListener) {
        dismissListener = listener
    }

    // Call this method when the dialog is dismissed
    @SuppressLint("ResourceType")
    private fun notifyDismissListener(answerDetails: Array<Answers>) {
        // Notify the dismiss listener
        dismissListener?.onDialogDismissed(answerDetails, catId)
        Toast.makeText(context, "Answers saved", Toast.LENGTH_SHORT).show()
    }

    private fun setupUI() {
        val closeIcon: ImageView = findViewById(R.id.closeIcon)
        val popupTitle: TextView = findViewById(R.id.popUpTitle)
        val saveBtn: Button = findViewById(R.id.saveButton)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        sharedPreferences = context.getSharedPreferences("AnswersPref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

//        get answers from shared preferences
        json = sharedPreferences.getString("answers", null).toString()
        val items: List<Questions> = questionParser(audit, auditId, catId)

        popupTitle.text = catName
        adapter = QuestionAdapter(
            auditId,
            items,
            answerDetails,
            respondent,
            cwsName,
            editMode,
            viewMode
        )

        recyclerView.adapter = adapter

        saveBtn.setOnClickListener {
            val allAnswered =
                adapter.answerDetails.size >= items.size && adapter.answerDetails.all { it.qId != 0L }

            if (allAnswered) {
                notifyDismissListener(adapter.answerDetails)
                dismiss()
                updateCategoryCompletion()
            } else {
                Toast.makeText(context, "Please answer all questions", Toast.LENGTH_SHORT).show()
            }
        }
        if (viewMode) saveBtn.visibility = View.GONE else saveBtn.visibility = View.VISIBLE

        closeIcon.setOnClickListener {
            dismiss()
        }
    }
}