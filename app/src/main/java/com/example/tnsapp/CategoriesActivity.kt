package com.example.tnsapp


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.Categories
import com.example.tnsapp.parsers.categoryParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener, PopupActivity.DialogDismissListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
    private lateinit var respondent:TextView
    private lateinit var cwsName: TextView
    private lateinit var answerDetails: Array<Answers>
    private lateinit var dialog: PopupActivity
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        supportActionBar?.hide()

        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)

        val auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        val items: List<Categories> = categoryParser(audit, auditId)

        println("back here")

        setupUI(items)

        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "Date: $formattedDate"

        toolBarTitle.text = intent.getStringExtra("auditName")

        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupUI(items: List<Categories>?) {
        respondent = findViewById(R.id.nameEditText)
        cwsName = findViewById(R.id.cwsNameEditText)
        answerDetails = arrayOf()

//        add respondent name and cws name to answerDetails when the user enters them
        respondent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if(answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails[0].let {
                        arrayOf(
                            Answers(
                                0,
                                respondent.text.toString(),
                                it.answer,
                                it.qId,
                                cwsName.text.toString()
                            )
                        )
                    }
                }
                else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            0,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
                            cwsName.text.toString()
                        )
                    )
                }
            }
        }

        cwsName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if(answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails[0].let {
                        arrayOf(
                            Answers(
                                0,
                                respondent.text.toString(),
                                it.answer,
                                it.qId,
                                cwsName.text.toString()
                            )
                        )
                    }
                }
                else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            0,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
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

    @Deprecated("Deprecated in Java", ReplaceWith(
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
        dialog = PopupActivity(this, auditId, audit, position, adapter.items[position - 1].name, answerDetails, respondent.text.toString(), cwsName.text.toString())
        dialog.setDismissListener(this)
        dialog.show()

    }
    override fun onDialogDismissed(updatedAnswers: Array<Answers>?) {
        answerDetails = answerDetails.map { answer ->
            updatedAnswers?.find { it.qId == answer.qId } ?: answer
        }.toTypedArray()
    }
}