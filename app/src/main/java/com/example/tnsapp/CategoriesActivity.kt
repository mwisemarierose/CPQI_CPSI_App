package com.example.tnsapp


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Categories
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.parsers.readJsonFromAssets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        val auditId = intent.getIntExtra("auditId", 0)

        val jsonString = readJsonFromAssets(this,"data.json")
        val items: List<Categories>? = jsonString?.let { categoryParser(it, auditId) }


        setupUI(items)
        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "Date: $formattedDate"
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
        val intent = Intent(this, PopupActivity::class.java)
        intent.putExtra("catId", "$auditId.$position")
        intent.putExtra("catName", adapter.items[position - 1].name)
        startActivity(intent)
    }
}