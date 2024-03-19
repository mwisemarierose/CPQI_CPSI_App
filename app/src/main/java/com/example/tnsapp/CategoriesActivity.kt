package com.example.tnsapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Categories
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.parsers.readJsonFromAssets
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
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
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = items?.let { CategoryAdapter(it, this) }!!
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        startActivityAfterClick(position)
    }

    private fun startActivityAfterClick(position: Int) {
        val auditId = intent.getIntExtra("auditId", 0)
        val dialog = PopupActivity(this, auditId, audit, position, adapter.items[position - 1].name)
        dialog.show()
    }
}