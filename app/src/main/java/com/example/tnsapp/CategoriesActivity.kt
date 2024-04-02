package com.example.tnsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Categories
import com.example.tnsapp.parsers.categoryParser
import com.google.gson.Gson
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.util.Date
import android.os.Handler
import java.util.Locale
import kotlin.properties.Delegates

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener,
    PopupActivity.DialogDismissListener, View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
    private var auditId by Delegates.notNull<Int>()
    private lateinit var respondent: TextView
    private lateinit var cwsName: TextView
    private lateinit var submitAll: Button
    private lateinit var dialog: PopupActivity
    private var answerDetails: Array<Answers> = emptyArray()
    private var progress = 0
    private var scoreText = ""
    private var percentage = ""

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private var json: String = ""

    //    initialize room db
    private lateinit var db: AppDatabase
    private fun successMessage() {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("SUCCESS!")
            .setContentText("Audit submitted successfully!").show()

        Handler().postDelayed({
            dialog.dismiss()
        }, 100000)
    }
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportActionBar?.hide()

        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        submitAll = findViewById(R.id.submitAllBtn)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)
        auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        val items: List<Categories> = categoryParser(audit, auditId)
        val progressBar: CircularProgressBar = findViewById(R.id.scoreProgressBar)
        val scoreText: TextView = findViewById(R.id.scoreText)
        val percentageText: TextView = findViewById(R.id.percentageText)
        val score = 80

        progressBar.setProgressWithAnimation(score.toFloat()) // Progress out of 100

        scoreText.text = applicationContext.getString(R.string.score)

        // Update percentage text
        percentageText.text = "$score%"

        sharedPreferences = getSharedPreferences("AnswersPref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setupUI(items)

        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "${applicationContext.getString(R.string.date_lbl)}: $formattedDate"
        toolBarTitle.text = intent.getStringExtra("auditName")

        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        submitAll.isEnabled = false
        submitAll.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))

        db = AppDatabase.getDatabase(this)!!

        submitAll.setOnClickListener {
            val answers = gson.fromJson(
                sharedPreferences.getString("answers", json),
                Array<Answers>::class.java
            )
            Thread {
                db.answerDao().insertAll(answers)
            }.start()

//            remove shared preferences after submitting answers
            editor.remove("answers")
            editor.apply()
            successMessage()
            val intent = Intent(this, AddNewActivity::class.java)
            startActivity(intent)

        }
    }

    private fun setupUI(items: List<Categories>?) {
        respondent = findViewById(R.id.nameEditText)
        cwsName = findViewById(R.id.cwsNameEditText)

//        add respondent name and cws name to answerDetails when the user enters them
        respondent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails.map {
                        Answers(
                            0,
                            respondent.text.toString(),
                            it.answer,
                            it.qId,
                            auditId.toLong(),
                            it.cwsName
                        )
                    }.toTypedArray()
                } else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            0,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
                            auditId.toLong(),
                            cwsName.text.toString()
                        )
                    )
                }
            }
        }

        cwsName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails.map {
                        Answers(
                            0,
                            it.responderName,
                            it.answer,
                            it.qId,
                            auditId.toLong(),
                            cwsName.text.toString()
                        )
                    }.toTypedArray()
                } else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            0,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
                            auditId.toLong(),
                            cwsName.text.toString()
                        )
                    )
                }
            }
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = items?.let { CategoryAdapter(it, this) }!!
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClick(position: Int) {
        startActivityAfterClick(position)
    }

    private fun startActivityAfterClick(position: Int) {
        val auditId = intent.getIntExtra("auditId", 0)
        dialog = PopupActivity(
            this,
            auditId,
            audit,
            position,
            adapter.items[position - 1].name,
            answerDetails,
            respondent.text.toString(),
            cwsName.text.toString()
        )
        dialog.setDismissListener(this)
        dialog.show()
    }

    override fun onDialogDismissed(updatedAnswers: Array<Answers>?) {
        updatedAnswers?.forEach { updatedAnswer ->
            val existingAnswer = answerDetails.find { it.qId == updatedAnswer.qId }
            if (existingAnswer != null) {
                // Update existing answer in answerDetails
                val index = answerDetails.indexOf(existingAnswer)
                answerDetails[index] = updatedAnswer
            } else {
                // Add new answer to answerDetails
                answerDetails = answerDetails.plus(updatedAnswer)
            }
        }

        if (answerDetails.isNotEmpty()) {
            submitAll.isEnabled = true
            submitAll.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))
        } else {
            submitAll.isEnabled = false
            submitAll.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))
        }

//        add shared preferences to save answers and print them out

        json = gson.toJson(answerDetails)
        println(json)
        editor.putString("answers", json)
        editor.apply()
    }


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}