package com.example.tnsapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.EditAuditAdapter
import com.example.tnsapp.data.Categories

class EditauditActivity : AppCompatActivity(),EditAuditAdapter.OnItemClickListener {


    private lateinit var items: List<Categories>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.editaudit)
        setupUI(items)
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setupUI(items: List<Categories>?) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = items?.let { EditAuditAdapter(it, this, applicationContext) }!!
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        // Handle item click here
        val clickedItem = items[position]
        // Do something with the clicked item
    }
}