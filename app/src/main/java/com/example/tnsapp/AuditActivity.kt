package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AuditAdapter
import com.example.tnsapp.data.AuditCategories
import com.example.tnsapp.parsers.auditParser
import com.example.tnsapp.parsers.readJsonFromAssets
import java.io.File

class AuditActivity : AppCompatActivity() {
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
        continueBtn.isEnabled = false

        val jsonString = readJsonFromAssets(this,"data.json")
        val items: List<AuditCategories>? = jsonString?.let { auditParser(it) }

        adapter = items?.let { AuditAdapter(it) }!!
        recyclerView.adapter = adapter

//        continueBtn.setOnClickListener {
//            if (lastClickedButton != null) {
//                when (lastClickedButton?.id) {
//                    R.id.cpqiBtn -> openAddNewCPQIActivity()
//                    R.id.cpsiBtn -> openAddNewCPSIActivity()
//                }
//            } else {
//                Toast.makeText(this, "Please select an audit option first", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun openAddNewCPQIActivity() {
        startActivity(Intent(this, AddNewActivity::class.java))
    }

    private fun openAddNewCPSIActivity() {
        startActivity(Intent(this, AddnewCpsiActivity::class.java))
    }
}
