package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AuditAdapter
import com.example.tnsapp.data.AuditCategories
import com.example.tnsapp.parsers.auditParser
import com.example.tnsapp.parsers.readJsonFromAssets

class AuditActivity : AppCompatActivity(), AuditAdapter.OnItemClickListener {
    private var lastClickedButton: Button? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)

        setupUI()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val continueBtn = findViewById<Button>(R.id.continueBtn)
//        continueBtn.isEnabled = false

        val jsonString = readJsonFromAssets(this,"data.json")
        val items: List<AuditCategories>? = jsonString?.let { auditParser(it) }

        adapter = items?.let { AuditAdapter(it, this) }!!
        recyclerView.adapter = adapter

        continueBtn.setOnClickListener {
            openAddNewCPQIActivity()
        }
    }

    private fun openAddNewCPQIActivity() {
        startActivity(Intent(this, CategoriesActivity::class.java))
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("auditId", position)
        startActivity(intent)
    }
}
