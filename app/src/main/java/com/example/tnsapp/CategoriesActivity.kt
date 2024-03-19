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


class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
    private lateinit var respondent:TextView
    private lateinit var cwsName: TextView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

//        remove action bar
        supportActionBar?.hide()

        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)

        val auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        val items: List<Categories> = categoryParser(audit, auditId)

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
        val answerDetails = Answers(0, "responderName", Answers.IGNORE, 0, "cwsName")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = items?.let { CategoryAdapter(it, this) }!!
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val responderName = data?.getStringExtra("responderName")
            val cwsName = data?.getStringExtra("cwsName")
            val answer = data?.getStringExtra("answer")
            val qId = data?.getIntExtra("qId", 0)

            val answerDetails = Answers(0, responderName.toString(), answer.toString(), qId!!.toLong(), cwsName.toString())
        }
    }

    override fun onItemClick(position: Int) {
        startActivityAfterClick(position)
    }

    private fun startActivityAfterClick(position: Int) {
        val auditId = intent.getIntExtra("auditId", 0)
        val dialog = PopupActivity(this, auditId, audit, position, adapter.items[position - 1].name, respondent.text.toString(), cwsName.text.toString())
        dialog.show()
    }
}