package com.example.tnsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Categories
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.parsers.readJsonFromAssets
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.QuestionAdapter
import com.example.tnsapp.data.Questions
import com.example.tnsapp.parsers.questionParser

class PopupActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)
        setupUI()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val extraId = intent.getStringExtra("catId")
//        split extraId with .
        val splittedExtraId = extraId?.split(".")

        val jsonString = readJsonFromAssets(this,"data.json")
        val items: List<Questions>? = jsonString?.let { splittedExtraId?.get(0)
            ?.let { it1 -> questionParser(it, it1.toInt(), splittedExtraId[1].toInt()) } }

        adapter = items?.let { QuestionAdapter(it) }!!
        recyclerView.adapter = adapter
    }
}